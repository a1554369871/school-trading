import { supabase, syntheticEmail } from '@/lib/supabase'
import { ok, raise } from './request'

async function fetchMyProfile() {
  const { data, error } = await supabase.rpc('get_my_profile')
  raise(error, '获取用户信息失败')
  const profile = Array.isArray(data) ? data[0] : data
  if (!profile) throw new Error('登录状态已失效，请重新登录')
  return profile
}

/** 用户名 + 密码登录 */
export async function login({ username, password }) {
  const email = await syntheticEmail(username)
  const { data, error } = await supabase.auth.signInWithPassword({ email, password })
  if (error) {
    const message = /invalid login credentials/i.test(error.message)
      ? '用户名或密码错误'
      : error.message
    throw new Error(message)
  }
  if (!data.session) throw new Error('登录失败，请重试')

  const profile = await fetchMyProfile()
  if (profile.status === 0) {
    await supabase.auth.signOut()
    throw new Error('账号已被冻结')
  }
  return ok({ token: data.session.access_token })
}

/** 注册（内部合成邮箱，Supabase 控制台需关闭 Confirm email） */
export async function register({ username, password, nickname, phone, email }) {
  const name = String(username || '').trim()
  if (!name) throw new Error('请输入用户名')

  const { data: taken, error: takenError } = await supabase.rpc('is_username_taken', {
    p_username: name,
  })
  raise(takenError, '检查用户名失败')
  if (taken) throw new Error('用户名已存在')

  const authEmail = await syntheticEmail(name)
  const { data, error } = await supabase.auth.signUp({
    email: authEmail,
    password,
    options: {
      data: {
        username: name,
        nickname: nickname || name,
        phone: phone || null,
        email: email || null,
      },
    },
  })
  if (error) {
    if (/already registered/i.test(error.message)) throw new Error('用户名已存在')
    throw new Error(error.message)
  }
  if (!data.session) {
    throw new Error('注册失败：请在 Supabase 控制台关闭 Confirm email 后重试')
  }

  // 与旧后端保持一致：注册后不自动登录
  await supabase.auth.signOut()
  return ok({ username: name })
}

/** 当前用户资料 */
export async function getUserInfo() {
  const profile = await fetchMyProfile()
  return ok(profile)
}

/** 修改昵称 / 头像 / 手机号 / 联系邮箱 */
export async function updateUserInfo(data) {
  const { data: sessionData } = await supabase.auth.getSession()
  const userId = sessionData.session?.user?.id
  if (!userId) throw new Error('请先登录')

  const patch = {}
  if (data.nickname !== undefined) patch.nickname = data.nickname
  if (data.avatar !== undefined) patch.avatar = data.avatar
  if (data.phone !== undefined) patch.phone = data.phone
  if (data.email !== undefined) patch.email = data.email

  if (Object.keys(patch).length) {
    const { error } = await supabase.from('profiles').update(patch).eq('id', userId)
    raise(error, '保存失败')
  }
  const profile = await fetchMyProfile()
  return ok(profile)
}

/** 修改密码：先校验旧密码，再更新 */
export async function changePassword({ oldPassword, newPassword }) {
  const profile = await fetchMyProfile()
  const authEmail = await syntheticEmail(profile.username)

  const { error: verifyError } = await supabase.auth.signInWithPassword({
    email: authEmail,
    password: oldPassword,
  })
  if (verifyError) throw new Error('原密码不正确')

  const { error } = await supabase.auth.updateUser({ password: newPassword })
  raise(error, '修改密码失败')
  return ok(null)
}
