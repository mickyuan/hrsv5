import request from '@/utils/request'

/**
 * 添加数据源数据信息
 */
export function addDataResource() {
    return request({
        url: 'http://127.0.0.1:8089/B/action/hrds/b/biz/agentdepoly/getDataSourceInfo',
        method: 'post',
        data:{
            "name":this.form.name,
            "region":this.form.region,
            "date1":this.form.date1,
            "value":this.value
        }
    })
}