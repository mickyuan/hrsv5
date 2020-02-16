package hrds.k.biz.dbmsortinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_normbasic;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "数据对标元管理标准分类管理类", author = "BY-HLL", createdate = "2020/2/11 0012 上午 11:33")
public class DbmSortInfoAction extends BaseAction {

    @Method(desc = "添加标准分类",
            logicStep = "1.数据校验" +
                    "1-1.分类名不能为空" +
                    "1-2.分类名称重复" +
                    "1-3.父分类不为空的情况下,检查上级分类是否存在" +
                    "2.设置标准分类信息" +
                    "3.添加标准分类信息")
    @Param(name = "standardClassifyInfo", desc = "标准分类的实体对象", range = "标准分类的实体对象", isBean = true)
    public void addDbmSortInfo(Dbm_sort_info dbm_sort_info) {
        //1.数据校验
        //1-1.分类名不能为空
        if (StringUtil.isBlank(dbm_sort_info.getSort_name())) {
            throw new BusinessException("标准分类名称为空!" + dbm_sort_info.getSort_name());
        }
        //1-2.分类名称重复
        if (checkSortNameIsRepeat(dbm_sort_info.getSort_name())) {
            throw new BusinessException("分类名称已经存在!" + dbm_sort_info.getSort_name());
        }
        //1-3.父分类不为空的情况下,检查上级分类是否存在
        if (StringUtil.isNotBlank(dbm_sort_info.getParent_id().toString())) {
            if (!checkParentIdIsRepeat(dbm_sort_info.getParent_id())) {
                throw new BusinessException("选择父分类名称不存在!" + dbm_sort_info.getParent_id());
            }
        }
        //2.设置标准分类信息
        dbm_sort_info.setSort_id(PrimayKeyGener.getNextId());
        dbm_sort_info.setCreate_user(getUserName());
        dbm_sort_info.setCreate_date(DateUtil.getSysDate());
        dbm_sort_info.setCreate_time(DateUtil.getSysTime());
        dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
        //3.添加标准分类信息
        dbm_sort_info.add(Dbo.db());
    }

    @Method(desc = "删除分类信息",
            logicStep = "1.检查待删除的分类下是否存在标准" +
                    "2.检查分类id是否存在" +
                    "3.根据分类id删除分类")
    @Param(name = "sort_id", desc = "标准分类id", range = "Int类型")
    public void deleteDbmSortInfo(long sort_id) {
        //1.检查待删除的分类下是否存在标准
        if (checkExistDataUnderTheSortInfo(sort_id)) {
            throw new BusinessException("分类下还存子分类或者标准!");
        }
        //2.检查分类id是否存在
        if (checkSortIdIsNotExist(sort_id)) {
            throw new BusinessException("删除的分类已经不存在!");
        }
        //3.根据分类id删除分类
        DboExecute.deletesOrThrow("删除分类失败!" + sort_id, "DELETE FROM " +
                Dbm_sort_info.TableName + " WHERE sort_id = ? ", sort_id);
    }

    @Method(desc = "修改分类信息",
            logicStep = "1.数据校验" +
                    "1-1.检查分类id是否存在" +
                    "1-2.检查分类名称是否为空" +
                    "1-3.检查分类名称是否重复" +
                    "2.设置分类信息" +
                    "3.修改数据")
    @Param(name = "dbm_sort_info", desc = "标准分类信息的实体对象", range = "标准分类信息的实体对象", isBean = true)
    public void updateDbmSortInfo(Dbm_sort_info dbm_sort_info) {
        //1.数据校验
        //1-1.检查分类id是否存在
        if (checkSortIdIsNotExist(dbm_sort_info.getSort_id())) {
            throw new BusinessException("修改的分类已经不存在!");
        }
        //1-2.检查分类名称是否为空
        if (StringUtil.isBlank(dbm_sort_info.getSort_name())) {
            throw new BusinessException("分类名称不能为空!" + dbm_sort_info.getSort_name());
        }
        //1-3.检查分类名称是否重复
        if (checkSortNameIsRepeat(dbm_sort_info.getSort_name())) {
            throw new BusinessException("分类名称已经存在!" + dbm_sort_info.getSort_name());
        }
        //1-4.分类创建人不能为空
        if (StringUtil.isBlank(dbm_sort_info.getCreate_user())) {
            throw new BusinessException("分类创建人不能为空!" + dbm_sort_info.getCreate_user());
        }
        //1-5.发布状态不能为空
        if (StringUtil.isBlank(dbm_sort_info.getSort_status())) {
            throw new BusinessException("分类发布状态不能为空!" + dbm_sort_info.getSort_status());
        }
        //1-6.分类等级不能为空
        if (StringUtil.isBlank(dbm_sort_info.getSort_level_num().toString())) {
            throw new BusinessException("分类等级不能为空!" + dbm_sort_info.getSort_level_num());
        }
        //2.设置分类信息
        //3.修改分类信息
        dbm_sort_info.update(Dbo.db());
    }

