package hrds.k.biz.dbm.normbasic;

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
import hrds.commons.codes.UserType;
import hrds.commons.entity.Dbm_normbasic;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "数据对标元管理标准管理类", author = "BY-HLL", createdate = "2020/2/16 0016 下午 02:09")
public class DbmNormbasicAction extends BaseAction {

    @Method(desc = "添加标准分类",
            logicStep = "1.数据校验" +
                    "1-1.分类名不能为空" +
                    "1-2.分类名称重复" +
                    "1-3.父分类不为空的情况下,检查上级分类是否存在" +
                    "2.设置标准分类信息" +
                    "3.添加标准分类信息")
    @Param(name = "dbm_normbasic", desc = "标准分类的实体对象", range = "标准分类的实体对象", isBean = true)
    public void addDbmNormbasicInfo(Dbm_normbasic dbm_normbasic) {
        //1.数据校验
        if (StringUtil.isNotBlank(dbm_normbasic.getSort_id().toString())) {
            if (checkSortIdIsNotExist(dbm_normbasic.getSort_id())) {
                throw new BusinessException("选择分类不存在!" + dbm_normbasic.getSort_id());
            }
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_cname())) {
            throw new BusinessException("标准中文名称为空!" + dbm_normbasic.getNorm_cname());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_ename())) {
            throw new BusinessException("标准英文名称为空!" + dbm_normbasic.getNorm_ename());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_aname())) {
            throw new BusinessException("标准别名为空!" + dbm_normbasic.getNorm_aname());
        }
        if (StringUtil.isBlank(dbm_normbasic.getDbm_domain())) {
            throw new BusinessException("标准值域为空!" + dbm_normbasic.getDbm_domain());
        }
        if (StringUtil.isBlank(dbm_normbasic.getCol_len().toString())) {
            throw new BusinessException("标准字段长度为空!" + dbm_normbasic.getCol_len());
        }
        if (StringUtil.isBlank(dbm_normbasic.getDecimal_point().toString())) {
            throw new BusinessException("标准小数长度为空!" + dbm_normbasic.getDecimal_point());
        }
        if (StringUtil.isBlank(dbm_normbasic.getFormulator())) {
            throw new BusinessException("标准制定人为空!" + dbm_normbasic.getFormulator());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_status())) {
            throw new BusinessException("标准发布状态为空!" + dbm_normbasic.getNorm_status());
        }
        //2.设置标准分类信息
        dbm_normbasic.setBasic_id(PrimayKeyGener.getNextId());
        dbm_normbasic.setCreate_user(getUserId().toString());
        dbm_normbasic.setCreate_date(DateUtil.getSysDate());
        dbm_normbasic.setCreate_time(DateUtil.getSysTime());
        //3.添加标准分类信息
        dbm_normbasic.add(Dbo.db());
    }

    @Method(desc = "删除标准信息",
            logicStep = "1.检查标准id是否存在" +
                    "2.根据标准id删除分类")
    @Param(name = "basic_id", desc = "标准id", range = "Int类型")
    public void deleteDbmNormbasicInfo(long basic_id) {
        //1.检查分类id是否存在
        if (checkBasicIdIsNotExist(basic_id)) {
            throw new BusinessException("删除的标准已经不存在! basic_id" + basic_id);
        }
        //2.根据分类id删除分类
        DboExecute.deletesOrThrow("删除标准失败!" + basic_id, "DELETE FROM " +
                Dbm_normbasic.TableName + " WHERE basic_id = ? ", basic_id);
    }

    @Method(desc = "修改标准信息",
            logicStep = "1.数据校验" +
                    "2.设置标准信息" +
                    "3.修改数据")
    @Param(name = "dbm_normbasic", desc = "标准信息的实体对象", range = "标准信息的实体对象", isBean = true)
    public void updateDbmNormbasicInfo(Dbm_normbasic dbm_normbasic) {
        if (checkBasicIdIsNotExist(dbm_normbasic.getBasic_id())) {
            throw new BusinessException("修改的分类已经不存在! basic_id=" + dbm_normbasic.getBasic_id());
        }
        if (StringUtil.isBlank(dbm_normbasic.getSort_id().toString())) {
            throw new BusinessException("所属分类为空! sort_id=" + dbm_normbasic.getSort_id());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_cname())) {
            throw new BusinessException("标准中文名字为空!" + dbm_normbasic.getNorm_cname());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_ename())) {
            throw new BusinessException("标准英文名字为空!" + dbm_normbasic.getNorm_ename());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_aname())) {
            throw new BusinessException("标准别名为空!" + dbm_normbasic.getNorm_aname());
        }
        if (StringUtil.isBlank(dbm_normbasic.getDbm_domain())) {
            throw new BusinessException("标准值域为空!" + dbm_normbasic.getDbm_domain());
        }
        if (StringUtil.isBlank(dbm_normbasic.getCol_len().toString())) {
            throw new BusinessException("字段长度为空!" + dbm_normbasic.getCol_len());
        }
        if (StringUtil.isBlank(dbm_normbasic.getDecimal_point().toString())) {
            throw new BusinessException("小数长度为空!" + dbm_normbasic.getDecimal_point());
        }
        if (StringUtil.isBlank(dbm_normbasic.getFormulator())) {
            throw new BusinessException("制定人为空!" + dbm_normbasic.getFormulator());
        }
        if (StringUtil.isBlank(dbm_normbasic.getNorm_status())) {
            throw new BusinessException("发布状态为空!" + dbm_normbasic.getNorm_status());
        }
        dbm_normbasic.update(Dbo.db());
    }

    @Method(desc = "获取所有标准信息", logicStep = "获取所有标准信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "所有分类信息", range = "所有分类信息")
    public Map<String, Object> getDbmNormbasicInfo(int currPage, int pageSize) {
        //设置查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_normbasic.TableName + " where");
        //如果用户是对标管理员,则校验并根据状态和创建用户查询
        if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql(" create_user = ?").addParam(getUserId().toString());
        }
        //如果是对标操作员,则检索已经发布的
        else if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql(" norm_status = ?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标数据权限!");
        }
        //查询并返回数据
        Map<String, Object> dbmNormbasicInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_normbasic> dbmNormbasicInfos = Dbo.queryPagedList(Dbm_normbasic.class, page, asmSql.sql(),
                asmSql.params());
        dbmNormbasicInfoMap.put("dbmNormbasicInfos", dbmNormbasicInfos);
        dbmNormbasicInfoMap.put("totalSize", page.getTotalSize());
        return dbmNormbasicInfoMap;
    }

    @Method(desc = "获取所有标准信息(只获取basic_id,norm_cname)", logicStep = "获取所有标准信息")
    @Return(desc = "所有分类信息(只获取basic_id,norm_cname)", range = "所有分类信息")
    public Map<String, Object> getDbmNormbasicIdAndNameInfo() {
        //设置查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select basic_id,norm_cname from " + Dbm_normbasic.TableName + " where");
        //如果用户是对标管理员,则校验并根据状态和创建用户查询
        if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql(" create_user = ?").addParam(getUserId().toString());
        }
        //如果是对标操作员,则检索已经发布的
        else if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql(" norm_status = ?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标数据权限!");
        }
        //查询并返回数据
        Map<String, Object> dbmNormbasicInfoMap = new HashMap<>();
        List<Map<String, Object>> dbmNormbasicInfos = Dbo.queryList(asmSql.sql(), asmSql.params());
        dbmNormbasicInfoMap.put("dbmNormbasicInfos", dbmNormbasicInfos);
        dbmNormbasicInfoMap.put("totalSize", dbmNormbasicInfos.size());
        return dbmNormbasicInfoMap;
    }

    @Method(desc = "根据Id获取标准信息",
            logicStep = "根据Id获取标准信息")
    @Param(name = "basic_id", desc = "标准Id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Optional<Dbm_normbasic> getDbmNormbasicInfoById(long basic_id) {
        return Dbo.queryOneObject(Dbm_normbasic.class, "select * from " + Dbm_normbasic.TableName +
                " where basic_id = ?", basic_id);
    }

    @Method(desc = "根据sort_id获取标准信息",
            logicStep = "根据sort_id获取标准信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Param(name = "sort_id", desc = "分类id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> getDbmNormbasicInfoBySortId(int currPage, int pageSize, long sort_id) {
        //设置查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_normbasic.TableName + " where sort_id = ?").addParam(sort_id);
        //如果是对标操作员,则检索已经发布的
        if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql(" and create_user = ?").addParam(getUserId().toString());
        } else if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql(" and norm_status = ?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标数据权限!");
        }
        //查询并返回数据
        Map<String, Object> dbmNormbasicInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_normbasic> dbmNormbasicInfos = Dbo.queryPagedList(Dbm_normbasic.class, page, asmSql.sql(),
                asmSql.params());
        dbmNormbasicInfoMap.put("dbmNormbasicInfos", dbmNormbasicInfos);
        dbmNormbasicInfoMap.put("totalSize", page.getTotalSize());
        return dbmNormbasicInfoMap;
    }

    @Method(desc = "根据发布状态获取标准信息",
            logicStep = "根据发布状态获取标准信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Param(name = "norm_status", desc = "发布状态", range = "IsFlag 0:未发布,1:已发布")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> getDbmNormbasicByStatus(int currPage, int pageSize, String norm_status) {
        //设置查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_normbasic.TableName + " where");
        //如果用户是对标管理员,则校验并根据状态和创建用户查询
        if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql(" create_user = ?").addParam(getUserId().toString());
            if (StringUtil.isNotBlank(norm_status)) {
                asmSql.addSql(" and norm_status = ?").addParam(norm_status);
            }
        }
        //如果是对标操作员,则检索已经发布的
        else if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql(" norm_status = ?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标-标准数据数据权限!");
        }
        //查询并返回数据
        Map<String, Object> dbmNormbasicInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_normbasic> dbmNormbasicInfos = Dbo.queryPagedList(Dbm_normbasic.class, page, asmSql.sql(),
                asmSql.params());
        dbmNormbasicInfoMap.put("dbmNormbasicInfos", dbmNormbasicInfos);
        dbmNormbasicInfoMap.put("totalSize", page.getTotalSize());
        return dbmNormbasicInfoMap;
    }

    @Method(desc = "检索标准信息",
            logicStep = "检索标准信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Param(name = "search_cond", desc = "检索字符串", range = "String类型,任意值", valueIfNull = "")
    @Param(name = "status", desc = "发布状态", range = "IsFlag 0:未发布,1:已发布", nullable = true)
    @Param(name = "sort_id", desc = "sort_id所属分类", range = "String类型,分类id,唯一", valueIfNull = "")
    @Return(desc = "标准信息列表", range = "标准信息列表")
    public Map<String, Object> searchDbmNormbasic(int currPage, int pageSize, String search_cond, String status,
                                                  String sort_id) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_normbasic.TableName + " where");
        //如果用户是对标管理员,则校验并根据状态和创建用户查询
        if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql(" create_user = ?").addParam(getUserId().toString());
            if (StringUtil.isNotBlank(status)) {
                asmSql.addSql(" and norm_status = ?").addParam(status);
            }
        }
        //如果是对标操作员,则检索已经发布的
        else if (getUser().getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql(" norm_status = ?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标数据权限!");
        }
        if (StringUtil.isNotBlank(search_cond)) {
            asmSql.addSql(" and (");
            asmSql.addLikeParam("norm_code", '%' + search_cond + '%', "");
            asmSql.addLikeParam("norm_cname", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("norm_ename", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("norm_aname", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("business_def", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("business_rule", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("dbm_domain", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("norm_basis", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("manage_department", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("relevant_department", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("origin_system", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("related_system", '%' + search_cond + '%', "or");
            asmSql.addLikeParam("formulator", '%' + search_cond + '%').addSql(")");
        }
        //查询并返回数据
        Map<String, Object> dbmNormbasicMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_normbasic> dbmNormbasicInfos = Dbo.queryPagedList(Dbm_normbasic.class, page, asmSql.sql(),
                asmSql.params());
        dbmNormbasicMap.put("dbmNormbasicInfos", dbmNormbasicInfos);
        dbmNormbasicMap.put("totalSize", page.getTotalSize());
        return dbmNormbasicMap;
    }

    @Method(desc = "根据标准id发布标准",
            logicStep = "根据标准id发布标准")
    @Param(name = "basic_id", desc = "标准id", range = "long类型")
    public void releaseDbmNormbasicById(long basic_id) {
        int execute = Dbo.execute("update " + Dbm_normbasic.TableName + " set norm_status = ? where" +
                        " basic_id = ? ",
                IsFlag.Shi.getCode(), basic_id);
        if (execute != 1) {
            throw new BusinessException("标准发布失败！basic_id" + basic_id);
        }
    }

    @Method(desc = "根据标准id数组批量发布标准",
            logicStep = "根据标准id数组批量发布标准")
    @Param(name = "basic_id_s", desc = "标准id数组", range = "long类型数组")
    public void batchReleaseDbmNormbasic(Long[] basic_id_s) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("update " + Dbm_normbasic.TableName + " set norm_status = ? where create_user=?");
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(getUserId().toString());
        asmSql.addORParam("basic_id ", basic_id_s);
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据标准id数组批量删除标准",
            logicStep = "根据标准id数组批量删除标准")
    @Param(name = "basic_id_s", desc = "标准id数组", range = "long类型数组")
    public void batchDeleteDbmNormbasic(Long[] basic_id_s) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("delete from " + Dbm_normbasic.TableName + " where create_user=?");
        asmSql.addParam(getUserId().toString());
        asmSql.addORParam("basic_id ", basic_id_s);
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "检查标准编号是否存在", logicStep = "检查标准编号是否存在")
    @Param(name = "norm_code", desc = "分类名称", range = "String类型，长度为10，该值唯一", example = "国籍")
    @Return(desc = "分类名称是否存在", range = "true：存在，false：不存在")
    private boolean checkNormCodeIsRepeat(String norm_code) {
        //1.根据 sort_name 检查名称是否重复
        return Dbo.queryNumber("select count(norm_code) count from " + Dbm_normbasic.TableName +
                        " WHERE norm_code =?",
                norm_code).orElseThrow(() -> new BusinessException("检查标准编号是否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查分类id是否存在", logicStep = "检查分类id是否存在")
    @Param(name = "sort_id", desc = "分类id", range = "long类型")
    @Return(desc = "分类否存在", range = "true：不存在，false：存在")
    private boolean checkSortIdIsNotExist(long sort_id) {
        //1.根据 sort_id 检查分类是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
        return Dbo.queryNumber("SELECT COUNT(sort_id) FROM " + Dbm_sort_info.TableName +
                " WHERE sort_id = ?", sort_id).orElseThrow(() ->
                new BusinessException("检查分类id否存在的SQL编写错误")) != 1;
    }

    @Method(desc = "检查标准id是否存在", logicStep = "检查标准id是否存在")
    @Param(name = "basic_id", desc = "标准id", range = "long类型")
    @Return(desc = "标准是否存在", range = "true：不存在，false：存在")
    private boolean checkBasicIdIsNotExist(long basic_id) {
        //1.根据 sort_id 检查分类是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
        return Dbo.queryNumber("SELECT COUNT(basic_id) FROM " + Dbm_normbasic.TableName +
                " WHERE basic_id = ?", basic_id).orElseThrow(() ->
                new BusinessException("检查分类id否存在的SQL编写错误")) != 1;
    }
}
