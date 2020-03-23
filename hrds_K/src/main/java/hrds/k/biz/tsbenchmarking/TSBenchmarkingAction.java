package hrds.k.biz.tsbenchmarking;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.tsbenchmarking.bean.TSBConf;
import hrds.k.biz.tsbenchmarking.query.TSBQuery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "表结构对标", author = "BY-HLL", createdate = "2020/3/12 0012 上午 09:10")
public class TSBenchmarkingAction extends BaseAction {

    private static final TSBConf tsbConf = new TSBConf();

    @Method(desc = "获取表结构对标树", logicStep = "获取表结构对标树")
    @Param(name = "tree_source", desc = "树菜单来源", range = "String类型", nullable = true)
    @Return(desc = "表结构对标树信息", range = "表结构对标树信息")
    public Map<String, Object> getTSBTreeData(String tree_source) {
        if (StringUtil.isBlank(tree_source)) {
            throw new BusinessException("树来源不能为空! 数据对标树请设置为: dataBenchmarking");
        }
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //根据源菜单信息获取节点数据列表
        List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(tree_source, getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        List<Node> tsbTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        //定义返回的分叉树结果Map
        Map<String, Object> tsbTreeDataMap = new HashMap<>();
        tsbTreeDataMap.put("tsbTreeList", JsonUtil.toObjectSafety(tsbTreeList.toString(), List.class));
        return tsbTreeDataMap;
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    @Param(name = "table_type", desc = "表类型标识", range = "01:批量数据,02:实时数据表", nullable = true)
    @Param(name = "file_id", desc = "表源属性id", range = "String[]")
    @Return(desc = "字段信息列表", range = "字段信息列表")
    public List<Map<String, Object>> getColumnByFileId(String data_layer, String table_type, String file_id) {
        //设置 Dbm_normbm_detect 对象
        setDbmNormbmDetect(data_layer);
        //设置 Dbm_dtable_info 对象
        setDbmDtableInfo(file_id);
        List<Map<String, Object>> col_info_s = new ArrayList<>();
        //数据层获取不同表结构
        switch (data_layer) {
            case "DCL":
                //如果数据表所属层是DCL层,判断表类型是批量还是实时
                if (Constant.DCL_BATCH.equals(table_type)) {
                    col_info_s.addAll(TSBQuery.getDCLBatchTableColumnsById(file_id));
                } else if (Constant.DCL_REALTIME.equals(table_type)) {
                    col_info_s.addAll(TSBQuery.getDCLBatchRealTimeTableColumnsById(file_id));
                } else {
                    throw new BusinessException("数据表类型错误! 01:批量数据,02:实时数据");
                }
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException(data_layer + "层暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
        return col_info_s;
    }

    @Method(desc = "设置对标检测字段信息表",
            logicStep = "设置对标检测字段信息表")
    @Param(name = "col_info_s", desc = "字段信息", range = "String类型,格式" +
            "[column_id: 1000369846" +
            "tc_remark: 参数名" +
            "is_primary_key: 0" +
            "agent_id: 1000003138" +
            "database_id: 1000369838" +
            "column_name: para_name" +
            "column_ch_name: 参数名" +
            "source_id: 1000003137" +
            "column_type: varchar(512)]")
    @Return(desc = "检测字段信息表信息集合", range = "检测字段信息表信息集合")
    public void setDbmDtcolInfo(String col_info_s) {
        //设置 Dbm_dtcol_info 对象
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> col_info_list = JsonUtil.toObject(col_info_s, type);
        List<Dbm_dtcol_info> dbm_dtcol_info_list = new ArrayList<>();
        col_info_list.forEach(col_info -> {
            Dbm_dtcol_info dbm_dtcol_info = new Dbm_dtcol_info();
            dbm_dtcol_info.setCol_id(PrimayKeyGener.getNextId());
            dbm_dtcol_info.setCol_cname(col_info.get("column_ch_name"));
            dbm_dtcol_info.setCol_ename(col_info.get("column_name"));
            if (null == col_info.get("tc_remark")) {
                dbm_dtcol_info.setCol_remark("");
            } else {
                dbm_dtcol_info.setCol_remark(col_info.get("tc_remark"));
            }
            Map<String, String> col_type_map = parsingFiledType(col_info.get("column_type"));
            dbm_dtcol_info.setData_type(col_type_map.get("data_type"));
            dbm_dtcol_info.setData_len(col_type_map.get("data_len"));
            dbm_dtcol_info.setDecimal_point(col_type_map.get("decimal_point"));
            dbm_dtcol_info.setIs_key(col_info.get("is_primary_key"));
            dbm_dtcol_info.setIs_null(IsFlag.Fou.getCode());
            dbm_dtcol_info.setDefault_value("");
            dbm_dtcol_info.setDbm_tableid(tsbConf.getDbm_dtable_info().getDbm_tableid());
            dbm_dtcol_info.setDetect_id(tsbConf.getDbm_normbm_detect().getDetect_id());
            dbm_dtcol_info.setColumn_id(col_info.get("column_id"));
            dbm_dtcol_info.setDatabase_id(col_info.get("database_id"));
            dbm_dtcol_info.setAgent_id(col_info.get("agent_id"));
            dbm_dtcol_info.setSource_id(col_info.get("source_id"));
            dbm_dtcol_info_list.add(dbm_dtcol_info);
            tsbConf.setDbm_dtcol_info_list(dbm_dtcol_info_list);
        });
    }

    @Method(desc = "获取预测对标结果", logicStep = "获取预测对标结果")
    @Return(desc = "预测对标结果", range = "预测对标结果")
    public Map<String, Object> getPredictBenchmarkingResults() {
        //获取待检测字段信息
        List<Dbm_dtcol_info> dbm_dtcol_infos = tsbConf.getDbm_dtcol_info_list();
        //获取预测接口地址信息
        String predict_address = PropertyParaValue.getString("predict_address", "http://127.0.0.1:38081/predict");
        //设置请求参数
        List<Map<String, String>> params = new ArrayList<>();
        dbm_dtcol_infos.forEach(dbm_dtcol_info -> {
            Map<String, String> map = new HashMap<>();
            if (StringUtil.isBlank(dbm_dtcol_info.getCol_id().toString())) {
                throw new BusinessException("检测字段主键为空!");
            }
            map.put("id", dbm_dtcol_info.getCol_id().toString());
            if (StringUtil.isBlank(dbm_dtcol_info.getCol_cname())) {
                throw new BusinessException("检测字段中文名为空!");
            }
            map.put("col_cnname", dbm_dtcol_info.getCol_cname());
            map.put("col_ename", dbm_dtcol_info.getCol_ename());
            map.put("col_desc", dbm_dtcol_info.getCol_remark());
            params.add(map);
        });
        //根据接口获取预测结果
        String bodyString = new HttpClient()
                .addData("content", JsonUtil.toJson(params)).post(predict_address).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("URL请求失败!" + predict_address));
        //设置返回结果
        Map<String, Object> predictDataMap = new HashMap<>();
        predictDataMap.put("predictData", JsonUtil.toObjectSafety(ar.getData().toString(), List.class));
        return predictDataMap;
    }

    @Method(desc = "保存表结构对标数据", logicStep = "保存表结构对标数据")
    @Param(name = "dbm_normbmd_info_s", desc = "对标结果数据信息",
            range = "String类型,JsonArray格式,[{col_id:'',col_similarity:'',standard_id:'',is_artificial:''}]" +
                    "col_id: 字段id,唯一性" +
                    "col_similarity: 字段相似度" +
                    "standard_id: 标准编号(唯一性),如: IP600012" +
                    "is_artificial: 是否人工对标,IsFlag标识,1:是,0:否")
    public void saveTSBConfData(String dbm_normbmd_info_s) {
        //设置 Dbm_normbmd_result
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> dbm_normbmd_info_list = JsonUtil.toObject(dbm_normbmd_info_s, type);
        setDbmNormbmdResult(dbm_normbmd_info_list);
        //结果存入数据库
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //设置检测结束时间
            Dbm_normbm_detect dbm_normbm_detect = tsbConf.getDbm_normbm_detect();
            dbm_normbm_detect.setDetect_edate(DateUtil.getSysDate());
            dbm_normbm_detect.setDetect_etime(DateUtil.getSysTime());
            //保存对标检测记录表表信息
            tsbConf.setDbm_normbm_detect(dbm_normbm_detect);
            tsbConf.getDbm_normbm_detect().add(db);
            //保存对标检测表信息表
            tsbConf.getDbm_dtable_info().add(db);
            //保存对标检测字段信息表
            tsbConf.getDbm_dtcol_info_list().forEach(dbm_dtcol_info -> dbm_dtcol_info.add(db));
            //保存对标检测结果表
            tsbConf.getDbm_normbmd_result_list().forEach(dbm_normbmd_result -> dbm_normbmd_result.add(db));
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
        }
    }

    @Method(desc = "设置对标检测记录",
            logicStep = "设置对标检测记录")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    private void setDbmNormbmDetect(String data_layer) {
        //数据校验
        if (StringUtil.isBlank(data_layer)) {
            throw new BusinessException("数据来源类型为空!");
        }
        //设置 Dbm_normbm_detect
        Dbm_normbm_detect dbm_normbm_detect = new Dbm_normbm_detect();
        dbm_normbm_detect.setDetect_id(PrimayKeyGener.getNextId());
        dbm_normbm_detect.setDetect_name(dbm_normbm_detect.getDetect_id());
        dbm_normbm_detect.setSource_type(data_layer);
        dbm_normbm_detect.setIs_import(IsFlag.Fou.getCode());
        dbm_normbm_detect.setDetect_status(IsFlag.Shi.getCode());
        dbm_normbm_detect.setCreate_user(getUserId().toString());
        dbm_normbm_detect.setDetect_sdate(DateUtil.getSysDate());
        dbm_normbm_detect.setDetect_stime(DateUtil.getSysTime());
        tsbConf.setDbm_normbm_detect(dbm_normbm_detect);
    }

    @Method(desc = "设置对标检测表信息表", logicStep = "设置对标检测表信息表")
    @Param(name = "file_id", desc = "表源属性id", range = "String类型")
    private void setDbmDtableInfo(String file_id) {
        //根据表源属性id获取表信息
        Map<String, Object> dclBatchTableInfo = TSBQuery.gerDCLBatchTableInfoById(file_id);
        if (dclBatchTableInfo.isEmpty()) {
            throw new BusinessException("查询的表信息已经不存在!");
        }
        //设置 Dbm_dtable_info
        Dbm_dtable_info dbm_dtable_info = new Dbm_dtable_info();
        dbm_dtable_info.setDbm_tableid(PrimayKeyGener.getNextId());
        dbm_dtable_info.setTable_cname(dclBatchTableInfo.get("table_ch_name").toString());
        dbm_dtable_info.setTable_ename(dclBatchTableInfo.get("table_name").toString());
        dbm_dtable_info.setIs_external(IsFlag.Fou.getCode());
        //如果源表的描述为null,则设置为""
        if (null == dclBatchTableInfo.get("remark")) {
            dbm_dtable_info.setTable_remark("");
        } else {
            dbm_dtable_info.setTable_remark(dclBatchTableInfo.get("remark").toString());
        }
        dbm_dtable_info.setDetect_id(tsbConf.getDbm_normbm_detect().getDetect_id());
        dbm_dtable_info.setTable_id(dclBatchTableInfo.get("table_id").toString());
        dbm_dtable_info.setSource_id(dclBatchTableInfo.get("source_id").toString());
        dbm_dtable_info.setAgent_id(dclBatchTableInfo.get("agent_id").toString());
        dbm_dtable_info.setDatabase_id(dclBatchTableInfo.get("database_id").toString());
        tsbConf.setDbm_dtable_info(dbm_dtable_info);
    }

    @Method(desc = "设置对标检测结果表",
            logicStep = "逻辑说明")
    @Param(name = "dbm_normbmd_info_list", desc = "对标结果集合",
            range = "List类型,[{col_id:'',col_similarity:'',standard_id:'',is_artificial:''}]")
    private static void setDbmNormbmdResult(List<Map<String, String>> dbm_normbmd_info_list) {
        List<Dbm_normbmd_result> dbm_normbmd_results_list = new ArrayList<>();
        dbm_normbmd_info_list.forEach(dbm_normbmd_info -> {
            //设置对标检测结果对象
            Dbm_normbmd_result dbm_normbmd_result = new Dbm_normbmd_result();
            dbm_normbmd_result.setResult_id(PrimayKeyGener.getNextId());
            dbm_normbmd_result.setCol_similarity(dbm_normbmd_info.get("col_similarity"));
            dbm_normbmd_result.setRemark_similarity("0");
            dbm_normbmd_result.setDetect_id(tsbConf.getDbm_normbm_detect().getDetect_id());
            dbm_normbmd_result.setCol_id(dbm_normbmd_info.get("col_id"));
            //根据 standard_id 获取对应标准元id
            dbm_normbmd_result.setBasic_id(getDbmNormbasicInfoById(dbm_normbmd_info.get("standard_id")));
            dbm_normbmd_result.setIs_artificial(dbm_normbmd_info.get("is_artificial"));
            dbm_normbmd_results_list.add(dbm_normbmd_result);
            tsbConf.setDbm_normbmd_result_list(dbm_normbmd_results_list);
        });
    }

    @Method(desc = "解析字段类型", logicStep = "解析字段类型")
    @Param(name = "col_type", desc = "字段类型", range = "String类型字符串")
    @Return(desc = "字段类型的Map", range = "data_type:数据类型,data_len:数据长度,decimal_point:数据小数长度")
    private static Map<String, String> parsingFiledType(String col_type) {
        Map<String, String> map = new HashMap<>();
        if (col_type.indexOf('(') != -1) {
            //数据类型
            String data_type = col_type.substring(0, col_type.indexOf('('));
            map.put("data_type", data_type);
            //数据长度
            String substring = col_type.substring(col_type.indexOf('(') + 1, col_type.lastIndexOf(')'));
            map.put("data_len", substring.split(",")[0]);
            //小数长度
            if (substring.split(",").length == 1) {
                map.put("decimal_point", "0");
            } else {
                map.put("decimal_point", substring.split(",")[1]);
            }
        } else {
            map.put("data_type", col_type);
            map.put("data_len", "0");
            map.put("decimal_point", "0");
        }
        if (map.isEmpty()) {
            throw new BusinessException("字段类型解析失败!");
        }
        return map;
    }

    @Method(desc = "根据标准编号获取标准id", logicStep = "根据标准编号获取标准id")
    @Param(name = "norm_code", desc = "标准编码", range = "String类型,该值唯一")
    @Return(desc = "basic_id标准元id", range = "basic_id标准元id")
    private static Long getDbmNormbasicInfoById(String norm_code) {
        List<Long> list = Dbo.queryOneColumnList("select basic_id from " + Dbm_normbasic.TableName +
                " where norm_code = ?", norm_code);
        if ((list.size() != 1)) {
            throw new BusinessException(norm_code + "标准编号已不存在或者标准编号不唯一,请检查标准信息!");
        }
        return list.get(0);
    }
}
