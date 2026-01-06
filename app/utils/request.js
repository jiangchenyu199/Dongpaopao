// utils/request.js

import { getApiUrl } from '../config/index.js'

const BASE_URL = getApiUrl()

function request(options) {
	const config = {
		url: BASE_URL + options.url,
		method: options.method || 'GET',
		data: options.data || {},
		header: {
			'Content-Type': 'application/json',
			...options.header
		},
		timeout: options.timeout || 10000
	}

	return new Promise((resolve, reject) => {
		uni.request({
			...config,
			success: (res) => {
				if (res.data && res.data.errCode === 11) {
					uni.showToast({
						title: res.msg
					})
				}
				resolve(res.data)
			},
			fail: (err) => {
				// reject(err)
				uni.showToast({
					title: err.errMsg,
					icon: 'none'
				})

				// 执行调用方传入的失败回调（自定义操作）
				if (typeof options.failed === 'function') {
					options.failed(err)
				}
			}
		})
	})
}

// 挂载到 uni 对象
uni.$request = request

// 默认导出
export default request