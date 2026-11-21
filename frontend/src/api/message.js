import { supabase, currentUserId } from '@/lib/supabase'
import { ok, pageResult, raise, readPage } from './request'

export async function sendMessage({ receiverId, content }) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')
  if (String(receiverId) === String(uid)) throw new Error('不能给自己发送私信')

  const { error } = await supabase.from('messages').insert({
    sender_id: uid,
    receiver_id: receiverId,
    content,
    is_read: 0,
  })
  raise(error, '发送失败')
  return ok(null)
}

export async function getConversation(otherId, params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 1000)

  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('messages')
    .select(
      `*, sender:profiles!messages_sender_id_fkey ( nickname, avatar ), receiver:profiles!messages_receiver_id_fkey ( nickname, avatar )`,
      { count: 'exact' },
    )
    .or(
      `and(sender_id.eq.${uid},receiver_id.eq.${otherId}),and(sender_id.eq.${otherId},receiver_id.eq.${uid})`,
    )
    .order('send_time', { ascending: true })
    .range(from, to)
  raise(error, '获取对话失败')

  const list = (data || []).map((row) => ({
    id: row.id,
    sender_id: row.sender_id,
    receiver_id: row.receiver_id,
    content: row.content,
    send_time: row.send_time,
    is_read: row.is_read,
    sender_nickname: row.sender?.nickname || '',
    sender_avatar: row.sender?.avatar || '',
    receiver_nickname: row.receiver?.nickname || '',
  }))
  return pageResult(list, count || 0, pageNum, pageSize)
}

export async function getRecentChats() {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, 100)

  const { data, error } = await supabase.rpc('get_recent_chats')
  raise(error, '获取聊天列表失败')
  const list = (data || []).map((row) => ({
    id: row.id,
    sender_id: row.sender_id,
    receiver_id: row.receiver_id,
    content: row.content,
    send_time: row.send_time,
    is_read: row.is_read,
    other_nickname: row.other_nickname || '',
    other_avatar: row.other_avatar || '',
    other_id: row.other_id,
    unread_count: Number(row.unread_count || 0),
  }))
  return pageResult(list, list.length, 1, 100)
}

export async function markConversationRead(otherId) {
  const { error } = await supabase.rpc('mark_conversation_read', { p_other_id: otherId })
  raise(error, '标记已读失败')
  return ok(null)
}

function mapNotification(row) {
  return {
    id: row.id,
    receiverId: row.receiver_id,
    title: row.title,
    content: row.content,
    isRead: row.is_read,
    createTime: row.create_time,
  }
}

export async function getNotifications(params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 10)

  const { pageNum, pageSize, from, to } = readPage(params, 10)
  const { data, error, count } = await supabase
    .from('notifications')
    .select('id, receiver_id, title, content, is_read, create_time', { count: 'exact' })
    .eq('receiver_id', uid)
    .order('create_time', { ascending: false })
    .range(from, to)
  raise(error, '获取通知失败')
  return pageResult((data || []).map(mapNotification), count || 0, pageNum, pageSize)
}

export async function getUnreadCount() {
  const uid = await currentUserId()
  if (!uid) return ok(0)

  const { count, error } = await supabase
    .from('notifications')
    .select('id', { count: 'exact', head: true })
    .eq('receiver_id', uid)
    .eq('is_read', 0)
  raise(error, '获取未读数失败')
  return ok(count || 0)
}

export async function markAsRead(id) {
  const { error } = await supabase.from('notifications').update({ is_read: 1 }).eq('id', id)
  raise(error, '操作失败')
  return ok(null)
}

export async function markAllAsRead() {
  const uid = await currentUserId()
  if (!uid) return ok(null)
  const { error } = await supabase
    .from('notifications')
    .update({ is_read: 1 })
    .eq('receiver_id', uid)
    .eq('is_read', 0)
  raise(error, '操作失败')
  return ok(null)
}

export async function deleteNotification(id) {
  const { error } = await supabase.from('notifications').delete().eq('id', id)
  raise(error, '删除失败')
  return ok(null)
}
