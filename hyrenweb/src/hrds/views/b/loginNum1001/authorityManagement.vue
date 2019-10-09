<template>
  <div class="dataManage">
   
      <i class="block_icon fa text-warning fa-users blue"></i>
      <span>数据权限管理</span>
      <div class="lines"></div>
<!-- 表格 -->
      <el-table
        :data="tableData"
        border
        :header-cell-style="{background:'#ede7f3',color:'black'}"
        size="mini"
        stripe
        row-key
        
      >
        <el-table-column prop="date" label="序号" width="64" align="center"></el-table-column>
        <el-table-column prop="name" label="数据源名称" width="592" align="center"></el-table-column>
        <el-table-column prop="address" label="所属部门" width="380" align="center"></el-table-column>
    <!-- 点击操作弹出框 -->

        <el-table-column  label="操作"  align="center">
            <template slot-scope="scope">
          <b @click="dialogVisible = true;handleEdit(scope.$index, scope.row)">  编辑</b>
           </template>
        </el-table-column>
      </el-table>
    
    <el-row>
      <el-dialog title="更改部门" :visible.sync="dialogVisible" width="53%" class="elDialog">
        数据源
        <el-input v-model="input" placeholder="请确认项目" style="width:200px" disabled class="elInput" ></el-input>所属部门
        <el-select v-model="value1" placeholder="请选择部门（可多选）" multiple style="width:200px">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="mini" type="danger">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false" size="mini">保 存</el-button>
        </span>
      </el-dialog>
    </el-row>

    <el-row class="pagination">
      <el-pagination
        background
        prev-text="<上一页"
        next-text="下一页>"
        layout="prev, pager, next"
        :total="10"
        :page-size="4"
      ></el-pagination>
    </el-row>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false,
      input: '',
      tableData: [
        {
          
          date: "2016",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        },
        {
           
          date: "2016",
          name: "王小虎h",
          address: "上海市普陀区金沙江路 1517 弄"
        },
        {
        
          date: "2016",
          name: "王小虎2",
          address: "上海市普陀区金沙江路 1519 弄"
        },
        {
        
          date: "2016",
          name: "王小虎2",
          address: "上海市普陀区金沙江路 1519 弄"
        },{
        
          date: "2016",
          name: "王小虎2",
          address: "上海市普陀区金沙江路 1519 弄"
        },
        
      ],
      options: [
        {
          value: "选项1",
          label: "第一部门"
        },
        {
          value: "选项2",
          label: "hll"
        }
      ],
      value1: []
    };
  },
  methods:{
  handleEdit(index, row) {
        this.input=row.name
      }
    },
};
</script>

<style scoped>
.dataManage {
  margin-top: 26px;
  margin-bottom: 10px;
  color: #61c3fa;
}
.dataManage span {
  color: #2196f3;
  font-size: 18px;
}
.dataManage b {
  font-size: 14px;
  font-weight: normal;
  cursor: pointer;
  color: red;
}
.lines {
  margin-top: 10px;
  width: 100%;
  height: 1px;
  background: #dddddd;
  margin-bottom: 15px;
}
/* 小图标样式 */
.fa-users {
  margin-right: 5px;
  color: #2196f3;
}
/* 分页 */
.pagination {
  margin-top: 20px;
  width: 100%;
}
.el-pagination {
  float: right;
}
/* 弹出框input的样式 */
.elDialog .el-input {
  margin-left: 20px;
}
.elDialog .el-select{
  margin-left: 20px;
}
.elDialog .elInput {
  margin-right: 88px;
}
</style>