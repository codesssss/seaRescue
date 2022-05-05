import request from '@/utils/request'

// 查询
export function listShip() {
  return request({
    url: 'ship-management/getAll',
    method: 'get'
  })
}

// 新增船只
export function addShip(data) {
  return request({
    url: '/ship-management/add',
    method: 'post',
    data: data
  })
}

// 修改船只类型
export function updateShip(data) {
  return request({
    url: '/ship-management/update',
    method: 'post',
    data: data
  })
}

// 删除船只类型
export function delShip(shipId) {
  return request({
    url: '/ship-management/delete/' + shipId,
    method: 'delete'
  })
}

// 获取船只类型
export function getShip(shipId) {
  return request({
    url: '/ship-management/getShip/' + shipId,
    method: 'get'
  })
}

// 导出船只类型
export function exportShip(query) {
  return request({
    url: '/ship-management/dict/type/export',
    method: 'get',
    params: query
  })
}

// 获取船只选择框列表
export function optionselect() {
  return request({
    url: '/ship-management/dict/type/optionselect',
    method: 'get'
  })
}
