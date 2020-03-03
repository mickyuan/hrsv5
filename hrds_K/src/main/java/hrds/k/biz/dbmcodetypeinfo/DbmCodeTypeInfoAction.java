package hrds.k.biz.dbmcodetypeinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_code_type_info;
import hrds.commons.entity.Dbm_normbasic;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "数据对标元管理代码类信息管理", author = "BY-HLL", createdate = "2020/2/14 0014 下午 05:34")
public class DbmCodeTypeInfoAction extends BaseAction {

    @Method(desc = "添加代码类信息",
            logicStep = "1.数据校验" +
                    "2.设置代码类信息" +
                    "3.添加代码类信息")
    @Param(name = "dbm_code_type_info", desc = "代码类信息的实体对象", range = "代码类信息的实体对象", isBean = true)
    public void addDbmCodeTypeInfo(Dbm_code_type_info dbm_code_type_info) {
        //1.数据校验
        if (StringUtil.isBlank(dbm_code_type_info.getCode_type_name())) {
            throw new BusinessException("代码类名称为空!" + dbm_code_type_info.getCode_type_name());
        }
        if (StringUtil.isBlank(dbm_code_type_info.getCode_status())) {
            throw new BusinessException("代码类发布状态为空!" + dbm_code_type_info.getCode_status());
        }
        //2.设置代码项信息
        dbm_code_type_info.setCode_type_id(PrimayKeyGener.getNextId());
        dbm_code_type_info.setCreate_user(getUserId().toString());
        dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
        dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
        //3.添加代码项信息
        dbm_code_type_info.add(Dbo.db());
    }

    @Method(desc = "删除代码类信息",
            logicStep = "1.检查待删除的代码类是否存在" +
                    "2.检查代码分类id是否存在" +
                    "3.根据代码分类id删除分类")
    @Param(name = "code_type_id", desc = "代码项id", range = "Int类型")
    public void deleteDbmCodeTypeInfo(long code_type_id) {
        //1.检查待删除的代码项是否存在
        if (checkCodeTypeIdIsNotExist(code_type_id)) {
            throw new BusinessException("删除的代码项已经不存在!");
        }
        //3.根据分类id删除分类
        DboExecute.deletesOrThrow("删除代码类分类失败!" + code_type_id, "DELETE FROM " +
                Dbm_code_type_info.TableName + " WHERE code_type_id = ? ", code_type_id);
    }

    @Method(desc = "修改分类信息",
            logicStep = "1.数据校验" +
                    "2.设置分类信息" +
                    "3.修改数据")
    @Param(name = "dbm_code_type_info", desc = "代码类信息的实体对象", range = "代码类信息的实体对象", isBean = true)
    public void updateDbmCodeTypeInfo(Dbm_code_type_info dbm_code_type_info) {
        //1.数据校验
        //1-1.检查分类id是否存在
        if (checkCodeTypeIdIsNotExist(dbm_code_type_info.getCode_type_id())) {
            throw new BusinessException("修改的代码类已经不存在!");
        }
        if (StringUtil.isBlank(dbm_code_type_info.getCode_type_name())) {
            throw new BusinessException("代码类名称不能为空!");
        }
        if (StringUtil.isBlank(dbm_code_type_info.getCode_status())) {
            throw new BusinessException("代码类发布状态不能为空!");
        }
        //2.设置分类信息
        //3.修改分类信息
        dbm_code_type_info.update(Dbo.db());
    }

