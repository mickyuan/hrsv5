import Vue from 'vue'

import router from './router'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import store from './store'
import App from './App.vue';
import '@/assets/font-awesome-4.7.0/css/font-awesome.min.css'

Vue.use(ElementUI);

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App),
}).$mount('#app')
