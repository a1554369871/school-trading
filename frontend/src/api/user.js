import { supabase, currentUserId } from '@/lib/supabase'
import { ok, raise } from './request'

export { updateUserInfo, changePassword } from './auth'

/** 上传图片到 Supabase Storage，返回 { url, originalFilename, size } */
export async function uploadFile(file) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')
  if (!file) throw new Error('请选择文件')

  const ext = (file.name?.split('.').pop() || 'png').toLowerCase()
  const path = `${uid}/${crypto.randomUUID()}.${ext}`

  const { error } = await supabase.storage.from('images').upload(path, file, {
    cacheControl: '31536000',
    contentType: file.type || undefined,
    upsert: false,
  })
  raise(error, '上传失败')

  const { data } = supabase.storage.from('images').getPublicUrl(path)
  return ok({
    url: data.publicUrl,
    originalFilename: file.name,
    size: file.size,
  })
}
