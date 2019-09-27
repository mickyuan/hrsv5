import request from '@/utils/request'

///////////////////////////这里的地址目前是写死的(一个这里存在跨域问题,暂时未找到解决办法)////////////////////////////////

/**
 * 返回部署Agent首页所需的数据信息(数据源列表)
 */
export function getCollectData() {
    return request({
        url: 'http://127.0.0.1:8089/B/action/hrds/b/biz/agentdepoly/getDataSourceInfo',
        method: 'post'
    })
}

/**
 * 获取Agent类型的列表信息
 */
export function getAgentTypeData() {
    return request({
        url: 'http://127.0.0.1:8089/B/action/hrds/b/biz/agentdepoly/getAgentType',
        method: 'post'
    })
}

/**
 * 当前数据源需要部署的Agent列表信息
 * @param data.agent_type Agent类型
 * @param data.source_id 当前行的数据源ID
 * @param data.datasource_name 数据源名称
 */
export function deployAgentList(data) {
    return request({
        url: 'http://127.0.0.1:8089/B/action/hrds/b/biz/agentdepoly/getAgentInfo',
        method: 'post',
        params: data
    })
}

/**
 * 编辑部署Agent信息
 */
export function handleEdit(data) {

    return request({
        url : 'http://127.0.0.1:8089/B/action/hrds/b/biz/agentdepoly/getAgentDownInfo',
        method : 'post',
        params : data
    })
}
