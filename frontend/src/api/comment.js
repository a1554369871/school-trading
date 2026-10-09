import request from './request'

export const addComment = (productId, data) => request.post('/comments', { ...data, productId })
export const getComments = (productId) => request.get(`/comments/product/${productId}`)
export const deleteComment = (id) => request.delete(`/comments/${id}`)
