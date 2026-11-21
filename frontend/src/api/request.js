/**
 * Supabase 版请求层。保留旧后端 { code, message, data } 的返回结构，
 * 这样所有页面里的 res.data / res.data.list 都不需要改。
 */

export function ok(data = null) {
  return { code: 200, message: 'success', data }
}

export function pageResult(list, total = 0, pageNum = 1, pageSize = 10) {
  const safeSize = Number(pageSize) || 10
  return ok({
    list: list || [],
    total: Number(total) || 0,
    pageNum: Number(pageNum) || 1,
    pageSize: safeSize,
    pages: Math.ceil((Number(total) || 0) / safeSize),
  })
}

export function fail(message = '请求失败') {
  const error = new Error(message)
  error.code = 500
  throw error
}

const MESSAGES = {
  23505: '数据已存在',
  23503: '关联数据不存在或仍被使用',
  42501: '没有操作权限',
  PGRST116: '数据不存在',
}

/** 把 supabase-js 的 error 转成中文 Error */
export function raise(error, fallback = '请求失败') {
  if (!error) return
  const code = error.code || ''
  const text = MESSAGES[code] || error.message || fallback
  const wrapped = new Error(text)
  wrapped.code = code
  wrapped.details = error.details
  wrapped.hint = error.hint
  throw wrapped
}

/** 统一分页参数：同时兼容旧的 page 和 pageNum */
export function readPage(params = {}, defaultSize = 10) {
  const pageNum = Math.max(1, Number(params.pageNum || params.page || 1) || 1)
  const pageSize = Math.max(1, Number(params.pageSize || defaultSize) || defaultSize)
  return {
    pageNum,
    pageSize,
    from: (pageNum - 1) * pageSize,
    to: pageNum * pageSize - 1,
  }
}

/** 把用户输入转成安全的 ilike 关键字，避免破坏 PostgREST or 表达式 */
export function likeKeyword(keyword) {
  return String(keyword || '')
    .trim()
    .replace(/[,()%]/g, ' ')
}

export default { ok, pageResult, fail, raise, readPage, likeKeyword }
