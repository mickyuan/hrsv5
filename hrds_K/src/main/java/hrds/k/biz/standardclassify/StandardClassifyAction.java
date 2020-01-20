package hrds.k.biz.standardclassify;

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
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.entity.StandardClassifyInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "标准分类管理类", author = "BY-HLL", createdate = "2020/1/19 0019 下午 01:37")
public class StandardClassifyAction extends BaseAction {

    @Method(desc = "添加标准分类",
            logicStep = "1.数据校验" +
                    "1-1.分类名不能为空" +
                    "1-2.分类名称重复" +
                    "1-3.父分类不为空的情况下,检查上级分类是否存在" +
                    "2.设置标准分类信息" +
                    "3.添加标准分类信息")
    @Param(name = "standardClassifyInfo", desc = "标准分类的实体对象", range = "标准分类的实体对象", isBean = true)
    public void addStandardClassify(StandardClassifyInfo standardClassifyInfo) {
        //1.数据校验
        //1-1.分类名不能为空
        if (StringUtil.isBlank(standardClassifyInfo.getCategoryName())) {
            throw new BusinessException("部门名称重复!" + standardClassifyInfo.getCategoryName());
        }
        //1-2.分类名称重复
        if (checkCategoryNameIsRepeat(standardClassifyInfo.getCategoryName())) {
            throw new BusinessException("分类名称已经存在!" + standardClassifyInfo.getCategoryName());
        }
        //1-3.父分类不为空的情况下,检查上级分类是否存在
        if (StringUtil.isNotBlank(standardClassifyInfo.getParentCategory().toString())) {
            if (!checkParentCategoryIsRepeat(standardClassifyInfo.getParentCategory())) {
                throw new BusinessException("选择父分类名称不存在!" + standardClassifyInfo.getParentCategory());
            }
        }
        //2.设置标准分类信息
        standardClassifyInfo.setStandardCategoryId(PrimayKeyGener.getNextId());
        standardClassifyInfo.setCategoryCreator(getUserName());
        standardClassifyInfo.setCreationDate(DateUtil.getSysDate());
        standardClassifyInfo.setCreationTime(DateUtil.getDateTime());
        standardClassifyInfo.setPostStatus(IsFlag.Shi.getCode());
        //3.添加标准分类信息
        standardClassifyInfo.add(Dbo.db());
    }

    @Method(desc = "删除分类信息",
            logicStep = "1.检查待删除的分类下是否存在标准" +
                    "2.检查分类id是否存在" +
                    "3.根据分类id删除分类")
    @Param(name = "standardCategoryId", desc = "标准分类id", range = "Int类型")
    public void deleteStandardClassifyInfo(long standardCategoryId) {
        //1.检查待删除的分类下是否存在标准
        if (checkExistDataUnderTheCategory(standardCategoryId)) {
            throw new BusinessException("分类下还存子分类或者标准!");
        }
        //2.检查分类id是否存在
        if (checkCategoryIdIsNotExist(standardCategoryId)) {
            throw new BusinessException("删除的分类已经不存在!");
        }
        //3.根据分类id删除分类
        DboExecute.deletesOrThrow("删除分类失败!" + standardCategoryId, "DELETE FROM " +
                StandardClassifyInfo.TableName + " WHERE standardCategoryId = ? ", standardCategoryId);
    }

    @Method(desc = "修改分类信息",
            logicStep = "1.数据校验" +
                    "1-1.检查分类id是否存在" +
                    "1-2.检查分类名称是否为空" +
                    "1-3.检查分类名称是否重复" +
                    "2.设置分类信息" +
                    "3.修改数据")
    @Param(name = "standardCategoryId", desc = "标准分类id", range = "Int类型")
    public void updateStandardClassifyInfo(StandardClassifyInfo standardClassifyInfo) {
        //1.数据校验
        //1-1.检查分类id是否存在
        if (checkCategoryIdIsNotExist(standardClassifyInfo.getStandardCategoryId())) {
            throw new BusinessException("修改的分类已经不存在!");
        }
        //1-2.检查分类名称是否为空
        if (StringUtil.isBlank(standardClassifyInfo.getCategoryName())) {
            throw new BusinessException("分类名称不能为空!" + standardClassifyInfo.getCategoryName());
        }
        //1-3.检查分类名称是否重复
        if (checkCategoryNameIsRepeat(standardClassifyInfo.getCategoryName())) {
            throw new BusinessException("分类名称已经存在!" + standardClassifyInfo.getCategoryName());
        }
        //1-4.分类创建人不能为空
        if (StringUtil.isBlank(standardClassifyInfo.getCategoryCreator())) {
            throw new BusinessException("分类创建人不能为空!" + standardClassifyInfo.getCategoryCreator());
        }
        //1-5.发布状态不能为空
        if (StringUtil.isBlank(standardClassifyInfo.getPostStatus())) {
            throw new BusinessException("分类发布状态不能为空!" + standardClassifyInfo.getPostStatus());
        }
        //1-6.分类等级不能为空
        if (StringUtil.isBlank(standardClassifyInfo.getCategoryLevel())) {
            throw new BusinessException("分类等级不能为空!" + standardClassifyInfo.getCategoryLevel());
        }
        //2.设置分类信息
        //3.修改分类信息
        standardClassifyInfo.update(Dbo.db());
    }

