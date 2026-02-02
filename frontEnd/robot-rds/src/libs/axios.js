import axios from 'axios'
import store from '@/store'
import vmInstance from '@/main'

const addErrorLog = errorInfo => {
  const {
    statusText,
    status,
    request: {
      responseURL
    }
  } = errorInfo
  let info = {
    type: 'ajax',
    code: status,
    mes: statusText,
    url: responseURL
  }
  if (!responseURL.includes('save_error_logger')) store.dispatch('addErrorLog', info)
}

class HttpRequest {
  constructor() {
    //this.baseUrl = baseUrl
    this.queue = {}
  }
  getInsideConfig() {
    const config = {
      baseURL: window.location.origin,
      headers: {}
    }
    return config
  }
  destroy(url) {
    delete this.queue[url]
  }
  interceptors(instance, url) {
    // 请求拦截
    instance.interceptors.request.use(config => {
      this.queue[url] = true
      return config
    }, error => {
      return Promise.reject(error)
    })
    // 响应拦截
    instance.interceptors.response.use(res => {
      this.destroy(url);
      if (res.data.code == 401) {
        vmInstance.rejectLogin();
      } else {
        return res
      }
    }, error => {
      this.destroy(url)
      let errorInfo = error.response
      if (!errorInfo) {
        const {
          request: {
            statusText,
            status
          },
          config
        } = JSON.parse(JSON.stringify(error))
        errorInfo = {
          statusText,
          status,
          request: {
            responseURL: config.url
          }
        }
      } else if (errorInfo.status == 401) {
        vmInstance.rejectLogin();
      }
      /* 
        Desc:规避 gb28181服务，不明情况下返回状态码400的问题，修改状态码为200  ，else为其他服务按标准正常返回。
        Date:20221009
        Author:zzk 
      */
      if (url.match("/videoservice28181/") && error.response) {
        return error.response;

      } else {
        addErrorLog(errorInfo)
        return Promise.reject(error)
      }
    })
  }
  request(options) {
    const instance = axios.create()
    options = Object.assign(this.getInsideConfig(), options)
    this.interceptors(instance, options.url)
    return instance(options)
  }
}
export default HttpRequest
