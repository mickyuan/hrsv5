<template>
<div>
    <el-row :gutter="20">
        <el-col :span="8">
            <el-col :span="12">
                <el-badge :value="agentnum" class="item">
                    <el-button type="success" size="s">评论</el-button>
                </el-badge>
            </el-col>
            <el-col :span="12">
                <div class="grid-purple">{{sourcenum}}</div>
            </el-col>
        </el-col>
        <el-col :span="8">
            <div class="grid-content bg-purple">{{sourcenum}}</div>
        </el-col>
    </el-row>
</div>
</template>

<script>
import * as collect from '@/hrds/api/b/collectmonitor/index'
export default {
    data() {
        return {
            agentnum: 0,
            sourcenum: 0
        }
    },
    created() {
        collect.getAgentNumAndSourceNum().then((res) => {
            if (res.success) {
                this.agentnum = res.data.agentnum;
                this.sourcenum = res.data.sourcenum;
            }
        })
    }
}
</script>

<style scoped>
.bg-purple {
    background: #108516;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}
</style>
