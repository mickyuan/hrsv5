import request from '@/utils/request'

export function getSysPara() {
  return request({
    url: '/api/A/action/hrds/a/biz/syspara/getSysPara',
    method: 'post'
  })
}

export function deleteSysPara(data) {
  return request({
    url: '/api/A/action/hrds/a/biz/syspara/deleteSysPara',
    method: 'post',
    params: data
  })
}

export function addSysPara(data) {
  return request({
    url: '/api/A/action/hrds/a/biz/syspara/addSysPara',
    method: 'post',
    params: data
  })
}

export function editorSysPara(data) {
  return request({
    url: '/api/A/action/hrds/a/biz/syspara/editorSysPara',
    method: 'post',
    params: data
  })
}