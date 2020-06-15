package hrds.k.biz.dbm.dataimport.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DbmDataType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_code_item_info;
import hrds.commons.entity.Dbm_code_type_info;
import hrds.commons.entity.Dbm_normbasic;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ExcelUtil;
import hrds.commons.utils.User;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "导入数据", author = "BY-HLL", createdate = "2020/2/23 0023 上午 11:13")
public class ImportData {

    //标准分类名和分类id的Map列表
    private static Map<String, Object> sortInfoIdAndNameMap = new HashMap<>();
    //代码分类名和分类id的Map列表
    private static Map<String, String> codeTypeInfoIdAndNameMap = new HashMap<>();

    @Method(desc = "导入标准分类信息",
            logicStep = "1.获取标准分类信息列表" +
                    "2.导入标准分类信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    @Param(name = "user", desc = "User对象", range = "User")
    public static void importDbmSortInfoData(Workbook workbook, User user) {
        //1.获取标准分类信息列表
        //sheetIndex为1代表第二个sheet页面或者sheetName为sheet页名称 ExcelUtil.readExcel(workbook, "基础标准分类体系");
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "基础标准分类体系");
        String categoryTopic = "";
        String rootClassify = "";
        String subClassify;
        long categoryTopicId = 0L;
        long rootClassifyId = 0L;
        Object[] dbm_sort_info_obj;
        List<Object[]> dbm_sort_info_pool = new ArrayList<>();
        for (int i = 1; i < lists.size(); i++) {
            dbm_sort_info_obj = new Object[9];
            Dbm_sort_info dbm_sort_info = new Dbm_sort_info();
            dbm_sort_info.setSort_id(PrimayKeyGener.getNextId());
            dbm_sort_info.setSort_remark(lists.get(i).get(3).toString());
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(user.getUserId().toString());
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info_obj[0] = dbm_sort_info.getSort_id();
            dbm_sort_info_obj[4] = dbm_sort_info.getSort_remark();
            dbm_sort_info_obj[5] = dbm_sort_info.getSort_status();
            dbm_sort_info_obj[6] = dbm_sort_info.getCreate_user();
            dbm_sort_info_obj[7] = dbm_sort_info.getCreate_date();
            dbm_sort_info_obj[8] = dbm_sort_info.getCreate_time();
            //分类主题
            if (StringUtil.isNotBlank(lists.get(i).get(0).toString())) {
                categoryTopic = lists.get(i).get(0).toString();
                dbm_sort_info.setParent_id(0L);
                dbm_sort_info.setSort_level_num(0L);
                dbm_sort_info.setSort_name(categoryTopic);
                dbm_sort_info_obj[1] = dbm_sort_info.getParent_id();
                dbm_sort_info_obj[2] = dbm_sort_info.getSort_level_num();
                dbm_sort_info_obj[3] = dbm_sort_info.getSort_name();
                categoryTopicId = dbm_sort_info.getSort_id();
                sortInfoIdAndNameMap.put(categoryTopic, dbm_sort_info.getSort_id());
                dbm_sort_info_pool.add(dbm_sort_info_obj);
            }
            //分类大类
            if (StringUtil.isNotBlank(lists.get(i).get(1).toString())) {
                rootClassify = lists.get(i).get(1).toString();
                dbm_sort_info.setParent_id(categoryTopicId);
                dbm_sort_info.setSort_level_num(1L);
                dbm_sort_info.setSort_name(rootClassify);
                dbm_sort_info_obj[1] = dbm_sort_info.getParent_id();
                dbm_sort_info_obj[2] = dbm_sort_info.getSort_level_num();
                dbm_sort_info_obj[3] = dbm_sort_info.getSort_name();
                rootClassifyId = dbm_sort_info.getSort_id();
                sortInfoIdAndNameMap.put(categoryTopic + rootClassify, dbm_sort_info.getSort_id());
                dbm_sort_info_pool.add(dbm_sort_info_obj);
                // 如果一条数据包含大类和子类,则同时添加子类信息
                if (StringUtil.isNotBlank(lists.get(i).get(2).toString())
                        && !"/".equals(lists.get(i).get(2).toString())) {
                    dbm_sort_info_obj = new Object[9];
                    subClassify = lists.get(i).get(2).toString();
                    dbm_sort_info.setSort_id(PrimayKeyGener.getNextId());
                    dbm_sort_info.setParent_id(rootClassifyId);
                    dbm_sort_info.setSort_level_num(2L);
                    dbm_sort_info.setSort_name(subClassify);
                    dbm_sort_info.setSort_remark(lists.get(i).get(3).toString());
                    dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
                    dbm_sort_info.setCreate_user(user.getUserId().toString());
                    dbm_sort_info.setCreate_date(DateUtil.getSysDate());
                    dbm_sort_info.setCreate_time(DateUtil.getSysTime());
                    dbm_sort_info_obj[0] = dbm_sort_info.getSort_id();
                    dbm_sort_info_obj[1] = dbm_sort_info.getParent_id();
                    dbm_sort_info_obj[2] = dbm_sort_info.getSort_level_num();
                    dbm_sort_info_obj[3] = dbm_sort_info.getSort_name();
                    dbm_sort_info_obj[4] = dbm_sort_info.getSort_remark();
                    dbm_sort_info_obj[5] = dbm_sort_info.getSort_status();
                    dbm_sort_info_obj[6] = dbm_sort_info.getCreate_user();
                    dbm_sort_info_obj[7] = dbm_sort_info.getCreate_date();
                    dbm_sort_info_obj[8] = dbm_sort_info.getCreate_time();
                    sortInfoIdAndNameMap.put(categoryTopic + rootClassify + subClassify, dbm_sort_info.getSort_id());
                    dbm_sort_info_pool.add(dbm_sort_info_obj);
                }
            }
            //分类子类
            if (StringUtil.isBlank(lists.get(i).get(1).toString())
                    && StringUtil.isNotBlank(lists.get(i).get(2).toString())
                    && !"/".equals(lists.get(i).get(2).toString())) {
                subClassify = lists.get(i).get(2).toString();
                dbm_sort_info.setParent_id(rootClassifyId);
                dbm_sort_info.setSort_level_num(2L);
                dbm_sort_info.setSort_name(subClassify);
                dbm_sort_info_obj[1] = dbm_sort_info.getParent_id();
                dbm_sort_info_obj[2] = dbm_sort_info.getSort_level_num();
                dbm_sort_info_obj[3] = dbm_sort_info.getSort_name();
                sortInfoIdAndNameMap.put(categoryTopic + rootClassify + subClassify, dbm_sort_info.getSort_id());
                dbm_sort_info_pool.add(dbm_sort_info_obj);
            }
        }
        //批量插入数据
        Dbo.executeBatch("insert into dbm_sort_info(sort_id,parent_id,sort_level_num,sort_name,sort_remark," +
                "sort_status,create_user,create_date,create_time) values(?,?,?,?,?,?,?,?,?)", dbm_sort_info_pool);
    }

