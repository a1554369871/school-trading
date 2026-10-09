import request from './request'

export const sendMessage = (data) => request.post('/messages/send', data)
export const getConversation = (userId, params) => request.get(`/messages/conversation/${userId}`, { params })
export const getRecentChats = (params) => request.get('/messages/recent', { params })
export const markConversationRead = (userId) => request.put(`/messages/conversation/${userId}/read`)
export const getNotifications = (params) => request.get('/messages/notifications', { params })
export const getUnreadCount = () => request.get('/messages/notifications/unread-count')
export const markAsRead = (id) => request.put(`/messages/notifications/${id}/read`)
export const markAllAsRead = () => request.put('/messages/notifications/read-all')
export const deleteNotification = (id) => request.delete(`/messages/notifications/${id}`)
