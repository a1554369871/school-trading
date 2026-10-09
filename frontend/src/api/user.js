import request from './request'

// 更新用户信息
export function updateUserInfo(data) {
  return request.put('/users/me', data)
}

// 修改密码
export function changePassword(data) {
  return request.put('/users/me/password', data)
}

// 上传文件
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/users/upload', formData)
}
