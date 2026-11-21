import { supabase, currentUserId } from '@/lib/supabase'
import { ok, pageResult, raise, readPage, likeKeyword } from './request'

const PRODUCT_SELECT = `
  id, user_id, product_name, product_image, publish_time, price, description,
  category_id, item_condition, status, version, view_count,
  categories:categories!products_category_id_fkey ( category_name )
`

export function mapProduct(row) {
  if (!row) return null
  return {
    id: row.id,
    userId: row.user_id,
    productName: row.product_name,
    productImage: row.product_image,
    publishTime: row.publish_time,
    price: Number(row.price),
    description: row.description,
    categoryId: row.category_id,
    categoryName: row.categories?.category_name || '',
    condition: row.item_condition,
    status: row.status,
    version: row.version,
    viewCount: row.view_count,
  }
}

async function queryProducts(params = {}) {
  const { pageNum, pageSize, from, to } = readPage(params, 10)
  let query = supabase
    .from('products')
    .select(PRODUCT_SELECT, { count: 'exact' })
    .range(from, to)

  // 管理端传 admin: true 时不在服务端过滤状态（RLS 已限制普通用户只能看自己的）
  if (!params.admin) query = query.eq('status', 1)

  const keyword = likeKeyword(params.keyword)
  if (keyword) {
    query = query.or(`product_name.ilike.%${keyword}%,description.ilike.%${keyword}%`)
  }
  if (params.categoryId) query = query.eq('category_id', params.categoryId)
  if (params.condition) query = query.eq('item_condition', params.condition)
  if (params.minPrice !== undefined && params.minPrice !== '' && params.minPrice !== null) {
    query = query.gte('price', params.minPrice)
  }
  if (params.maxPrice !== undefined && params.maxPrice !== '' && params.maxPrice !== null) {
    query = query.lte('price', params.maxPrice)
  }

  const sortColumn = params.sortBy === 'price' ? 'price' : 'publish_time'
  const ascending = params.sortOrder === 'asc'
  query = query.order(sortColumn, { ascending })

  const { data, error, count } = await query
  raise(error, '获取商品失败')
  return pageResult((data || []).map(mapProduct), count || 0, pageNum, pageSize)
}

export function getProducts(params = {}) {
  return queryProducts(params)
}

export function searchProducts(params = {}) {
  return queryProducts(params)
}

/** 商品详情：商品 + 卖家 + 互动计数 + 当前用户状态 */
export async function getProductDetail(id) {
  await supabase.rpc('increment_view', { p_product_id: Number(id) })

  const { data: row, error } = await supabase
    .from('products')
    .select(PRODUCT_SELECT)
    .eq('id', id)
    .single()
  raise(error, '商品不存在')

  const product = mapProduct(row)

  const [{ data: seller }, { data: counts }, uid] = await Promise.all([
    supabase
      .from('profiles')
      .select('id, nickname, avatar')
      .eq('id', row.user_id)
      .maybeSingle(),
    supabase.rpc('product_interaction_counts', { p_product_id: Number(id) }),
    currentUserId(),
  ])

  const stat = (Array.isArray(counts) ? counts[0] : counts) || {}
  const interactions = {
    like_count: Number(stat.like_count || 0),
    favorite_count: Number(stat.favorite_count || 0),
    want_count: Number(stat.want_count || 0),
  }

  let userLiked = false
  let userFavorited = false
  let userWanted = false
  if (uid) {
    const { data: mine } = await supabase
      .from('interactions')
      .select('type')
      .eq('user_id', uid)
      .eq('product_id', id)
    ;(mine || []).forEach((item) => {
      if (item.type === 'like') userLiked = true
      if (item.type === 'favorite') userFavorited = true
      if (item.type === 'want') userWanted = true
    })
  }

  return ok({
    product: { ...product, viewCount: (product.viewCount || 0) + 1 },
    seller: seller ? { id: seller.id, nickname: seller.nickname, avatar: seller.avatar } : null,
    interactions,
    userLiked,
    userFavorited,
    userWanted,
  })
}

