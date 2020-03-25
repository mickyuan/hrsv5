package hrds.k.biz.dbm.codeiteminfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Dbm_code_item_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dbm.codetypeinfo.DbmCodeTypeInfoAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "数据对标元管理代码项信息管理类", author = "BY-HLL", createdate = "2020/2/13 0013 下午 01:56")
public class DbmCodeItemInfoAction extends BaseAction {

    @Method(desc = "添加代码项信息",
            logicStep = "1.数据校验" +
                    "2.设置代码项信息" +
                    "3.添加代码项信息")
    @Param(name = "dbm_code_item_info", desc = "代码项的实体对象", range = "代码项的实体对象", isBean = true)
    public void addDbmCodeItemInfo(Dbm_code_item_info dbm_code_item_info) {
        //1.数据校验
        if (StringUtil.isBlank(dbm_code_item_info.getCode_item_name())) {
            throw new BusinessException("代码项名称为空!" + dbm_code_item_info.getCode_item_name());
        }
        if (StringUtil.isBlank(dbm_code_item_info.getCode_type_id().toString())) {
            throw new BusinessException("代码项分类为空!" + dbm_code_item_info.getCode_type_id());
        }
        if (DbmCodeTypeInfoAction.checkCodeTypeIdIsNotExist(dbm_code_item_info.getCode_type_id())) {
            throw new BusinessException("代码项分类已经不存在!" + dbm_code_item_info.getCode_type_id());
        }
        //2.设置代码项信息
        dbm_code_item_info.setCode_item_id(PrimayKeyGener.getNextId());
        //3.添加代码项信息
        dbm_code_item_info.add(Dbo.db());
    }

    @Method(desc = "删除代码项信息",
            logicStep = "1.检查待删除的代码项是否存在" +
                    "2.检查代码项是否存在" +
                    "3.根据代码项id删除分类")
    @Param(name = "code_item_id", desc = "代码项id", range = "Int类型")
    public void deleteDbmCodeItemInfo(long code_item_id) {
        //1.检查待删除的代码项是否存在
        if (checkCodeItemIdIsNotExist(code_item_id)) {
            throw new BusinessException("删除的代码项已经不存在!");
        }
        //3.根据代码项id删除
        DboExecute.deletesOrThrow("删除代码项失败!" + code_item_id, "DELETE FROM " +
                Dbm_code_item_info.TableName + " WHERE code_item_id = ? ", code_item_id);
    }

    @Method(desc = "修改代码项信息",
            logicStep = "1.数据校验" +
                    "2.设置分类信息" +
                    "3.修改数据")
    @Param(name = "dbm_code_item_info", desc = "代码项的实体对象", range = "代码项的实体对象", isBean = true)
    public void updateDbmCodeItemInfo(Dbm_code_item_info dbm_code_item_info) {
        //1.数据校验
        //1-1.检查分类id是否存在
        if (checkCodeItemIdIsNotExist(dbm_code_item_info.getCode_item_id())) {
            throw new BusinessException("修改的代码项已经不存在!");
        }
        if (StringUtil.isBlank(dbm_code_item_info.getCode_item_name())) {
            throw new BusinessException("代码项名称不能为空!");
        }
        if (StringUtil.isBlank(dbm_code_item_info.getDbm_level())) {
            throw new BusinessException("代码项层级不能为空!");
        }
        if (StringUtil.isBlank(dbm_code_item_info.getCode_type_id().toString())) {
            throw new BusinessException("代码项所属代码类不能为空!");
        }
        //2.设置分类信息
        //3.修改分类信息
        dbm_code_item_info.update(Dbo.db());
    }

