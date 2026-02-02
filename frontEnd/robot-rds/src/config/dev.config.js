
import UTILS from './utils'
import baseConfig from './base.config.js'
export default  UTILS.deepObjectMerge(baseConfig,{
    socketServer:"http://192.168.13.228:7003",
});