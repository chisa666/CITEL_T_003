import request from './request'

/** 导入数据文件 */
export function importData(filePath) {
  return request.post('/data/import', null, {
    params: { filePath }
  })
}

/** 获取数据状态 */
export function getDataStatus() {
  return request.get('/data/status')
}
