import request from '@/libs/ajax'
import Cookies from 'js-cookie'


// 统一登录接口
export const ssoLoginAction = (data) => {
  let loginOld = "/service-sso/auth/login";
  let loginNew = "/service_user/login/login";
  return request.post(loginNew, {"data": data})
}
// 统一登录生成验证码
export const ssoCreatecode = () => {
  return request.post('/service-sso/auth/createcode',{},
  {
    'x-csrf-token': Cookies.get('csrfToken'),
    'Content-Type': 'application/json'
  })
}
//获取用户信息
export const getUserInfo = (data) => {
  return request.post('/service_user/login/getLoginUser')
}

//获取模块信息
export const getAuthMoudles = (data) => {
  return request.post('/service_user/moudle/getAuthMoudles',data)
}
//获取页面按钮列表
export const getOperationList = (data) => {
  return request.post('/service_user/operation/findAll')
}


//添加设备
export const addDevice = (data) => {
  // console.log("data",data)
  return request.post(`/service_rms/robotConnect/addEquipment?robotIp=${data.ip}&typeName=${data.typeName}`)
}
//根据IP查询设备
export const findDeviceListByIp = (data) => {
  console.log("data",data)
  return request.post(`/service_rms/robotConnect/listByIp?robotIp=${data.ip}`)
}
//查询设备
export const getDeviceList = (data) => {
  return request.get('/service_rms/robotConnect/list')
}
//删除设备
export const delDevice = (data) => {
  return request.get(`/service_rms/robotConnect/delete?id=${data.id}`)
}
//链接设备
export const openDevice = (data) => {
  return request.post(`/service_rms/robotConnect/connect?robotIp=${data.ip}`)
}
//断开设备
export const overDevice = (data) => {
  return request.post(`/service_rms/robotConnect/disConnect?robotIp=${data.ip}`)
}
//保存任务
export const saveTask = (data) => {
  return request.post(`/service_rms/task/saveTaskEdit?data=${ data.data}`)
}