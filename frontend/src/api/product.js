import request from './request'

// 获取商品列表（分页+筛选）
export function getProducts(params) {
  return request.get('/products', { params })
}

// 全文搜索
export function searchProducts(params) {
  return request.get('/products/search', { params })
}

// 获取商品详情
export function getProductDetail(id) {
  return request.get(`/products/${id}`)
}

// 发布商品
export function publishProduct(data) {
  return request.post('/products', data)
}

// 更新商品
export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

// 更新商品状态（上架/下架）
export function updateProductStatus(id, status) {
  return request.put(`/products/${id}/status`, { status })
}

// 删除商品
export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}

// 商品互动（点赞/收藏/想要）
export function interactProduct(id, type) {
  return request.post(`/products/${id}/interact?type=${type}`)
}

// 我的商品
export function getMyProducts(params) {
  return request.get('/products/my', { params })
}

// 我的收藏
export function getMyFavorites(params) {
  return request.get('/products/favorites', { params })
}

// 获取分类统计
export function getCategoryStats() {
  return request.get('/products/stats/categories')
}

// 获取所有分类
export function getCategories() {
  return request.get('/categories')
}
