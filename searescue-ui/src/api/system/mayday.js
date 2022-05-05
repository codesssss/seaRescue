import request from '@/utils/request'

// 查询
export function listMayday() {
  return request({
    url: 'mayday-management/getAll',
    method: 'get'
  })
}

export function listValidMayday() {
  return request({
    url: 'mayday-management/getValid',
    method: 'get'
  })
}

export function listPath() {
  return request({
    url: 'mayday-management/getPath',
    method: 'get'
  })
}

// 新增船只
export function addMayday(data) {
  return request({
    url: '/mayday-management/add',
    method: 'post',
    data: data
  })
}

// 修改船只类型
export function updateMayday(data) {
  return request({
    url: '/mayday-management/update',
    method: 'post',
    data: data
  })
}

// 删除船只类型
export function delMayday(maydayId) {
  return request({
    url: '/mayday-management/delete/' + maydayId,
    method: 'delete'
  })
}

// 获取船只类型
export function getMayday(maydayId) {
  return request({
    url: '/mayday-management/getMayday/' + maydayId,
    method: 'get'
  })
}

// 获取合适船只
export function getResShip(maydayId) {
  return request({
    url: '/mayday-management/getResShip/' + maydayId,
    method: 'get'
  })
}

// 导出船只类型
export function exportMayday(query) {
  return request({
    url: '/mayday-management/dict/type/export',
    method: 'get',
    params: query
  })
}

// 派遣船只类型
export function sendShip(data) {
  return request({
    url: '/mayday-management/send',
    method: 'post',
    data: data
  })
}

// 派遣船只类型
export function finishMayday(data) {
  return request({
    url: '/mayday-management/finish',
    method: 'post',
    data: data
  })
}

// 获取船只选择框列表
export function optionselect() {
  return request({
    url: '/mayday-management/dict/type/optionselect',
    method: 'get'
  })
}
