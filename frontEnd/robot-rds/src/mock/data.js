import Mock from 'mockjs'
import orgData from './data/org-data'
import { treeData } from './data/tree-select'
const Random = Mock.Random


export const uploadImage = req => {
  return Promise.resolve()
}

export const getOrgData = req => {
  return orgData
}

export const getTreeSelectData = req => {
  return treeData
}
