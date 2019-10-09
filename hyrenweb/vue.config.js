module.exports = {
	lintOnSave: process.env.NODE_ENV === 'development',
	devServer: {
	    // port: port,
	    open: true,
	    https: false,
	    overlay: {
	      warnings: false,
	      errors: true
	    },
	    proxy: {
	      '/api': {
	        target: process.env.VUE_APP_BASE_API,	// 目标 API 地址
	        changeOrigin: true,	// 允许websockets跨域
	        ws: true,
	        pathRewrite: {
	          '^/api': ''
	        }
	      }
	    }
	  }
}
