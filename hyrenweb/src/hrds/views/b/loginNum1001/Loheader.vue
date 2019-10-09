<template>
  <div class="loheader">
    <el-row>
     <i class="block_icon fa text-warning fa-globe blue"></i><span>数据来源表</span>
      <el-button
        type="primary"
        class="el1 els"
        @click="dialogFormVisibleAdd = true"
        size="small" >
        <i class="block_icon fa fa-cubes"></i>添加数据源</el-button>
      <el-button
        type="primary"
        class="els"
        @click="dialogFormVisibleImport = true"
        size="small">
       <i class="fa fa-cloud-upload"></i>导入数据源</el-button>
    </el-row>
 <!-- 实现点击导入按钮进行页面数据导入-->
   <!-- 弹出表单 -->
    <el-dialog title="上传文件" :visible.sync="dialogFormVisibleImport" width="40%">
      <el-form :model="form" >
        <el-form-item label="Agent IP地址 :" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" style="width:284px"></el-input> <el-tooltip class="item" effect="dark" content="要上传的数据源下Agent的IP地址" placement="right"><i class="fa fa-question-circle" aria-hidden="true"></i></el-tooltip>
        </el-form-item>
        <el-form-item label="Agent 端口 :" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" style="width:284px"></el-input><el-tooltip class="item" effect="dark" content="要上传的数据源下Agent的端口" placement="right"><i class="fa fa-question-circle" aria-hidden="true"></i></el-tooltip>
        </el-form-item>
        <el-form-item label="数据采集用户：" :label-width="formLabelWidth">
          <el-select v-model="form.region" placeholder="请选择" style="width:284px">
             <el-option
      v-for="item in optionsone"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
          </el-select>
        </el-form-item>
          <el-form-item label="上传要导入的数据源 :" :label-width="formLabelWidth">
            <el-tooltip class="item" effect="dark" content="在本系统中要下载的数据源，后缀名为hrds的加密文件" placement="right"><i class="fa fa-question-circle question3  " aria-hidden="true"></i></el-tooltip><el-input placeholder v-model="input2" style="width:284px">
              <template slot="append">
                <el-upload
                  class="upload-demo"
                  action="https://jsonplaceholder.typicode.com/posts/"
                  :on-preview="handlePreview"
                  :on-remove="handleRemove"
                  :before-remove="beforeRemove"
                  multiple
                  :limit="3"
                  :on-exceed="handleExceed"
                  :file-list="fileList"
                >
                  <el-button size="small" type="primary" fill="blue">点击上传</el-button>
                </el-upload>
              </template>
            </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisibleImport = false" size="mini" type="danger">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisibleImport = false" size="mini">上传</el-button>
      </div>
    </el-dialog>
<!-- 实现点击添加按钮进行页面数添加-->
  <!-- 添加的弹出表单 -->
    <el-dialog title="添加数据源" :visible.sync="dialogFormVisibleAdd" width="40%">
      <el-form :model="form">
        <el-form-item label=" 数据源编号" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" placeholder="数据源编号" style="width:284px"></el-input>
        </el-form-item>
        <el-form-item label=" 数据源名称" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" placeholder="数据源名称" style="width:284px"></el-input>
        </el-form-item>
        <el-form-item label=" 所属部门" :label-width="formLabelWidth">
          <el-select v-model="value" filterable placeholder="请选择" style="width:284px">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
  </el-select>
        </el-form-item>
       <el-form-item label=" 数据源详细描述" :label-width="formLabelWidth">
          <el-input type="textarea" v-model="form.name" autocomplete="off"  placeholder="数据源详细描述" style="width:284px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisibleAdd = false" size="mini" type="danger">取 消</el-button>
        <el-button type="primary" @click=" Add();" size="mini">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      options: [{
          value: '选项1',
          label: '第一部门'
        }, {
          value: '选项2',
          label: 'hll'
        }],
        optionsone:[{
            value: '选项1',
          label: '全功能操作员'
        },{
            value: '选项2',
          label: 'hll全功能操作员'
        },{
            value: '选项3',
           label: '全功能操作员'
        }],
         value: '',
      dialogFormVisibleImport: false,
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
  },
  methods:{
      save(){
        console.log("1");
      }
  }
};
</script>

<style scoped>
.el-row {
  height: 64px;
  line-height: 64px;
  width: 100%;
}
.el-row span{
  color: #2196f3; 
  font-size:18px;
}
/* 字体小图标样式设置 */
.fa-globe{
  color: #2196f3; 
  margin-right: 5px;
}
.fa-cubes{
  margin-right: 5px;
}
.fa-cloud-upload{
  margin-right: 5px;

}
.fa-question-circle{
  margin-left: 40px;
}
.question3{
  margin-left: 0;
  margin-right: 30px;
}
/* button样式设置 */
.els {
  float: right;
  margin-top: 18px;
   background: #337ab7;
}
.el1 {
  margin-left: 10px;
  background: #337ab7;
}
</style>