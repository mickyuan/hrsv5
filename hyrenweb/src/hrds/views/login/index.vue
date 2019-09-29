<template>
<div class="login">
    <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" class="demo-ruleForm" label-width="80px">
        <el-form-item label="登录名" prop="username">
            <el-input v-model="ruleForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="ruleForm.password" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
            <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
    </el-form>
</div>
</template>

<script>
import * as user from '@/hrds/api/login/login'
export default {
    name: 'Login',
    data() {
        var validateLoginName = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入用户名'));
                // } else if (value !== this.ruleForm.checkLoginName) {
                //     callback(new Error('用户名不存在'));
            } else {
                callback();
            }
        };
        var validatePassword = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入密码'));
                // } else if (value !== this.ruleForm.checkPassword) {
                //     callback(new Error('密码错误'));
            } else {
                callback();
            }
        };
        return {
            ruleForm: {
                username: '',
                password: ''
            },
            rules: {
                username: [{
                    required: true,
                    validator: validateLoginName,
                    trigger: 'blur'
                }],
                password: [{
                    required: true,
                    validator: validatePassword,
                    trigger: 'blur'
                }]
            },
            formLabelWidth: '60px'
        };
    },
    methods: {
        submitForm(formName) {
            const that = this;
            that.$refs[formName].validate((valid) => {
                if (valid) {
                    user.login(that.ruleForm).then((res) => {
                        // console.log(res)
                        if (res.success) {
                            that.$router.push('home');
                        }
                    })
                    // user.login(that.ruleForm).then((res) => {
                    // })
                } else {
                    // console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        }
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style scoped>
.login {
    background: url("../../../assets/bg13.jpg") no-repeat;
    background-position: center;
    height: 100%;
    width: 100%;
    background-size: cover;
    position: fixed;
}

.demo-ruleForm {
    position: relative;
    width: 25%;
    padding: 18%;
    margin: 0 auto;
    /* overflow: hidden; */
}
</style>
