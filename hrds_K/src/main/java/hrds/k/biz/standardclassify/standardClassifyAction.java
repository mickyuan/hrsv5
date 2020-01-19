package hrds.k.biz.standardclassify;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.entity.StandardClassifyInfo;

@DocClass(desc = "标准分类管理类", author = "BY-HLL", createdate = "2020/1/19 0019 下午 01:37")
public class standardClassifyAction extends BaseAction {

    @Method(desc = "添加标准分类", logicStep = "添加标准分类")
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

    public void deleteStandardClassifyInfo() {

    }

    @Method(desc = "检查分类名称是否存在",
            logicStep = "1.根据 categoryName 检查名称是否存在")
    @Param(name = "categoryName", desc = "分类名称", range = "String类型，长度为10，该值唯一", example = "国籍")
    @Return(desc = "分类名称是否存在", range = "true：存在，false：不存在")
    private boolean checkCategoryNameIsRepeat(String categoryName) {
        //1.根据 categoryName 检查名称是否重复
        return Dbo.queryNumber("select count(categoryName) count from " + StandardClassifyInfo.TableName +
                        " WHERE categoryName =?",
                categoryName).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }

    @Method(desc = "检查父分类是否存在",
            logicStep = "1.根据 parentCategory 检查父分类是否存在")
    @Param(name = "parentCategory", desc = "分类名称", range = "Integer类型")
    @Return(desc = "父分类是否存在", range = "true：存在，false：不存在")
    private boolean checkParentCategoryIsRepeat(long parentCategory) {
        //1.根据 categoryName 检查名称是否重复
        return Dbo.queryNumber("select count(parentCategory) count from " + StandardClassifyInfo.TableName +
                        " WHERE parentCategory =?",
                parentCategory).orElseThrow(() -> new BusinessException("检查分类名称否重复的SQL编写错误")) != 0;
    }
}
