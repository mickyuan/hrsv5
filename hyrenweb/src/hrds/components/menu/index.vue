<template>
<div class="home">
    <el-container>
        <el-header>
            
            <el-row type="flex" :gutter="20">
                <el-col :span="2">
                    <span><i class="el-icon-menu"></i>菜单列表</span>
                </el-col> 
                <!-- <el-col :span="3">
                <div class="grid-content bg-purple"><span class="link ">
                        <router-link ><i class="el-icon-s-custom">修改密码(未做)</i></router-link>
                    </span></div>
            </el-col> -->
                <el-col :span="3" :offset="18">
                    <el-link :underline="false" @click="goback"><i class="el-icon-s-custom">退出登录</i></el-link>
                </el-col>
            </el-row>
        </el-header>
        <el-container>
            <el-aside width="170px">
                <!-- 导航 -->
                <el-menu router default-active="/home">
                    <el-menu-item index="/syspara">
                        <i class="el-icon-setting"></i>
                        <span slot="title">系统参数</span>
                    </el-menu-item>
                    <el-menu-item index="/agentdeploy">
                        <i class="el-icon-document"></i>
                        <span slot="title">Agent部署</span>
                    </el-menu-item>
                    <el-menu-item index="/collectmonitor">
                        <i class="el-icon-document"></i>
                        <span slot="title">采集监控首页</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-container>
                <el-main>
                    <router-view></router-view>
                </el-main>
                <!-- <el-footer><span>版权所有：海云数服 Version 5.0</span></el-footer> -->
            </el-container>
        </el-container>
        <el-header><span>版权所有：海云数服 Version 5.0</span></el-header>
    </el-container>
</div>
</template>

<script>
import {
    mapActions
} from 'vuex'
export default {
    data() {
        return {

        }
    },
    mounted() {
        // 这里是菜单默认路径
        this.$router.push('syspara');
    },
    methods: {
        ...mapActions(['resetToken']),
        goback(){
            this.resetToken();
            this.$router.push('/');
        }
    }
}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style scoped>
.el-header,
.el-footer {
    background-color: #3F51B5;
    text-align: center;
    line-height: 50px;
    color: #fff;
    
}
.el-menu{
    height: 100%;
    /* background-color: #3F51B5; */
}
.home .el-header i{
    color: white;
}
</style>
