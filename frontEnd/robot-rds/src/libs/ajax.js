import HttpRequest from '@/libs/axios'
const axios = new HttpRequest();

const getHeader = function(url,headers){
    var headersObj = {
        'device-type': 'PC',
        'access-token': localStorage.getItem('access_token'),
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8;'
        //'Content-Type': 'application/json;'
    }
    if(!headers){
        headers = {};
    }
    if(url && (url.match('/service_user/') || url.match('/service_user/')) && void 0 == headers['Content-Type']){
        headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8;'
    }

    headersObj = Object.assign({}, headersObj, headers);
    var transformRequest = [];
    if(headersObj['Content-Type'] == "application/x-www-form-urlencoded;charset=UTF-8;"){
        transformRequest.push(function (data) {
            return Qs.stringify(data);
        })
    }
    else{
        transformRequest.push(function (data) {
            return JSON.stringify(data);
        })
    }
    return {
        headers:headersObj,
        transformRequest:transformRequest
    };
}
const obj ={
    post :function(url, data, headers){
        var obj1 = getHeader(url, headers);
        return axios.request({
            url:url,
            method: 'post',
            data:data,
            headers:obj1.headers,
            transformRequest:obj1.transformRequest
        })
    },
    postJson :function(url, data, headers){
        if(!headers){
            headers = {};
        }
        if(void 0 == headers['Content-Type']){
            headers['Content-Type'] = 'application/json'
        }
        var obj1 = getHeader(url, headers);
        return axios.request({
            url:url,
            method: 'post',
            data:data,
            headers:obj1.headers,
            transformRequest:obj1.transformRequest
        })
    },
    get:function(url, params, headers){
        var obj1 = getHeader(url, headers);
        return axios.request({
            url: url,
            method: 'get',
            params: params,
            headers:obj1.headers,
        })
    },
    getJson:function(url, params, headers){
        if(!headers){
            headers = {};
        }
        if(void 0 == headers['Content-Type']){
            headers['Content-Type'] = 'application/json'
        }
        var obj1 = getHeader(url, headers);
        return axios.request({
            url: url,
            method: 'get',
            params: params,
            headers:obj1.headers,
        })
    },
    custom:function({obj}){
        var obj1 = getHeader(obj.url, obj.headers);
        obj.headers = obj1.headers;
        obj.transformRequest = obj1.transformRequest;
        return axios.request(obj);
    }
}
export default obj;
