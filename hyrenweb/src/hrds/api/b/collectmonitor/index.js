import request from '@/utils/request'

export function getAgentNumAndSourceNum() {

    return request({
        url : 'http://127.0.0.1:8089/B/action/hrds/b/biz/collectmonitor/getAgentNumAndSourceNum',
        method : 'post'
    })
}