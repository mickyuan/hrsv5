package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PackageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "系统层(SFL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class SFLDataQuery {

    @Method(desc = "获取系统层信息",
            logicStep = "1.获取系统层信息")
    @Return(desc = "系统层信息列表", range = "无限制")
    public static List<Map<String, Object>> getSFLDataInfos() {
        List<Map<String, Object>> sflDataInfos = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", Constant.SYS_DATA_TABLE);
        map.put("label", "系统数据表");
        map.put("parent_id", DataSourceType.SFL.getCode());
        map.put("description", "系统数据表");
        map.put("sysId", Constant.SYS_DATA_TABLE);
        map.put("sysname", "系统数据表");
        sflDataInfos.add(map);
        map = new HashMap<>();
        map.put("id", Constant.SYS_DATA_BAK);
        map.put("label", "系统数据备份");
        map.put("parent_id", DataSourceType.SFL.getCode());
        map.put("description", "系统数据表");
        map.put("sysId", Constant.SYS_DATA_BAK);
        map.put("sysname", "系统数据表");
        sflDataInfos.add(map);
        return sflDataInfos;
    }

    @Method(desc = "获取系统表信息",
            logicStep = "1.获取系统表信息")
    @Return(desc = "系统表信息列表", range = "无限制")
    public static List<String> getSFLTableInfos() {
        String packageName = "hrds.commons.entity";
        try {
            List<String> classNames = PackageUtil.getClassName(packageName, false);
            List<String> tableInfo = new ArrayList<>();
            if (null != classNames && !classNames.isEmpty()) {
                for (String className : classNames) {
                    tableInfo.add(StringUtil.replace(className, packageName + ".", "").toLowerCase());
                }
            }
            return tableInfo;
        } catch (Exception e) {
            throw new BusinessException("获取系统表名失败！");
        }
    }

    @Method(desc = "获取系统表备份信息",
            logicStep = "1.获取系统表备份信息")
    @Return(desc = "系统表备份信息", range = "无限制")
    public static List<Map<String, Object>> getSFLDataBakInfos() {
        return Dbo.queryList("SELECT * from sys_dump");
    }

}
