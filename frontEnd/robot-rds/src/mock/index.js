import Mock from 'mockjs'
import {getOrgData, getTreeSelectData } from './data'

// 配置Ajax请求延时，可用来测试网络延迟大时项目中一些效果
Mock.setup({
  timeout: 10000
})

// 登录相关和获取用户信息
Mock.mock(/\/get_org_data/, getOrgData)
Mock.mock(/\/get_tree_select_data/, getTreeSelectData)
// Mock.mock(new RegExp("/service_rms/moudle/getAuthMoudles"), getTreeSelectData)

export default Mock
