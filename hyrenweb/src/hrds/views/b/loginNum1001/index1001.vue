<template>
  <div class="index1001">
    <el-container>
      <el-header  height= "50px">Header</el-header>
      <el-container>
        <el-aside width="30px">Aside</el-aside>
        <el-main>
          <lo-header></lo-header>
          <data-sheetmain></data-sheetmain>
          <data-manage></data-manage>
          <authority-management></authority-management>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Loheader from "@/hrds/components/loginNum1001/Loheader";
import dataSheetmain from "@/hrds/components/loginNum1001/dataSheetmain";
import dataManage from "@/hrds/components/loginNum1001/dataManage";
import authorityManagement from "@/hrds/components/loginNum1001/authorityManagement";

export default {
  name: "index1001",
  components: {
    "lo-header": Loheader,
    "data-sheetmain": dataSheetmain,
    "data-manage": dataManage,
    "authority-management": authorityManagement
  }
};
</script>

<style scoped>
.index1001  .el-header{
  background: #7cb1de;
 
}
.index1001 .el-aside{
    background: #7cb1de;
}
.index1001 .el-main {
 padding-left:74px;
 padding-right:108px;

}
</style>