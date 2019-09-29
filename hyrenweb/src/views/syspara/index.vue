<template>
<div>
    <!-- 列表数据 start-->
    <el-table :data="tableData.filter(data => !search || data.para_name.trim().toLowerCase().includes(search) || data.para_value.trim().toLowerCase().includes(search))" :default-sort="{prop: 'para_id', order: 'ascending'}" height="600px">
        <el-table-column prop="para_id" sortable label="参数ID"></el-table-column>
        <el-table-column label="参数名称" prop="para_name" sortable>
            <template slot-scope="{row, $index}">
                <el-input class="edit-cell" v-if="showEdit[$index]" v-model="row.para_name" />
                <span v-if="!showEdit[$index]">{{row.para_name}}</span>
            </template>
        </el-table-column>
        <el-table-column label="参数类型" prop="para_type">
            <template slot-scope="{row, $index}">
                <el-input class="edit-cell" v-if="showEdit[$index]" v-model="row.para_type" />
                <span v-if="!showEdit[$index]">{{row.para_type}}</span>
            </template>
        </el-table-column>
        <el-table-column label="参数值" prop="para_value">
            <template slot-scope="{row, $index}">
                <el-input class="edit-cell" v-if="showEdit[$index]" v-model="row.para_value" />
                <span v-if="!showEdit[$index]">{{row.para_value}}</span>
            </template>
        </el-table-column>
        <el-table-column label="描述" prop="remark">
            <template slot-scope="{row, $index}">
                <el-input class="edit-cell" v-if="showEdit[$index]" v-model="row.remark" />
                <span v-if="!showEdit[$index]">{{row.remark}}</span>
            </template>
        </el-table-column>
        <el-table-column label="操作">
            <template slot-scope="{row, $index}">
                <el-button size="mini" @click.native="handleUpdate($index, row)" v-if="showBtn[$index]">更新</el-button>
                <el-button size="mini" @click.native="handleCancel($index, row)" v-if="showBtn[$index]">取消</el-button>
                <el-button size="mini" type="primary" @click.native="handleEdit($index, row)" v-if="!showBtn[$index]">编辑</el-button>
                <el-button size="mini" type="danger" @click="handleDelete($index, row)" v-if="!showBtn[$index]">删除</el-button>
            </template>
        </el-table-column>
        <el-table-column>
            <template slot="header" slot-scope="scope">
                <el-button size="mini" type="success" @click="dialogFormVisible = true">新增参数</el-button>
            </template>
        </el-table-column>
        <el-table-column>
            <template slot="header" slot-scope="scope">
                <el-input v-model="search" size="mini" placeholder="输入关键字搜索" />
            </template>
        </el-table-column>
    </el-table>
    <!-- 列表数据 end-->

    <!-- 添加一条数据 start-->
    <el-dialog title="添加一条数据" :visible.sync="dialogFormVisible">
        <el-form :model="form" ref="form" :rules="rules">
            <el-form-item label="para_name" prop="para_name" :label-width="formLabelWidth">
                <el-input v-model="form.para_name" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="para_value" prop="para_value" :label-width="formLabelWidth">
                <el-input v-model="form.para_value" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="para_type" prop="para_type" :label-width="formLabelWidth">
                <el-input v-model="form.para_type" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="remark" prop="remark" :label-width="formLabelWidth">
                <el-input type="textarea" v-model="form.remark"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="resetForm('form')">取 消</el-button>
            <el-button type="primary" @click="submitForm('form')">确 定</el-button>
        </div>
    </el-dialog>
    <!-- 添加一条数据 end-->
</div>
</template>

<script>
import * as sysPara from "@/api/syspara/syspara";

export default {
    name: "Syspara",
    data() {
        return {
            tableData: [],
            dialogFormVisible: false,
            search: "",
            form: {
                // para_name: "",
                // para_value: "",
                // para_type: "",
                // remark: ""
            },
            formLabelWidth: "120px",
            rules: {
                para_name: [{
                    required: true,
                    message: "para_name 是必填项",
                    trigger: "blur"
                }],
                para_value: [{
                    required: true,
                    message: "para_value 是必填项",
                    trigger: "blur"
                }],
                para_type: [{
                    required: true,
                    message: "para_type 是必填项",
                    trigger: "blur"
                }],
                remark: [{
                    required: true,
                    message: "remark 是必填项",
                    trigger: "blur"
                }]
            },
            showEdit: [], //显示编辑框
            showBtn: []
        };
    },
    created() {
        sysPara.getSysPara().then((response) => {
            this.tableData = response.data;
        });
    },
    methods: {
        handleEdit(index, row) {

            //点击编辑
            this.$set(this.showEdit, index, true);
            this.$set(this.showBtn, index, true);
        },
        handleCancel(index, row) {

            //取消编辑
            this.$set(this.showEdit, index, false);
            this.$set(this.showBtn, index, false);
        },
        //点击更新
        handleUpdate(index, row) {
            sysPara.editorSysPara(row).then(response => {
                    if (response && response.success) {
                        this.$message({
                            type: "success",
                            message: "更新成功!"
                        });
                        // 重新渲染列表
                        this.tableData = response.data;
                        this.$set(this.showEdit, index, false);
                        this.$set(this.showBtn, index, false);
                    } else {
                        this.$message.error("更新失败！");
                    }
                })
                .catch(() => {
                    this.$message({
                        type: "error",
                        message: "更新失败"
                    });
                });
        },
        // 删除
        handleDelete(index, row) {
            this.$confirm("确定要删除该条数据?", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                })
                .then(() => {
                    // 入参
                    let params = {};
                    params["para_id"] = row.para_id;
                    params["para_name"] = row.para_name;

                    // 调用删除方法
                    sysPara.deleteSysPara(params).then((response) => {
                        if (response && response.success) {
                            this.$message({
                                type: "success",
                                message: "删除成功!"
                            });
                            // 重新渲染列表
                            this.tableData = response.data;
                        } else {
                            this.$message.error("删除失败！");
                        }
                    });
                })
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: "已取消删除"
                    });
                });
        },
        // 新增一条数据
        submitForm(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    // 调用添加方法
                    sysPara.addSysPara(this.form).then((response) => {
                        if (response && response.success) {
                            this.$message({
                                type: "success",
                                message: "添加成功!"
                            });

                            // 隐藏对话框
                            this.dialogFormVisible = false;
                            // 数据清空
                            this.form = {};
                            // 重新渲染列表
                            this.tableData = response.data;
                        } else {
                            this.$message.error("添加失败！");
                        }
                    });
                } else {
                    // console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
            this.dialogFormVisible = false;
        }
    }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style scoped>

</style>
