import { supabase, currentUserId } from '@/lib/supabase'
import { ok, raise } from './request'

export async function addComment(productId, data = {}) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')

  const { error } = await supabase.from('comments').insert({
    product_id: Number(productId),
    parent_id: data.parentId || null,
    commenter_id: uid,
    replier_id: data.replierId || null,
    content: data.content,
  })
  raise(error, '评论失败')
  return ok(null)
}

export async function getComments(productId) {
  const { data, error } = await supabase.rpc('get_product_comments', {
    p_product_id: Number(productId),
  })
  raise(error, '获取评论失败')
  return ok(data || [])
}

export async function deleteComment(id) {
  const { error } = await supabase.from('comments').delete().eq('id', id)
  raise(error, '删除失败')
  return ok(null)
}
