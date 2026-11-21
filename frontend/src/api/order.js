import { supabase, currentUserId } from '@/lib/supabase'
import { ok, pageResult, raise, readPage } from './request'

const BUY_SELECT = `
  id, order_no, deal_price, status, create_time, pay_time, ship_time,
  confirm_time, cancel_time, delivery_method, remark, buyer_id, seller_id, product_id,
  products:products!orders_product_id_fkey ( product_name, product_image ),
  seller:profiles!orders_seller_id_fkey ( nickname, avatar )
`

const SELL_SELECT = `
  id, order_no, deal_price, status, create_time, pay_time, ship_time,
  confirm_time, cancel_time, delivery_method, remark, buyer_id, seller_id, product_id,
  products:products!orders_product_id_fkey ( product_name, product_image ),
  buyer:profiles!orders_buyer_id_fkey ( nickname, avatar )
`

const ADMIN_SELECT = `
  id, order_no, deal_price, status, create_time, delivery_method, remark,
  buyer_id, seller_id, product_id,
  products:products!orders_product_id_fkey ( product_name, product_image ),
  buyer:profiles!orders_buyer_id_fkey ( nickname, avatar ),
  seller:profiles!orders_seller_id_fkey ( nickname, avatar )
`

function mapOrder(row, side) {
  const base = {
    id: row.id,
    orderNo: row.order_no,
    dealPrice: Number(row.deal_price),
    status: row.status,
    createTime: row.create_time,
    payTime: row.pay_time,
    shipTime: row.ship_time,
    confirmTime: row.confirm_time,
    cancelTime: row.cancel_time,
    deliveryMethod: row.delivery_method,
    remark: row.remark,
    buyerId: row.buyer_id,
    sellerId: row.seller_id,
    productId: row.product_id,
    productName: row.products?.product_name || '',
    productImage: row.products?.product_image || '',
  }
  if (side === 'buy') {
    base.sellerNickname = row.seller?.nickname || ''
    base.sellerAvatar = row.seller?.avatar || ''
  } else if (side === 'sell') {
    base.buyerNickname = row.buyer?.nickname || ''
    base.buyerAvatar = row.buyer?.avatar || ''
  } else {
    base.buyerNickname = row.buyer?.nickname || ''
    base.buyerAvatar = row.buyer?.avatar || ''
    base.sellerNickname = row.seller?.nickname || ''
    base.sellerAvatar = row.seller?.avatar || ''
  }
  return base
}

export async function createOrder(data) {
  const { data: order, error } = await supabase.rpc('create_order', {
    p_product_id: Number(data.productId),
    p_address_id: data.addressId ? Number(data.addressId) : null,
    p_delivery_method: data.deliveryMethod || '面交',
    p_remark: data.remark || '',
  })
  raise(error, '下单失败')
  return ok(order)
}

export async function payOrder(id) {
  const { error } = await supabase.rpc('pay_order', { p_order_id: Number(id) })
  raise(error, '支付失败')
  return ok(null)
}

export async function shipOrder(id) {
  const { error } = await supabase.rpc('ship_order', { p_order_id: Number(id) })
  raise(error, '发货失败')
  return ok(null)
}

export async function confirmOrder(id) {
  const { error } = await supabase.rpc('confirm_order', { p_order_id: Number(id) })
  raise(error, '确认收货失败')
  return ok(null)
}

export async function cancelOrder(id) {
  const { error } = await supabase.rpc('cancel_order', { p_order_id: Number(id) })
  raise(error, '取消订单失败')
  return ok(null)
}

export async function getBuyOrders(params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 1000)

  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('orders')
    .select(BUY_SELECT, { count: 'exact' })
    .eq('buyer_id', uid)
    .order('create_time', { ascending: false })
    .range(from, to)
  raise(error, '获取订单失败')
  return pageResult((data || []).map((row) => mapOrder(row, 'buy')), count || 0, pageNum, pageSize)
}

export async function getSellOrders(params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 1000)

  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('orders')
    .select(SELL_SELECT, { count: 'exact' })
    .eq('seller_id', uid)
    .order('create_time', { ascending: false })
    .range(from, to)
  raise(error, '获取订单失败')
  return pageResult((data || []).map((row) => mapOrder(row, 'sell')), count || 0, pageNum, pageSize)
}

/** 管理端一次取回全部订单，页面在前端筛选/分页 */
export async function getAdminOrders(params = {}) {
  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('orders')
    .select(ADMIN_SELECT, { count: 'exact' })
    .order('create_time', { ascending: false })
    .range(from, to)
  raise(error, '获取订单失败')
  return pageResult((data || []).map((row) => mapOrder(row, 'admin')), count || 0, pageNum, pageSize)
}

export async function getOrderDetail(id) {
  const { data, error } = await supabase
    .from('orders')
    .select(ADMIN_SELECT)
    .eq('id', id)
    .single()
  raise(error, '订单不存在')
  return ok(mapOrder(data, 'admin'))
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
