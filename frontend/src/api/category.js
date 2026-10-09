import request from './request'

// 获取分类列表
export const getCategories = () => request.get('/categories')

// 创建分类
export const createCategory = (data) => request.post('/categories', data)

// 删除分类
export const deleteCategory = (id) => request.delete(`/categories/${id}`)

// 更新分类
export const updateCategory = (id, data) => request.put(`/categories/${id}`, data)
