import { createClient } from '@supabase/supabase-js'

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY

if (!supabaseUrl || !supabaseAnonKey) {
  console.warn(
    '[supabase] 缺少 VITE_SUPABASE_URL / VITE_SUPABASE_ANON_KEY，请在 .env.local 或 GitHub Actions Variables 中配置',
  )
}

export const supabase = createClient(
  supabaseUrl || 'http://127.0.0.1:54321',
  supabaseAnonKey || 'public-anon-key',
  {
    auth: {
      persistSession: true,
      autoRefreshToken: true,
      // 使用 hash 路由，关闭 URL 中的 token 解析，避免和 #/route 冲突
      detectSessionInUrl: false,
    },
  },
)

/** 当前登录用户 id（未登录返回 null） */
export async function currentUserId() {
  const { data } = await supabase.auth.getSession()
  return data.session?.user?.id || null
}

/** 用户名 → 内部合成邮箱（大小写不敏感，不暴露真实邮箱） */
export async function syntheticEmail(username) {
  const normalized = String(username || '').trim().toLowerCase()
  const bytes = new TextEncoder().encode(normalized)
  if (globalThis.crypto?.subtle) {
    const digest = await crypto.subtle.digest('SHA-256', bytes)
    const hex = Array.from(new Uint8Array(digest))
      .map((b) => b.toString(16).padStart(2, '0'))
      .join('')
    return `u${hex.slice(0, 40)}@users.example.com`
  }
  // 极老浏览器兜底
  let hash = 0
  for (let i = 0; i < normalized.length; i += 1) {
    hash = (hash * 31 + normalized.charCodeAt(i)) >>> 0
  }
  return `u${hash.toString(16).padStart(8, '0')}@users.example.com`
}
