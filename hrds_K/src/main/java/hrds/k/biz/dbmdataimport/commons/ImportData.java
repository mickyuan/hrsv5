package hrds.k.biz.dbmdataimport.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.utils.ExcelUtil;
import hrds.commons.utils.User;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

@DocClass(desc = "导入数据", author = "BY-HLL", createdate = "2020/2/23 0023 上午 11:13")
public class ImportData {

    @Method(desc = "导入标准分类信息",
            logicStep = "1.获取标准分类信息列表" +
                    "2.导入标准分类信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    @Param(name = "user", desc = "User对象", range = "User")
    public static void importDbmSortInfoData(Workbook workbook, User user) {
        //1.获取标准分类信息列表
        //sheetIndex为1代表第二个sheet页面或者sheetName为sheet页名称 ExcelUtil.readExcel(workbook, "基础标准分类体系");
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "基础标准分类体系");
        String categoryTopic;
        String rootClassify;
        String subClassify;
        long categoryTopicId = 0L;
        long rootClassifyId = 0L;
        for (int i = 1; i < lists.size(); i++) {
            Dbm_sort_info dbm_sort_info = new Dbm_sort_info();
            dbm_sort_info.setSort_id(PrimayKeyGener.getNextId());
            //分类主题
            if (StringUtil.isNotBlank(lists.get(i).get(0).toString())) {
                categoryTopic = lists.get(i).get(0).toString();
                dbm_sort_info.setParent_id(0L);
                dbm_sort_info.setSort_level_num(0L);
                dbm_sort_info.setSort_name(categoryTopic);
                categoryTopicId = dbm_sort_info.getSort_id();
            }
            //分类大类
            if (StringUtil.isNotBlank(lists.get(i).get(1).toString())) {
                rootClassify = lists.get(i).get(1).toString();
                dbm_sort_info.setParent_id(categoryTopicId);
                dbm_sort_info.setSort_level_num(1L);
                dbm_sort_info.setSort_name(rootClassify);
                rootClassifyId = dbm_sort_info.getSort_id();
            }
            //分类子类
            if (StringUtil.isNotBlank(lists.get(i).get(2).toString()) && !"/".equals(lists.get(i).get(2).toString())) {
                subClassify = lists.get(i).get(2).toString();
                dbm_sort_info.setParent_id(rootClassifyId);
                dbm_sort_info.setSort_level_num(2L);
                dbm_sort_info.setSort_name(subClassify);
            }
            dbm_sort_info.setSort_remark(lists.get(i).get(3).toString());
            dbm_sort_info.setSort_status("0");
            dbm_sort_info.setCreate_user(user.getUserName());
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(Dbo.db());

        }
    }

    @Method(desc = "导入标准信息",
            logicStep = "1.获取标准信息列表" +
                    "2.导入标准信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    public static void importDbmNormbasicData(Workbook workbook) {
        //1.获取标准分类信息列表
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "数据标准");
        for (int i = 2; i < lists.size(); i++) {
            System.out.println(lists.get(i));
        }
    }

    @Method(desc = "导入标准代码类信息",
            logicStep = "1.标准代码类信息" +
                    "2.导入标准代码类信息")
    @Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
    public static void importDbmCodeTypeInfoData(Workbook workbook) {
        //1.获取标准分类信息列表
        List<List<Object>> lists = ExcelUtil.readExcel(workbook, "代码扩展定义");
        for (int i = 1; i < lists.size(); i++) {
            System.out.println(lists.get(i));
        }
    }
}