    @Method(desc = "分页获取所有代码类信息", logicStep = "分页获取所有代码类信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "所有分类信息", range = "所有分类信息")
    public Map<String, Object> getDbmCodeTypeInfo(int currPage, int pageSize) {
        Map<String, Object> dbmDbmCodeTypeInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_code_type_info> dbmCodeTypeInfos =
                Dbo.queryPagedList(Dbm_code_type_info.class, page,
                        "select * from " + Dbm_code_type_info.TableName);
        dbmDbmCodeTypeInfoMap.put("dbmCodeTypeInfos", dbmCodeTypeInfos);
        dbmDbmCodeTypeInfoMap.put("totalSize", page.getTotalSize());
        return dbmDbmCodeTypeInfoMap;
    }

    @Method(desc = "获取所有代码类信息(只获取code_type_id和code_type_name)", logicStep = "获取所有代码类信息")
    @Return(desc = "所有分类信息(只获取code_type_id和code_type_name)", range = "所有分类信息")
    public Map<String, Object> getDbmCodeTypeIdAndNameInfo() {
        Map<String, Object> dbmDbmCodeTypeInfoMap = new HashMap<>();
        List<Map<String, Object>> dbmCodeTypeInfos =
                Dbo.queryList("select code_type_id,code_type_name from " + Dbm_code_type_info.TableName);
        dbmDbmCodeTypeInfoMap.put("dbmCodeTypeInfos", dbmCodeTypeInfos);
        dbmDbmCodeTypeInfoMap.put("totalSize", dbmCodeTypeInfos.size());
        return dbmDbmCodeTypeInfoMap;
    }

    @Method(desc = "根据Id获取代码分类信息",
            logicStep = "根据Id获取分类信息")
    @Param(name = "code_type_id", desc = "分类Id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Optional<Dbm_code_type_info> getDbmCodeTypeInfoById(long code_type_id) {
        //1.检查分类是否存在
        if (checkCodeTypeIdIsNotExist(code_type_id)) {
            throw new BusinessException("查询的分类已经不存在! code_type_id=" + code_type_id);
        }
        return Dbo.queryOneObject(Dbm_code_type_info.class, "select * from " + Dbm_code_type_info.TableName +
                " where code_type_id = ?", code_type_id);
    }

    @Method(desc = "根据发布状态获取代码分类信息",
            logicStep = "根据发布状态获取代码分类信息")
    @Param(name = "code_status", desc = "发布状态", range = "IsFlag 0:未发布,1:已发布")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> getDbmCodeTypeInfoByStatus(String code_status) {
        Map<String, Object> dbmDbmCodeTypeInfoMap = new HashMap<>();
        List<Dbm_code_type_info> dbmCodeTypeInfos = Dbo.queryList(Dbm_code_type_info.class,
                "select * from " + Dbm_code_type_info.TableName +
                        " where code_status = ? and create_user = ?", code_status, getUserId().toString());
        dbmDbmCodeTypeInfoMap.put("dbmCodeTypeInfos", dbmCodeTypeInfos);
        dbmDbmCodeTypeInfoMap.put("totalSize", dbmCodeTypeInfos.size());
        return dbmDbmCodeTypeInfoMap;
    }

    @Method(desc = "根据代码分类id发布代码分类",
            logicStep = "根据代码分类id发布代码分类")
    @Param(name = "code_type_id", desc = "代码分类id", range = "long类型")
    public void releaseDbmCodeTypeInfoById(long code_type_id) {
        int execute = Dbo.execute("update " + Dbm_code_type_info.TableName + " set code_status = ? where" +
                        " code_type_id = ? ",
                IsFlag.Shi.getCode(), code_type_id);
        if (execute != 1) {
            throw new BusinessException("标准分类发布失败！code_type_id" + code_type_id);
        }
    }

    @Method(desc = "根据代码分类id数组批量发布代码分类",
            logicStep = "根据代码分类id数组批量发布代码分类")
    @Param(name = "code_type_id_s", desc = "代码分类id数组", range = "long类型数组")
    public void batchReleaseDbmCodeTypeInfo(Long[] code_type_id_s) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("update " + Dbm_code_type_info.TableName + " set code_status = ? where create_user=?");
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(getUserId().toString());
        asmSql.addORParam("code_type_id ", code_type_id_s);
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据标准id数组批量删除标准",
            logicStep = "根据标准id数组批量删除标准")
    @Param(name = "sort_id_s", desc = "标准分类id", range = "long类型数组")
    public void batchDeleteDbmCodeTypeInfo(Long[] code_type_id_s) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("delete from " + Dbm_code_type_info.TableName + " where create_user=?");
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(getUserId().toString());
        asmSql.addORParam("code_type_id ", code_type_id_s);
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "检查代码分类id是否存在", logicStep = "检查代码分类id是否存在")
    @Param(name = "code_type_id", desc = "代码分类id", range = "long类型")
    @Return(desc = "分类否存在", range = "true：不存在，false：存在")
    private boolean checkCodeTypeIdIsNotExist(long code_type_id) {
        //1.根据 code_type_id 检查分类是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
        return Dbo.queryNumber("SELECT COUNT(code_type_id) FROM " + Dbm_code_type_info.TableName +
                " WHERE code_type_id = ?", code_type_id).orElseThrow(() ->
                new BusinessException("检查分类id否存在的SQL编写错误")) != 1;
    }

    @Method(desc = "检查代码分类是否存在", logicStep = "检查代码分类是否存在")
    @Param(name = "code_encode", desc = "分类编码", range = "String类型")
    @Return(desc = "父分类是否存在", range = "true：存在，false：不存在")
    private boolean checkCodeEncodeIsRepeat(String code_encode) {
        //1.根据 code_type_id 检查代码分类是否存在
        return Dbo.queryNumber("select count(code_type_id) count from " + Dbm_code_type_info.TableName +
                        " WHERE code_encode =?",
                code_encode).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }
}
