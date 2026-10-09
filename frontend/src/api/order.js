import request from './request'

// 创建订单
export function createOrder(data) {
  return request.post('/orders', data)
}

// 支付订单
export function payOrder(id) {
  return request.put(`/orders/${id}/pay`)
}

// 发货
export function shipOrder(id) {
  return request.put(`/orders/${id}/ship`)
}

// 确认收货
export function confirmOrder(id) {
  return request.put(`/orders/${id}/confirm`)
}

// 取消订单
export function cancelOrder(id) {
  return request.put(`/orders/${id}/cancel`)
}

// 获取我的购买订单
export function getBuyOrders(params) {
  return request.get('/orders/buy', { params })
}

// 获取我的销售订单
export function getSellOrders(params) {
  return request.get('/orders/sell', { params })
}

// 管理员获取所有订单
export function getAdminOrders(params) {
  return request.get('/admin/orders', { params })
}