export async function publishProduct(data) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')

  const { data: row, error } = await supabase
    .from('products')
    .insert({
      user_id: uid,
      product_name: data.productName,
      product_image: data.productImage || null,
      price: Number(data.price) || 0,
      description: data.description || '',
      category_id: data.categoryId || null,
      item_condition: data.condition || null,
      status: 1,
    })
    .select(PRODUCT_SELECT)
    .single()
  raise(error, '发布失败')
  return ok(mapProduct(row))
}

export async function updateProduct(id, data) {
  const uid = await currentUserId()
  if (!uid) throw new Error('请先登录')

  const patch = {}
  if (data.productName !== undefined) patch.product_name = data.productName
  if (data.productImage !== undefined) patch.product_image = data.productImage
  if (data.price !== undefined) patch.price = Number(data.price)
  if (data.description !== undefined) patch.description = data.description
  if (data.categoryId !== undefined) patch.category_id = data.categoryId
  if (data.condition !== undefined) patch.item_condition = data.condition

  const { data: row, error } = await supabase
    .from('products')
    .update(patch)
    .eq('id', id)
    .eq('user_id', uid)
    .select(PRODUCT_SELECT)
    .maybeSingle()
  raise(error, '修改失败')
  if (!row) throw new Error('商品不存在或无权操作')
  return ok(mapProduct(row))
}

export async function updateProductStatus(id, status) {
  const { error } = await supabase.from('products').update({ status }).eq('id', id)
  raise(error, '操作失败')
  return ok(null)
}

/** 旧后端的删除是逻辑下架 */
export async function deleteProduct(id) {
  const { error } = await supabase.from('products').update({ status: 0 }).eq('id', id)
  raise(error, '操作失败')
  return ok(null)
}

export async function interactProduct(id, type) {
  const { data, error } = await supabase.rpc('toggle_interaction', {
    p_product_id: Number(id),
    p_type: type,
  })
  raise(error, '操作失败')
  return ok(Boolean(data))
}

export async function getMyProducts(params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 1000)

  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const { data, error, count } = await supabase
    .from('products')
    .select(PRODUCT_SELECT, { count: 'exact' })
    .eq('user_id', uid)
    .order('publish_time', { ascending: false })
    .range(from, to)
  raise(error, '获取商品失败')
  return pageResult((data || []).map(mapProduct), count || 0, pageNum, pageSize)
}

export async function getMyFavorites(params = {}) {
  const uid = await currentUserId()
  if (!uid) return pageResult([], 0, 1, params.pageSize || 1000)

  const { data: favorites, error: favError } = await supabase
    .from('interactions')
    .select('product_id, create_time')
    .eq('user_id', uid)
    .eq('type', 'favorite')
    .order('create_time', { ascending: false })
  raise(favError, '获取收藏失败')
  if (!favorites?.length) return pageResult([], 0, 1, params.pageSize || 1000)

  const { pageNum, pageSize, from, to } = readPage({ pageSize: 1000, ...params }, 1000)
  const ids = favorites.map((item) => item.product_id)
  const { data, error, count } = await supabase
    .from('products')
    .select(PRODUCT_SELECT, { count: 'exact' })
    .in('id', ids)
    .order('publish_time', { ascending: false })
    .range(from, to)
  raise(error, '获取收藏失败')
  return pageResult((data || []).map(mapProduct), count || 0, pageNum, pageSize)
}

export async function getCategoryStats() {
  const { data, error } = await supabase.rpc('category_stats')
  raise(error, '获取分类统计失败')
  return ok(data || [])
}

export async function getCategories() {
  const { data, error } = await supabase.rpc('get_categories')
  raise(error, '获取分类失败')
  return ok(data || [])
}

export async function getFavoriteStatus(productId) {
  const uid = await currentUserId()
  if (!uid) return ok(false)
  const { data, error } = await supabase
    .from('interactions')
    .select('id')
    .eq('user_id', uid)
    .eq('product_id', productId)
    .eq('type', 'favorite')
    .maybeSingle()
  raise(error, '获取收藏状态失败')
  return ok(Boolean(data))
}
