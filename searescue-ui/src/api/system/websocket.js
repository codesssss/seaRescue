import request from '@/utils/request'
import { praseStrEmpty } from "@/utils/ruoyi";

// 查询用户列表
export function notifyAll() {
  return request({
    url: '/websocket/test',
    method: 'get',
  })
}
