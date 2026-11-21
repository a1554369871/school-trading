import { supabase } from '@/lib/supabase'
import { ok, pageResult, raise, readPage, likeKeyword } from './request'

async function singleStat(rpc, fallback) {
  const { data, error } = await supabase.rpc(rpc)
  raise(error, fallback)
  return Array.isArray(data) ? data[0] || null : data
}

export async function getOverview() {
  return ok((await singleStat('admin_overview', '获取统计失败')) || {})
}

export async function getUserTradeStats() {
  const { data, error } = await supabase.rpc('admin_user_trade_stats')
  raise(error, '获取统计失败')
  return ok(data || [])
}

export async function getActiveUsers() {
  const { data, error } = await supabase.rpc('admin_active_users')
  raise(error, '获取统计失败')
  return ok(data || [])
}

export async function getMonthlyStats() {
  const { data, error } = await supabase.rpc('admin_monthly_stats')
  raise(error, '获取统计失败')
  return ok(data || [])
}

export async function getSellerRevenue() {
  const { data, error } = await supabase.rpc('admin_seller_revenue')
  raise(error, '获取统计失败')
  return ok(data || [])
}

export async function getUsers(params = {}) {
  const { pageNum, pageSize, from, to } = readPage(params, 10)
  let query = supabase
    .from('profiles')
    .select('id, username, nickname, avatar, role, status, register_time', { count: 'exact' })
    .order('register_time', { ascending: false })
    .range(from, to)

  const keyword = likeKeyword(params.keyword)
  if (keyword) query = query.or(`username.ilike.%${keyword}%,nickname.ilike.%${keyword}%`)

  const { data, error, count } = await query
  raise(error, '获取用户失败')
  const list = (data || []).map((row) => ({
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    avatar: row.avatar,
    role: row.role,
    status: row.status,
    registerTime: row.register_time,
  }))
  return pageResult(list, count || 0, pageNum, pageSize)
}

export async function updateUserRole(id, role) {
  const { error } = await supabase.rpc('admin_set_user_role', {
    p_user_id: id,
    p_role: Number(role),
  })
  raise(error, '操作失败')
  return ok(null)
}

export async function freezeUser(id) {
  const { error } = await supabase.rpc('admin_set_user_status', {
    p_user_id: id,
    p_status: 0,
  })
  raise(error, '操作失败')
  return ok(null)
}

export async function unfreezeUser(id) {
  const { error } = await supabase.rpc('admin_set_user_status', {
    p_user_id: id,
    p_status: 1,
  })
  raise(error, '操作失败')
  return ok(null)
}

function mapAnnouncement(row) {
  return {
    id: row.id,
    publisherId: row.publisher_id,
    title: row.title,
    content: row.content,
    status: row.status,
    publishTime: row.publish_time,
  }
}

export async function getAnnouncements(params = {}) {
  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('announcements')
    .select('id, publisher_id, title, content, status, publish_time', { count: 'exact' })
    .order('publish_time', { ascending: false })
    .range(from, to)
  raise(error, '获取公告失败')
  return pageResult((data || []).map(mapAnnouncement), count || 0, pageNum, pageSize)
}

export async function createAnnouncement(data) {
  const { data: row, error } = await supabase.rpc('admin_create_announcement', {
    p_title: data.title,
    p_content: data.content,
    p_status: Number(data.status) || 0,
  })
  raise(error, '发布失败')
  return ok(mapAnnouncement(row))
}

export async function updateAnnouncement(id, data) {
  const { data: row, error } = await supabase
    .from('announcements')
    .update({ title: data.title, content: data.content, status: Number(data.status) || 0 })
    .eq('id', id)
    .select('id, publisher_id, title, content, status, publish_time')
    .single()
  raise(error, '更新失败')
  return ok(mapAnnouncement(row))
}

export async function deleteAnnouncement(id) {
  const { error } = await supabase.rpc('admin_delete_announcement', { p_id: Number(id) })
  raise(error, '删除失败')
  return ok(null)
}

export async function getAdminComments(params = {}) {
  const { pageNum, pageSize, from, to } = readPage(params, 10)
  const { data, error, count } = await supabase
    .from('comments')
    .select(
      `id, content, comment_time, parent_id, commenter_id, product_id,
       profiles:profiles!comments_commenter_id_fkey ( username, nickname ),
       products:products!comments_product_id_fkey ( product_name )`,
      { count: 'exact' },
    )
    .order('comment_time', { ascending: false })
    .range(from, to)
  raise(error, '获取评论失败')
  const list = (data || []).map((row) => ({
    id: row.id,
    content: row.content,
    createTime: row.comment_time,
    parentId: row.parent_id,
    commenterId: row.commenter_id,
    productId: row.product_id,
    username: row.profiles?.username || '',
    nickname: row.profiles?.nickname || '',
    productName: row.products?.product_name || '',
  }))
  return pageResult(list, count || 0, pageNum, pageSize)
}

export async function deleteAdminComment(id) {
  const { error } = await supabase.from('comments').delete().eq('id', id)
  raise(error, '删除失败')
  return ok(null)
}

export async function updateProductStatus(id, status) {
  const { error } = await supabase.from('products').update({ status }).eq('id', id)
  raise(error, '操作失败')
  return ok(null)
}

export async function deleteAdminProduct(id) {
  const { error } = await supabase.from('products').update({ status: 0 }).eq('id', id)
  raise(error, '操作失败')
  return ok(null)
}
