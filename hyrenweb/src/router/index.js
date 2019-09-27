import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            name: 'login',
            component: () => import('@/views/login/index')
        },
        {
            path: "/home",
            name: 'home',
            component: () => import('@/components/menu'),
            children: [
                {
                    path: '/syspara',
                    name: 'syspara',
                    component: () => import('@/views/syspara/index')
                },
                {
                    path: '/agentdeploy',
                    name: 'agentdeploy',
                    component: () => import('@/views/agentdeploy/agentdeploylist')
                }
            ]
        }
    ]
})
