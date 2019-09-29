<template>
<div>
    <!-- <div>Agent信息列表</div> -->
    <!-- <svg-icon icon-class="add" /> -->
    <!-- <el-divider></el-divider> -->
    <el-table :data="sourceData.filter(data => !search || data.datasource_name.toLowerCase().includes(search.toLowerCase()))" :default-sort="{prop: 'datasource_name', order: 'descending'}" max-height="50%" style="width: 100%">
        <el-table-column sortable prop="datasource_name" label="数据源"></el-table-column>
        <el-table-column sortable label="数据库 Agent">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="deployAgentList(agentTypeData.shujuku, scope.row.source_id,scope.row.datasource_name)">部署</el-button>
            </template>
        </el-table-column>
        <el-table-column sortable label="数据文件 Agent">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="deployAgentList(agentTypeData.shujuwenjian, scope.row.source_id,scope.row.datasource_name)">部署</el-button>
            </template>
        </el-table-column>
        <el-table-column sortable label="非结构化 Agent">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="deployAgentList(agentTypeData.feijiegouhua, scope.row.source_id,scope.row.datasource_name)">部署</el-button>
            </template>
        </el-table-column>
        <el-table-column sortable label="半结构化 Agent">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="deployAgentList(agentTypeData.banjiegouhua, scope.row.source_id,scope.row.datasource_name)">部署</el-button>
            </template>
        </el-table-column>
        <el-table-column sortable label="Ftp Agent">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="deployAgentList(agentTypeData.ftp, scope.row.source_id,scope.row.datasource_name)">部署</el-button>
            </template>
        </el-table-column>
        <el-table-column align="right">
            <template scope="scope" slot="header">
                <el-input v-model="search" size="mini" placeholder="输入关键字搜索" />
            </template>
        </el-table-column>
    </el-table>
    <!--Agent信息列表模态框-->
    <el-dialog :title="datasource_name" :visible.sync="outerVisible" width="75%">
        <el-table :data="agentDataList.filter(data => !agentSearch || data.agent_name.toLowerCase().includes(agentSearch.toLowerCase()))" :default-sort="{prop: 'agent_name', order: 'descending'}">
            <el-table-column sortable property="agent_name" label="Agent名称"></el-table-column>
            <el-table-column sortable property="agent_ip" label="Agent IP"></el-table-column>
            <el-table-column sortable property="agent_port" label="Agent 端口"></el-table-column>
            <el-table-column sortable property="agent_type" label="采集类型"></el-table-column>
            <el-table-column sortable property="agent_status" label="连接状态"></el-table-column>
            <el-table-column label="操作">
                <template fixed slot-scope="scope">
                    <el-button type="success" size="mini" @click="handleEdit(scope.row)"><i class="el-icon-download">配置及部署Agent</i></el-button>
                </template>
            </el-table-column>
            <el-table-column align="left">
                <template slot="header" slot-scope="scope">
                    <el-input v-model="agentSearch" size="mini" placeholder="输入关键字搜索" />
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
            <el-button type="danger" @click="outerVisible = false">取 消</el-button>
        </div>
    </el-dialog>

    <!--部署Agent模态框 agentDeploy-->
    <el-dialog title="Agent部署" :visible.sync="dialogFormVisible" width="75%">
        <el-form :model="agentDeploy" label-width="110px" class="demo-form-inline" :inline="true" autocomplete="off">
            <el-row>
                <el-col :md="8">
                    <el-form-item label="Agent名称:">
                        <el-input v-model="agentDeploy.agent_name" placeholder="Agent名称" readonly autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="Agent IP:">
                        <el-input v-model="agentDeploy.agent_ip" placeholder="Agent IP" readonly autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="Agent端口:">
                        <el-input v-model="agentDeploy.agent_port" placeholder="Agent端口" readonly autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="用户名:">
                        <el-input v-model="agentDeploy.user_name" placeholder="用户名" autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="密码:">
                        <el-input v-model="agentDeploy.passwd" placeholder="密码" autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="是否自动部署:">
                        <el-switch style="display: block" active-value="0" inactive-value="1" v-model="agentDeploy.deploy" active-color="#13ce66" inactive-color="#ff4949" active-text="是" inactive-text="否">
                        </el-switch>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="Agent存放目录:">
                        <el-switch style="display: block" active-value="0" inactive-value="1" v-model="deploy" active-color="#13ce66" inactive-color="#ff4949" active-text="系统默认" inactive-text="自定义">
                        </el-switch>
                    </el-form-item>
                </el-col>
                <el-col :md="8" v-if="deploy==0">
                    <el-form-item label="Agent安装目录:">
                        <el-input v-model="agentDeploy.save_dir" hide-required-asterisk="true" placeholder="Agent安装目录" autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8" v-if="deploy==0">
                    <el-form-item label="日志文件:">
                        <el-input v-model="agentDeploy.log_dir" hide-required-asterisk="true" placeholder="日志文件" autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :md="8">
                    <el-form-item label="描述:">
                        <el-input v-model="agentDeploy.name" autocomplete="off"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button type="danger" @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
        </div>
    </el-dialog>
</div>
</template>

<script>
import * as agentDeploy from '@/api/agentdeploy/agentdeploylist'

export default {
    data() {
        return {
            sourceData: [],
            agentDataList: [], //Agent信息列表
            agentTypeData: {}, //Agent类型集合
            agentDeploy: {
                agent_name: '',
                agent_ip: '',
                agent_port: ''
            }, //部署Agent信息
            search: "",
            agentSearch: "",
            datasource_name: '',
            outerVisible: false,
            dialogFormVisible: false,
            requestData: {
                agent_type: '',
                source_id: ''
            },
            deploy: '1'
        }
    },
    created() {
        agentDeploy.getCollectData().then((res) => {
            this.sourceData = res.data;
        }).catch(error => {
            this.$message({
                message: "服务器连接异常",
                type: "error"
            });
        })

        agentDeploy.getAgentTypeData().then((res) => {
            this.agentTypeData = res.data;
        }).catch(error => {
            this.$message({
                message: "服务器连接异常",
                type: "error"
            });
        })
    },
    methods: {

        /**
         * 当前数据源需要部署的Agent列表信息
         * @param agent_type Agent类型
         * @param source_id 当前行的数据源ID
         * @param datasource_name 数据源名称
         */
        deployAgentList(agent_type, source_id, datasource_name) {
            this.requestData.agent_type = agent_type;
            this.requestData.source_id = source_id;
            this.datasource_name = '数据源名称 : ' + datasource_name;
            agentDeploy.deployAgentList(this.requestData).then(res => {
                    this.agentDataList = res.data;
                    this.outerVisible = true;

                })
                .catch(error => {
                    this.$message({
                        message: "服务器连接异常",
                        type: "error"
                    });
                })
        },
        handleEdit(row) {

            agentDeploy.handleEdit(row).then(res => {
                    this.dialogFormVisible = true;
                    this.agentDeploy = res.data;
                    if (typeof this.agentDeploy.down_id == 'undefined') {
                        this.agentDeploy.agent_name = row.agent_name;
                        this.agentDeploy.agent_ip = row.agent_ip;
                        this.agentDeploy.agent_port = row.agent_port;
                    }
                })
                .catch(error => {
                    this.$message({
                        message: "服务器连接异常",
                        type: "error"
                    });
                })
        }
    }
}
</script>
