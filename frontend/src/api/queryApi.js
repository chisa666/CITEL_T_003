import request from './request'     // 导入配置好的 Axios 实例


/** 按年龄区间查询 */
export function queryByAge(data) {
  return request.post('/query/age', data)
}

/** 按里程区间查询 */
export function queryByMileage(data) {
  return request.post('/query/mileage', data)
}

/** 按时间区间查询 */
export function queryByTime(data) {
  return request.post('/query/time', data)
}
