<template>
  <div class="index1001">
    <el-container>
      <el-header height="50px">Header</el-header>
      <el-container>
        <el-aside width="30px">As</el-aside>
        <el-main>
          <!-- 数据来源标题 -->
          <el-row>
            <i class="fa text-warning fa-desktop blue"></i>
            <span>数据源Agent列表</span>
            <router-link to="/index1001"><el-button type="primary" size="small" class="goIndex">
              <i class="fa fa-home fa-lg"></i>返回首页
            </el-button></router-link>
          </el-row>
          <div class="lines"></div>
          <!-- 列表内容 -->
          <div class="listContent">
            <div class="listPic">
              <img src="@/assets/images/a1.png" alt="数据图片" />
            </div>
            <h3>数据库 Agent</h3>
            <p>数据库 Agent 是用于采集结构化数据（RDBMS Etc）的Agent，只需要一个Agent就可以采集同一数据源中的不同源数据</p>
          </div>
          <!-- 数据类型标题 -->
          <h4>
            当前所属数据源为：
            <strong>传的东西</strong>
          </h4>
          <!-- 数据表格标题 -->
          <div class="tableList">
            <el-row>
              <i class="fa text-warning fa-database blue"></i>
              <span>数据源Agent</span>
          <!-- 添加数据表单弹出框  -->
              <el-button type="success"  class="addAgent" size="small" @click="dialogFormVisible = true"  >新增数据库Agent</el-button>
            </el-row>
        <!-- 表格内容 -->
            <el-table
              stripe
              size="mini"
              :data="tableData"
              border
              :header-cell-style="{background:'#ede7f3',color:'black'}"
            >
              <el-table-column prop="date" label="Agent名称" width="196" align="center"></el-table-column>
              <el-table-column prop="name" label="Agent所在服务器IP" width="324" align="center"></el-table-column>
              <el-table-column prop="address" label="Agent 连接端口" width="270" align="center"></el-table-column>
              <el-table-column prop="date" label="数据采集用户" width="224" align="center"></el-table-column>
              <el-table-column label="操作" align="center">
                <el-button size="mini" type="primary" >查看</el-button>
                <el-button size="mini" type="danger" >删除</el-button>
              </el-table-column>
            </el-table>
            <div class="lines"></div>
          </div>
        <!-- 点击新建按钮弹出框 -->
 <el-dialog title="添加数据库 Agent" :visible.sync="dialogFormVisible">
  <el-form>
    
        
    
  </el-form-item>
  

  </el-form>
</el-dialog>
        </el-main>
        
      </el-container>
    </el-container>

  </div>
</template>


<script>
export default {
  data() {
    return {
        dialogFormVisible : false,
      tableData: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1517 弄"
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1519 弄"
        },
        {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄"
        },
         {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄"
        },
         {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄"
        }
      ]
    };
  }
};
</script>

<style scoped>
.index1001 .el-header {
  background: #7cb1de;
}
.index1001 .el-aside {
  background: #7cb1de;
}
.index1001 .el-main {
  padding: 0 90px 0 59px;
}
/* 图标字体设置 */
.el-row {
  height: 64px;
  line-height: 64px;
}
.el-row span {
  color: #2196f3;
  font-size: 18px;
}
/* 字体小图标样式设置 */
.fa-desktop {
  color: #2196f3;
  margin-right: 5px;
}
.fa-database {
  color: #2196f3;
  margin-right: 5px;
}
.fa-home {
  margin-right: 5px;
}
/* button样式设置 */
.goIndex {
  float: right;
  background: #337ab7;
  margin-top: 18px;
  margin-right: 14px;
}
.addAgent {
  background: #5cb85c;
  float: right;
  margin-top: 18px;
  margin-right: 14px;
}
/* 下划线样式 */
.lines {
  width: 1156px;
  min-height: 1px;
  background: #dddddd;
  margin-bottom: 15px;
}
/* 列表内容 */
.listContent {
  width: 207px;
  padding: 10px 10px 0 10px;
  border: 1px solid #cccccc;
  height: 278px;
  margin-bottom: 20px;
  margin-right: 5px;
  float: left;
  border-radius: 4px;
}
.listPic {
  width: 108px;
  border: 1px solid #cccccc;
  height: 108px;
  margin-left: 46px;
  padding: 1px 5px;
  margin-bottom: 22px;
}
.listPic img {
  width: 100%;
  height: 100%;
}
.listContent h3 {
  text-align: center;
  font-size: 24px;
  font-weight: normal;
  line-height: 30px;
  margin-bottom: 10px;
}
.listContent p {
  font-size: 13px;
  line-height: 18px;
}
/* 数据类型标题 */
h4 {
  height: 39px;
  line-height: 39px;
  clear: left;
  font-size: 18px;
  font-weight: normal;
}
h4 strong {
  font-size: 18px;
  color: red;
}
/* 表格数据 */
.tableList {
  width: 100%;
  min-height: 132px;
  border: 1px solid #cccccc;
  padding: 0 14px;
}
</style>