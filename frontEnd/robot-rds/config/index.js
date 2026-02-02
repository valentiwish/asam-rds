'use strict'
// Template version: 1.3.1
// see http://vuejs-templates.github.io/webpack for documentation.

const path = require('path')

module.exports = {
  dev: {
    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/rms/',
    proxyTable: {
      '/sysconf/': {
        //8610服务器地址
        // target: 'http://192.168.13.228:9005',
        target: 'http://192.168.13.228:9005',
      },
      '/service_rms/': {
        pathRewrite: {
          "^/service_rms/": "/"
        },
        //后端rms服务地址，如果权限服务用的服务器，在本地电脑调试，只用修改此处ip地址即可
        // target: 'http://192.168.13.228:8091',
        target: 'http://192.168.13.228:8091',
        changeOrigin: true
      },
      '/service_user': {
        pathRewrite: {
          "^/service_user/": "/"
        },
        // target: 'http://192.168.13.228:8081',
        target: 'http://192.168.13.114:8081',
        prependPath:true,
        changeOrigin: true
      },
      '/service_file': {
        pathRewrite: {
          "^/service_file/": "/"
        },
        target: 'http://192.168.13.41:29109',
        prependPath:true,
        changeOrigin: true
      },
      //28181视频服务
      '/videoservice28181/': {
        target: 'http://192.168.13.39:18978/',
        changeOrigin: true,
        pathRewrite: {
          '^/videoservice28181/': '/'   //重写接口
        },
        onProxyReq(proxyReq) {
          proxyReq.removeHeader('origin')
        }
      }

      // '/task': {
      //   target: 'http://192.168.13.228:8087',
      //   prependPath:true,
      //   changeOrigin: true
      // },

    },


    // Various Dev Server settings
    host:'0.0.0.0', // can be overwritten by process.env.HOST
    port: 9090, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: false,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-

    // Use Eslint Loader?
    // If true, your code will be linted during bundling and
    // linting errors and warnings will be shown in the console.
    useEslint: true,
    // If true, eslint errors and warnings will also be shown in the error overlay
    // in the browser.
    showEslintErrorsInOverlay: false,

    /**
     * Source Maps
     */

    // https://webpack.js.org/configuration/devtool/#development
    devtool: 'cheap-module-eval-source-map',

    // If you have problems debugging vue-files in devtools,
    // set this to false - it *may* help
    // https://vue-loader.vuejs.org/en/options.html#cachebusting
    cacheBusting: true,

    cssSourceMap: false
  },

  build: {
    index: path.resolve(__dirname, '../dist/'+process.env.BUILD_ENV+'/index.html'),

    // Paths
    assetsRoot: path.resolve(__dirname, '../dist/'+process.env.BUILD_ENV),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/rms/',

    /**
     * Source Maps
     */

    productionSourceMap: false,
    // https://webpack.js.org/configuration/devtool/#production
    devtool: '#source-map',

    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: true,
    productionGzipExtensions: ['js', 'css'],

    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report
  }
}