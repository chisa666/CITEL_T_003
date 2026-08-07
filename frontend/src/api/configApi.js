import request from './request'

/** 获取所有区间配置 */
export function listConfigs() {
  return request.get('/config/list')
}

/** 按类型获取区间配置 */
export function listConfigsByType(queryType) {
  return request.get('/config/list/' + queryType)
}

/** 保存区间配置 */
export function saveConfig(data) {
  return request.post('/config/save', data)
}

/** 删除区间配置 */
export function deleteConfig(id) {
  return request.delete('/config/' + id)
}
