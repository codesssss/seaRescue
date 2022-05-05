import request from '@/utils/request'

// 查询
export function listLighthouse() {
  return request({
    url: 'lighthouse-management/getAll',
    method: 'get'
  })
}

// 新增灯塔
export function addLighthouse(data) {
  return request({
    url: '/lighthouse-management/add',
    method: 'post',
    data: data
  })
}

// 修改灯塔类型
export function updateLighthouse(data) {
  return request({
    url: '/lighthouse-management/update',
    method: 'post',
    data: data
  })
}

// 删除灯塔类型
export function delLighthouse(lighthouseId) {
  return request({
    url: '/lighthouse-management/delete/' + lighthouseId,
    method: 'delete'
  })
}

// 获取灯塔类型
export function getLighthouse(lighthouseId) {
  return request({
    url: '/lighthouse-management/getLighthouse/' + lighthouseId,
    method: 'get'
  })
}

// 导出灯塔类型
export function exportLighthouse(query) {
  return request({
    url: '/lighthouse-management/dict/type/export',
    method: 'get',
    params: query
  })
}

// 获取灯塔选择框列表
export function optionselect() {
  return request({
    url: '/lighthouse-management/dict/type/optionselect',
    method: 'get'
  })
}