    @Method(desc = "获取所有分类信息", logicStep = "获取所有分类信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true, valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", nullable = true,
            valueIfNull = "10")
    @Return(desc = "所有分类信息", range = "所有分类信息")
    public Map<String, Object> getStandardClassifyInfo(int currPage, int pageSize) {
        Map<String, Object> standardClassifyInfoMap = new HashMap<>();
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<StandardClassifyInfo> standardClassifyInfos = Dbo.queryPagedList(StandardClassifyInfo.class, page,
                "select * from " + StandardClassifyInfo.TableName);
        standardClassifyInfoMap.put("standardClassifyInfos", standardClassifyInfos);
        standardClassifyInfoMap.put("totalSize", page.getTotalSize());
        return standardClassifyInfoMap;
    }

    @Method(desc = "根据Id获取分类信息",
            logicStep = "根据Id获取分类信息")
    @Param(name = "standardCategoryId", desc = "分类Id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Optional<StandardClassifyInfo> getStandardClassifyInfoById(long standardCategoryId) {
        //1.检查分类是否存在
        if (checkCategoryIdIsNotExist(standardCategoryId)) {
            throw new BusinessException("查询的分类已经不存在! standardCategoryId=" + standardCategoryId);
        }
        return Dbo.queryOneObject(StandardClassifyInfo.class, "select * from " + StandardClassifyInfo.TableName +
                " where standardCategoryId = ?", standardCategoryId);
    }

    @Method(desc = "检查分类名称是否存在", logicStep = "1.根据 categoryName 检查名称是否存在")
    @Param(name = "categoryName", desc = "分类名称", range = "String类型，长度为10，该值唯一", example = "国籍")
    @Return(desc = "分类名称是否存在", range = "true：存在，false：不存在")
    private boolean checkCategoryNameIsRepeat(String categoryName) {
        //1.根据 categoryName 检查名称是否重复
        return Dbo.queryNumber("select count(categoryName) count from " + StandardClassifyInfo.TableName +
                        " WHERE categoryName =?",
                categoryName).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查父分类是否存在", logicStep = "1.根据 parentCategory 检查父分类是否存在")
    @Param(name = "parentCategory", desc = "分类名称", range = "Integer类型")
    @Return(desc = "父分类是否存在", range = "true：存在，false：不存在")
    private boolean checkParentCategoryIsRepeat(long parentCategory) {
        //1.根据 categoryName 检查名称是否重复
        return Dbo.queryNumber("select count(parentCategory) count from " + StandardClassifyInfo.TableName +
                        " WHERE parentCategory =?",
                parentCategory).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查分类下是否存在标准",
            logicStep = "1.检查分类下是否存在标准" +
                    "2.检查分类下是否存在子分类")
    @Param(name = "standardCategoryId", desc = "分类id", range = "long类型")
    @Return(desc = "分类下是否存在标准或者子分类", range = "true：存在，false：不存在")
    private boolean checkExistDataUnderTheCategory(long standardCategoryId) {
        boolean isExist = false;
        //1.根据 standardCategoryId 检查集分类下是否存在标准
        if (Dbo.queryNumber("select count(standardCategoryId) count from " + Sys_user.TableName + " WHERE " +
                "standardCategoryId =?", standardCategoryId).orElseThrow(() ->
                new BusinessException("检查集分类下是否存在标准的SQL编写错误")) > 0) {
            isExist = true;
        }
        //2.根据 standardCategoryId 检查集分类下是否存在子分类
        if (Dbo.queryNumber("select count(standardCategoryId) count from " + StandardClassifyInfo.TableName +
                " WHERE parentCategory =?", standardCategoryId).orElseThrow(() ->
                new BusinessException("检查集分类下是否存在子分类的SQL编写错误")) > 0) {
            isExist = true;
        }
        return isExist;
    }

    @Method(desc = "检查分类id是否存在", logicStep = "检查分类id是否存在")
    @Param(name = "standardCategoryId", desc = "分类id", range = "long类型")
    @Return(desc = "分类否存在", range = "true：不存在，false：存在")
    private boolean checkCategoryIdIsNotExist(long standardCategoryId) {
        //1.根据 dep_id 检查部门是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
        return Dbo.queryNumber("SELECT COUNT(standardCategoryId) FROM " + StandardClassifyInfo.TableName +
                " WHERE standardCategoryId = ?", standardCategoryId).orElseThrow(() ->
                new BusinessException("检查部门否存在的SQL编写错误")) != 1;
    }
}
