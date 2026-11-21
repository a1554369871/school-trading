import { supabase } from '@/lib/supabase'
import { ok, raise } from './request'

export async function getCategories() {
  const { data, error } = await supabase.rpc('get_categories')
  raise(error, '获取分类失败')
  return ok(data || [])
}

export async function createCategory(data) {
  const { data: row, error } = await supabase
    .from('categories')
    .insert({ category_name: data.categoryName })
    .select('id, category_name')
    .single()
  raise(error, '添加失败')
  return ok({ id: row.id, categoryName: row.category_name, productCount: 0 })
}

export async function updateCategory(id, data) {
  const { data: row, error } = await supabase
    .from('categories')
    .update({ category_name: data.categoryName })
    .eq('id', id)
    .select('id, category_name')
    .single()
  raise(error, '更新失败')
  return ok({ id: row.id, categoryName: row.category_name })
}

export async function deleteCategory(id) {
  const { error } = await supabase.from('categories').delete().eq('id', id)
  raise(error, '删除失败')
  return ok(null)
}