    @Method(desc = "获取所有分类信息", logicStep = "获取所有分类信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "所有分类信息", range = "所有分类信息")
    public Map<String, Object> getDbmSortInfo(int currPage, int pageSize) {
        Map<String, Object> dbmSortInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Dbm_sort_info> dbmSortInfos = Dbo.queryPagedList(Dbm_sort_info.class, page,
                "select * from " + Dbm_sort_info.TableName);
        dbmSortInfoMap.put("dbmSortInfos", dbmSortInfos);
        dbmSortInfoMap.put("totalSize", page.getTotalSize());
        return dbmSortInfoMap;
    }

    @Method(desc = "根据Id获取分类信息",
            logicStep = "根据Id获取分类信息")
    @Param(name = "sort_id", desc = "分类Id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Optional<Dbm_sort_info> getDbmSortInfoById(long sort_id) {
        //1.检查分类是否存在
        if (checkSortIdIsNotExist(sort_id)) {
            throw new BusinessException("查询的分类已经不存在! sort_id=" + sort_id);
        }
        return Dbo.queryOneObject(Dbm_sort_info.class, "select * from " + Dbm_sort_info.TableName +
                " where sort_id = ?", sort_id);
    }

    @Method(desc = "检查分类名称是否存在", logicStep = "1.根据 sort_name 检查名称是否存在")
    @Param(name = "sort_name", desc = "分类名称", range = "String类型，长度为10，该值唯一", example = "国籍")
    @Return(desc = "分类名称是否存在", range = "true：存在，false：不存在")
    private boolean checkSortNameIsRepeat(String sort_name) {
        //1.根据 sort_name 检查名称是否重复
        return Dbo.queryNumber("select count(sort_name) count from " + Dbm_sort_info.TableName +
                        " WHERE sort_name =?",
                sort_name).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查父分类是否存在", logicStep = "1.根据 parentCategory 检查父分类是否存在")
    @Param(name = "parent_id", desc = "分类名称", range = "Integer类型")
    @Return(desc = "父分类是否存在", range = "true：存在，false：不存在")
    private boolean checkParentIdIsRepeat(long parent_id) {
        //1.根据 categoryName 检查名称是否重复
        return Dbo.queryNumber("select count(parent_id) count from " + Dbm_sort_info.TableName +
                        " WHERE parentCategory =?",
                parent_id).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查分类下是否存在标准",
            logicStep = "1.检查分类下是否存在标准" +
                    "2.检查分类下是否存在子分类")
    @Param(name = "standardCategoryId", desc = "分类id", range = "long类型")
    @Return(desc = "分类下是否存在标准或者子分类", range = "true：存在，false：不存在")
    private boolean checkExistDataUnderTheSortInfo(long sort_id) {
        boolean isExist = false;
        //1.根据 sort_id 检查集分类下是否存在标准
        if (Dbo.queryNumber("select count(sort_id) count from " + Dbm_normbasic.TableName + " WHERE " +
                "sort_id =?", sort_id).orElseThrow(() ->
                new BusinessException("检查集分类下是否存在标准的SQL编写错误")) > 0) {
            isExist = true;
        }
        //2.根据 sort_id 检查集分类下是否存在子分类
        if (Dbo.queryNumber("select count(sort_id) count from " + Dbm_sort_info.TableName +
                " WHERE parent_id=?", sort_id).orElseThrow(() ->
                new BusinessException("检查集分类下是否存在子分类的SQL编写错误")) > 0) {
            isExist = true;
        }
        return isExist;
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
}
