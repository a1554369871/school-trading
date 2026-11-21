import { supabase, currentUserId } from '@/lib/supabase'
import { ok, raise } from './request'

function mapAddress(row) {
  return {
    id: row.id,
    userId: row.user_id,
    consignee: row.consignee,
    phone: row.phone,
    address: row.address,
    isDefault: row.is_default,
  }
}

export async function getAddresses() {
  const uid = await currentUserId()
  if (!uid) return ok([])

  const { data, error } = await supabase
    .from('addresses')
    .select('id, user_id, consignee, phone, address, is_default')
    .eq('user_id', uid)
    .order('is_default', { ascending: false })
    .order('id', { ascending: false })
  raise(error, '获取地址失败')
  return ok((data || []).map(mapAddress))
}

export async function getAddressDetail(id) {
  const { data, error } = await supabase
    .from('addresses')
    .select('id, user_id, consignee, phone, address, is_default')
    .eq('id', id)
    .single()
  raise(error, '地址不存在')
  return ok(mapAddress(data))
}

export async function addAddress(payload) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')

  if (payload.isDefault) {
    const { error: clearError } = await supabase
      .from('addresses')
      .update({ is_default: 0 })
      .eq('user_id', uid)
    raise(clearError, '保存失败')
  }

  const { data, error } = await supabase
    .from('addresses')
    .insert({
      user_id: uid,
      consignee: payload.consignee,
      phone: payload.phone,
      address: payload.address,
      is_default: payload.isDefault ? 1 : 0,
    })
    .select('id, user_id, consignee, phone, address, is_default')
    .single()
  raise(error, '保存失败')
  return ok(mapAddress(data))
}

export async function updateAddress(id, payload) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')

  if (payload.isDefault) {
    const { error: clearError } = await supabase
      .from('addresses')
      .update({ is_default: 0 })
      .eq('user_id', uid)
    raise(clearError, '保存失败')
  }

  const { data, error } = await supabase
    .from('addresses')
    .update({
      consignee: payload.consignee,
      phone: payload.phone,
      address: payload.address,
      is_default: payload.isDefault ? 1 : 0,
    })
    .eq('id', id)
    .eq('user_id', uid)
    .select('id, user_id, consignee, phone, address, is_default')
    .maybeSingle()
  raise(error, '保存失败')
  if (!data) throw new Error('地址不存在或无权操作')
  return ok(mapAddress(data))
}

export async function deleteAddress(id) {
  const { error } = await supabase.from('addresses').delete().eq('id', id)
  raise(error, '删除失败')
  return ok(null)
}