    @Method(desc = "导入标准代码类信息",
            logicStep = "1.标准代码类信息" +
                    "2.导入标准代码类信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    @Param(name = "user", desc = "User对象", range = "User")
    public static void importDbmCodeTypeInfoData(Workbook workbook, User user) {
        //1.获取标准分类信息列表
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "代码扩展定义");
        //初始化代码类信息Map (去重)
        for (int i = 1; i < lists.size(); i++) {
            codeTypeInfoIdAndNameMap.put(lists.get(i).get(2).toString(), "");
        }
        //设置代码类的id
        codeTypeInfoIdAndNameMap.forEach((code_type_name, code_type_id) ->
                codeTypeInfoIdAndNameMap.put(code_type_name, String.valueOf(PrimayKeyGener.getNextId())));
        List<Object[]> dbm_code_type_info_pool = new ArrayList<>();
        Dbm_code_type_info dbm_code_type_info = new Dbm_code_type_info();
        codeTypeInfoIdAndNameMap.forEach((code_type_name, code_type_id) -> {
            Object[] dbm_code_type_info_obj = new Object[8];
            dbm_code_type_info.setCode_type_id(code_type_id);
            dbm_code_type_info.setCode_type_name(code_type_name);
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(user.getUserId().toString());
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info_obj[0] = dbm_code_type_info.getCode_type_id();
            dbm_code_type_info_obj[1] = dbm_code_type_info.getCode_type_name();
            dbm_code_type_info_obj[2] = dbm_code_type_info.getCode_encode();
            dbm_code_type_info_obj[3] = dbm_code_type_info.getCode_remark();
            dbm_code_type_info_obj[4] = dbm_code_type_info.getCode_status();
            dbm_code_type_info_obj[5] = dbm_code_type_info.getCreate_user();
            dbm_code_type_info_obj[6] = dbm_code_type_info.getCreate_date();
            dbm_code_type_info_obj[7] = dbm_code_type_info.getCreate_time();
            dbm_code_type_info_pool.add(dbm_code_type_info_obj);
        });
        //批量插入数据
        Dbo.executeBatch("insert into dbm_code_type_info(code_type_id,code_type_name,code_encode,code_remark," +
                "code_status,create_user,create_date,create_time) values(?,?,?,?,?,?,?,?)", dbm_code_type_info_pool);
    }