    @Method(desc = "根据代码分类id获取所有代码项信息", logicStep = "根据代码分类id获取所有代码项信息")
    @Param(name = "code_type_id", desc = "代码项分类id", range = "Long类型值")
    @Return(desc = "分类下所有代码项信息", range = "分类下所有代码项信息")
    public Map<String, Object> getDbmCodeItemInfoByCodeTypeId(long code_type_id) {
        Map<String, Object> dbmCodeItemInfoMap = new HashMap<>();
        List<Dbm_code_item_info> dbmCodeItemInfos = Dbo.queryList(Dbm_code_item_info.class,
                "select * from " + Dbm_code_item_info.TableName + " where code_type_id=?", code_type_id);
        dbmCodeItemInfoMap.put("dbmCodeItemInfos", dbmCodeItemInfos);
        return dbmCodeItemInfoMap;
    }

    @Method(desc = "根据代码项id获取所有代码项信息", logicStep = "根据代码项id获取所有代码项信息")
    @Param(name = "code_item_id", desc = "代码项id", range = "Long类型值")
    @Return(desc = "单个代码项信息", range = "单个代码项信息")
    public Optional<Dbm_code_item_info> getDbmCodeItemInfoById(long code_item_id) {
        //1.检查代码项是否存在
        if (checkCodeItemIdIsNotExist(code_item_id)) {
            throw new BusinessException("代码项已经不存在! code_item_id=" + code_item_id);
        }
        return Dbo.queryOneObject(Dbm_code_item_info.class, "select * from " + Dbm_code_item_info.TableName +
                " where code_item_id = ?", code_item_id);
    }

    @Method(desc = "根据代码项id数组批量删除代码项",
            logicStep = "根据代码项id数组批量删除代码项")
    @Param(name = "code_item_id_s", desc = "代码项id数组", range = "long类型数组")
    public void batchDeleteDbmCodeItemInfo(Long[] code_item_id_s) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("delete from " + Dbm_code_item_info.TableName + " where");
        asmSql.addORParam(" code_item_id ", code_item_id_s, "");
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "检索代码项信息",
            logicStep = "检索代码项信息")
    @Param(name = "search_cond", desc = "检索字符串", range = "String类型,任意值")
    @Param(name = "code_type_id", desc = "代码项分类id", range = "long类型值")
    @Return(desc = "代码项信息列表", range = "代码项信息列表")
    public Map<String, Object> searchDbmCodeItemInfo(String search_cond, long code_type_id) {
        if (StringUtil.isBlank(search_cond)) {
            throw new BusinessException("搜索条件不能为空!" + search_cond);
        }
        Map<String, Object> dbmCodeItemInfoMap = new HashMap<>();
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_code_item_info.TableName)
                .addSql(" where code_type_id = ? and (").addParam(code_type_id)
                .addLikeParam("code_encode", '%' + search_cond + '%', "")
                .addLikeParam("code_item_name", '%' + search_cond + '%', "or")
                .addLikeParam("code_value", '%' + search_cond + '%', "or")
                .addLikeParam("dbm_level", '%' + search_cond + '%', "or")
                .addLikeParam("code_remark", '%' + search_cond + '%', "or").addSql(")");
        List<Dbm_code_item_info> dbmCodeItemInfos = Dbo.queryList(Dbm_code_item_info.class, asmSql.sql(),
                asmSql.params());
        dbmCodeItemInfoMap.put("dbmCodeItemInfos", dbmCodeItemInfos);
        dbmCodeItemInfoMap.put("totalSize", dbmCodeItemInfos.size());
        return dbmCodeItemInfoMap;
    }

    @Method(desc = "检查代码项id是否存在", logicStep = "检查代码项id是否存在")
    @Param(name = "code_item_id", desc = "代码项id", range = "long类型")
    @Return(desc = "分类否存在", range = "true：不存在，false：存在")
    private boolean checkCodeItemIdIsNotExist(long code_item_id) {
        //1.根据 code_item_id 检查代码项是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
        return Dbo.queryNumber("SELECT COUNT(code_item_id) FROM " + Dbm_code_item_info.TableName +
                " WHERE code_item_id = ?", code_item_id).orElseThrow(() ->
                new BusinessException("检查代码项id否存在的SQL编写错误")) != 1;
    }
}
