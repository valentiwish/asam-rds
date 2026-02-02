import CONFIG from '@/config'
import vmInstance from '@/main';

const service = axios.create({
  baseURL: CONFIG.bpm_api_baseurl, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 10000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    config.headers['Authorization'] = localStorage.getItem('access_token');
    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data;
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 0) {
      if(res.code == 401){
        vmInstance.rejectLogin();
      }
      else{
        return res;
      }
      
    } else {
      return res;
    }
  },
  error => {
    return Promise.reject(error)
  }
)

export default service