    @Method(desc = "导入标准代码项信息",
            logicStep = "1.标准代码项信息" +
                    "2.导入标准代码项信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    public static void importDbmCodeItemInfoData(Workbook workbook) {
        //1.获取标准分类信息列表
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "代码扩展定义");
        List<Object[]> dbm_code_item_info_pool = new ArrayList<>();
        Dbm_code_item_info dbm_code_item_info = new Dbm_code_item_info();
        for (int i = 1; i < lists.size(); i++) {
            Object[] dbm_code_item_info_obj = new Object[7];
            dbm_code_item_info.setCode_item_id(PrimayKeyGener.getNextId());
            dbm_code_item_info.setCode_encode(lists.get(i).get(1).toString());
            dbm_code_item_info.setCode_value(lists.get(i).get(3).toString());
            dbm_code_item_info.setCode_item_name(lists.get(i).get(4).toString());
            dbm_code_item_info.setCode_remark(lists.get(i).get(5).toString());
            dbm_code_item_info.setDbm_level(lists.get(i).get(8).toString());
            dbm_code_item_info.setCode_type_id(codeTypeInfoIdAndNameMap.get(lists.get(i).get(2).toString()));
            dbm_code_item_info_obj[0] = dbm_code_item_info.getCode_item_id();
            dbm_code_item_info_obj[1] = dbm_code_item_info.getCode_encode();
            dbm_code_item_info_obj[2] = dbm_code_item_info.getCode_item_name();
            dbm_code_item_info_obj[3] = dbm_code_item_info.getCode_value();
            dbm_code_item_info_obj[4] = dbm_code_item_info.getDbm_level();
            dbm_code_item_info_obj[5] = dbm_code_item_info.getCode_remark();
            dbm_code_item_info_obj[6] = dbm_code_item_info.getCode_type_id();
            dbm_code_item_info_pool.add(dbm_code_item_info_obj);
        }
        //批量插入数据
        Dbo.executeBatch("insert into dbm_code_item_info(code_item_id,code_encode,code_item_name,code_value," +
                "dbm_level,code_remark,code_type_id) values(?,?,?,?,?,?,?)", dbm_code_item_info_pool);
    }

    @Method(desc = "导入标准信息",
            logicStep = "1.获取标准信息列表" +
                    "2.导入标准信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    @Param(name = "user", desc = "User对象", range = "User")
    public static void importDbmNormbasicData(Workbook workbook, User user) {
        //1.获取标准分类信息列表
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "数据标准");
        List<Object[]> dbm_normbasic_pool = new ArrayList<>();
        Dbm_normbasic dbm_normbasic = new Dbm_normbasic();
        for (int i = 2; i < lists.size(); i++) {
            Object[] dbm_normbasic_obj = new Object[23];
            // 获取标准归属分类的id
            String key = lists.get(i).get(1).toString() + lists.get(i).get(2).toString();
            if (StringUtil.isNotBlank(lists.get(i).get(3).toString())
                    && !"/".equals(lists.get(i).get(3).toString())) {
                key += lists.get(i).get(3).toString();
            }
            Object sort_id = sortInfoIdAndNameMap.get(key);
            // 设置标准信息对象
            dbm_normbasic.setBasic_id(PrimayKeyGener.getNextId());
            dbm_normbasic.setNorm_code(lists.get(i).get(0).toString());
            dbm_normbasic.setSort_id((Long) sort_id);
            dbm_normbasic.setNorm_cname(lists.get(i).get(4).toString());
            dbm_normbasic.setNorm_ename(lists.get(i).get(5).toString());
            if ("/".equals(lists.get(i).get(6).toString())) {
                dbm_normbasic.setNorm_aname("");
            } else {
                dbm_normbasic.setNorm_aname(lists.get(i).get(6).toString());
            }
            dbm_normbasic.setBusiness_def(lists.get(i).get(7).toString());
            dbm_normbasic.setBusiness_rule(lists.get(i).get(8).toString());
            dbm_normbasic.setDbm_domain(lists.get(i).get(9).toString());
            dbm_normbasic.setNorm_basis(lists.get(i).get(10).toString());
            if (StringUtil.isBlank(lists.get(i).get(11).toString())) {
                throw new BusinessException("数据类型为空!");
            }
            switch (lists.get(i).get(11).toString()) {
                case "编码类":
                    dbm_normbasic.setData_type(DbmDataType.BianMaLei.getCode());
                    break;
                case "标识类":
                    dbm_normbasic.setData_type(DbmDataType.BiaoShiLei.getCode());
                    break;
                case "代码类":
                    dbm_normbasic.setData_type(DbmDataType.DaiMaLei.getCode());
                    break;
                case "金额类":
                    dbm_normbasic.setData_type(DbmDataType.JinELei.getCode());
                    break;
                case "日期类":
                    dbm_normbasic.setData_type(DbmDataType.RiQiLei.getCode());
                    break;
                case "日期时间类":
                    dbm_normbasic.setData_type(DbmDataType.RiQiShiJianLei.getCode());
                    break;
                case "时间类":
                    dbm_normbasic.setData_type(DbmDataType.ShiJianLei.getCode());
                    break;
                case "数值类":
                    dbm_normbasic.setData_type(DbmDataType.ShuZhiLei.getCode());
                    break;
                case "文本类":
                    dbm_normbasic.setData_type(DbmDataType.WenBenLei.getCode());
                    break;
                default:
                    throw new BusinessException("数据类型不匹配!");
            }
            dbm_normbasic.setCol_len(lists.get(i).get(12).toString());
            if ("/".equals(lists.get(i).get(13).toString())) {
                dbm_normbasic.setDecimal_point(0L);
            } else {
                dbm_normbasic.setDecimal_point(lists.get(i).get(13).toString());
            }
            // 如果是代码类,添加代码类id作为标准外键
            dbm_normbasic.setCode_type_id(codeTypeInfoIdAndNameMap.get(lists.get(i).get(4).toString()));
            dbm_normbasic.setManage_department(lists.get(i).get(15).toString());
            dbm_normbasic.setRelevant_department(lists.get(i).get(16).toString());
            dbm_normbasic.setOrigin_system(lists.get(i).get(17).toString());
            dbm_normbasic.setRelated_system(lists.get(i).get(18).toString());
            dbm_normbasic.setFormulator(lists.get(i).get(20).toString());
            // 默认发布状态为未发布
            dbm_normbasic.setNorm_status(IsFlag.Fou.getCode());
            dbm_normbasic.setCreate_user(user.getUserId().toString());
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic_obj[0] = dbm_normbasic.getBasic_id();
            dbm_normbasic_obj[1] = dbm_normbasic.getNorm_code();
            dbm_normbasic_obj[2] = dbm_normbasic.getSort_id();
            dbm_normbasic_obj[3] = dbm_normbasic.getNorm_cname();
            dbm_normbasic_obj[4] = dbm_normbasic.getNorm_ename();
            dbm_normbasic_obj[5] = dbm_normbasic.getNorm_aname();
            dbm_normbasic_obj[6] = dbm_normbasic.getBusiness_def();
            dbm_normbasic_obj[7] = dbm_normbasic.getBusiness_rule();
            dbm_normbasic_obj[8] = dbm_normbasic.getDbm_domain();
            dbm_normbasic_obj[9] = dbm_normbasic.getNorm_basis();
            dbm_normbasic_obj[10] = dbm_normbasic.getData_type();
            dbm_normbasic_obj[11] = dbm_normbasic.getCode_type_id();
            dbm_normbasic_obj[12] = dbm_normbasic.getCol_len();
            dbm_normbasic_obj[13] = dbm_normbasic.getDecimal_point();
            dbm_normbasic_obj[14] = dbm_normbasic.getManage_department();
            dbm_normbasic_obj[15] = dbm_normbasic.getRelevant_department();
            dbm_normbasic_obj[16] = dbm_normbasic.getOrigin_system();
            dbm_normbasic_obj[17] = dbm_normbasic.getRelated_system();
            dbm_normbasic_obj[18] = dbm_normbasic.getFormulator();
            dbm_normbasic_obj[19] = dbm_normbasic.getNorm_status();
            dbm_normbasic_obj[20] = dbm_normbasic.getCreate_user();
            dbm_normbasic_obj[21] = dbm_normbasic.getCreate_date();
            dbm_normbasic_obj[22] = dbm_normbasic.getCreate_time();
            dbm_normbasic_pool.add(dbm_normbasic_obj);
        }
        //批量插入数据
        Dbo.executeBatch("insert into dbm_normbasic(basic_id,norm_code,sort_id,norm_cname,norm_ename,norm_aname," +
                        "business_def,business_rule,dbm_domain,norm_basis,data_type,code_type_id,col_len," +
                        "decimal_point,manage_department,relevant_department,origin_system,related_system,formulator," +
                        "norm_status,create_user,create_date,create_time) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                dbm_normbasic_pool);
    }
}
