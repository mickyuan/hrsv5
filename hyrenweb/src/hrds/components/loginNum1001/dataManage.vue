<template>
  <div class="dataManage">
    <el-row>
        <i class="block_icon fa text-warning fa-book blue"></i><span>数据管理列表</span>
        <div class="lines"></div>
       <el-table  stripe size="mini" :data="tableData" border  :header-cell-style="{background:'#ede7f3',color:'black'}" >
      <el-table-column prop="date" label="序号" width="64" align="center"></el-table-column>
      <el-table-column prop="name" label="文件名"  width="119" align="center"></el-table-column>
      <el-table-column prop="address" label="文件后缀名" width="182"  align="center" > </el-table-column>
      <el-table-column prop="date" label="文件类型" width="150" align="center"></el-table-column>
      <el-table-column prop="date" label="提交人" width="120" align="center" ></el-table-column>
      <el-table-column prop="date" label="提交时间" width="151" align="center" ></el-table-column>
      <el-table-column prop="date" label="申请类型" width="152" align="center"></el-table-column>
      <el-table-column prop="date" label="操作"  align="center"></el-table-column>
    </el-table>
    </el-row>
    <el-row></el-row>
  </div>
</template>

<script>
export default {
 data(){
     return{
          tableData: [{
            date: '2016-05-02',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1518 弄'
          }, {
            date: '2016-05-04',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1517 弄'
          }, {
            date: '2016-05-01',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1519 弄'
          }, {
            date: '2016-05-03',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1516 弄'
          }]
     }
 }
}
</script>

<style scoped>
.dataManage{
    margin-top: 26px;
    margin-bottom: 10px;
    color: #61c3fa;
}
/* 下划线样式 */
.lines{
     margin-top: 10px;
    width: 1140px;
    height: 1px;
    background: #dddddd;
    margin-bottom: 15px;
}
/* 小图标字体样式设置 */
.dataManage  .fa-book {
  color: #2196f3;
  margin-right:5px;  
}
.dataManage span{
   color: #2196f3;
   font-size: 18px;
}
</style>