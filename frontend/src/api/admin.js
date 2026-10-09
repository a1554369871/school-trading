import request from './request'

// 统计
export const getOverview = () => request.get('/admin/stats/overview')
export const getUserTradeStats = () => request.get('/admin/stats/user-trade')
export const getActiveUsers = () => request.get('/admin/stats/active-users')
export const getMonthlyStats = () => request.get('/admin/stats/monthly')
export const getSellerRevenue = () => request.get('/admin/stats/seller-revenue')

// 用户管理
export const getUsers = (params) => request.get('/admin/users', { params })
export const updateUserRole = (id, role) => request.put(`/admin/users/${id}/role`, { role })
export const freezeUser = (id) => request.put(`/admin/users/${id}/freeze`)
export const unfreezeUser = (id) => request.put(`/admin/users/${id}/unfreeze`)

// 公告管理
export const getAnnouncements = (params) => request.get('/admin/announcements', { params })
export const createAnnouncement = (data) => request.post('/admin/announcements', data)
export const updateAnnouncement = (id, data) => request.put(`/admin/announcements/${id}`, data)
export const deleteAnnouncement = (id) => request.delete(`/admin/announcements/${id}`)

// 评论管理
export const getAdminComments = (params) => request.get('/admin/comments', { params })
export const deleteAdminComment = (id) => request.delete(`/admin/comments/${id}`)

// 商品管理
export const updateProductStatus = (id, status) => request.put(`/admin/products/${id}/status`, { status })
export const deleteAdminProduct = (id) => request.delete(`/admin/products/${id}`)
