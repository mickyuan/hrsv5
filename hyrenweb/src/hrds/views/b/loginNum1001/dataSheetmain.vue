<template>
  <div class="dataSheetmain">
    <div class="dataSheetmainDiv" v-for="index in lists" :key="index">
      <i class="block_icon fa fa-sitemap fa-3x tree"></i>
      <p>{{index.a}}</p>
      <p class="postionP">Agent个数为 {{index.b}}</p>
      <span>{{index.b}}</span>
      <div class="boxshletr">
          <i class="fa fa-download  fa-lg"></i>
     <el-button
        type="text"
        class="editBtn"
        @click="dialogFormVisibleAdd = true"
       >
      <i class="fa fa-pencil  fa-lg"></i></el-button>
      </div>
    </div>
    <!-- 弹出编辑的表单 -->
   <el-dialog title="添加数据源" :visible.sync="dialogFormVisibleAdd" width="40%">
      <el-form :model="form">
        <el-form-item label=" 数据源编号" :label-width="formLabelWidth">
          <el-input v-model="form.region" autocomplete="off" placeholder="数据源编号"></el-input>
        </el-form-item>
        <el-form-item label=" 数据源名称" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" placeholder="数据源名称"></el-input>
        </el-form-item>
        <el-form-item label=" 所属部门" :label-width="formLabelWidth">
          <el-select v-model="value" filterable placeholder="请选择">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
  </el-select>
        </el-form-item>
       <el-form-item label=" 数据源详细描述" :label-width="formLabelWidth">
          <el-input type="textarea" v-model="form.desc" autocomplete="off"  placeholder="数据源详细描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisibleAdd = false" size="mini" type="danger">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisibleAdd = false" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      lists: [
        { a: "cs", b: 1 },
        { a: "zcs", b: 2 },
        { a: "水质品质", b: 2 },
        { a: "测试", b: 3 },
        { a: "sss", b: 9 }
      ],
      dialogFormVisibleAdd: false,
      form: {
        name: "",
        region: "",
        date1: "",
        date2: "",
        delivery: false,
        type: [],
        resource: "",
        desc: ""
      },
      formLabelWidth: "150px"
    };
  }
};
</script>

<style scoped>
/* 组件样式设置 */
.dataSheetmain {
  padding-left: 44px;
  border: 1px solid #dddddd;
  height: 124px;
  padding-top: 30px;
}
.dataSheetmainDiv:hover .boxshletr {
  display: block;
}
.dataSheetmainDiv:hover {
  background: #286090;
  cursor: pointer;
}
.dataSheetmainDiv {
  padding-top: 10px;
  width: 100px;
  height: 80px;
  background: #337ab7;
  border-radius: 10px;
  margin-right: 90px;
  text-align: center;
  float: left;
  position: relative;
}
.dataSheetmainDiv p {
  color: #fff;
  font-size: 18px;
}
/* 标签定位 */
.dataSheetmainDiv span {
  display: block;
  width: 18px;
  height: 18px;
  background: #f89406;
  border-radius: 50%;
  color: white;
  position: absolute;
  right: -2px;
  top: -3px;
  font-size: 12px;
}
/* 字体描述定位 */
.dataSheetmainDiv .postionP {
  position: absolute;
  bottom: -20px;
  left: 4px;
  font-size: 13px;
  color: black;
}
/* 遮料层样式 */
.boxshletr {
  width: 106px;
  height: 28px;
  background: black;
  opacity: 0.6;
  position: absolute;
  bottom: -20px;
  left: -3px;
  display: none;
}
.fa {
  color: white;
}
.fa-download {
  margin-right: 6px;
  margin-top:6px;
}
.fa-pencil{
    margin-top:6px;
}
/* 小图标样式 */
.tree {
  color: aliceblue;
}
/* 按钮设置 */
.editBtn{
    padding: 0;
}
</style>