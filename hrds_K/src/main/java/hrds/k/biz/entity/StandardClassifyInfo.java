package hrds.k.biz.entity;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 标准分类表
 */
@Table(tableName = "standardClassifyInfo")
public class StandardClassifyInfo extends ProjectTableEntity {
    private static final long serialVersionUID = 321566870187324L;
    private transient static final Set<String> __PrimaryKeys;
    public static final String TableName = "standardClassifyInfo";

    /**
     * 检查给定的名字，是否为主键中的字段
     *
     * @param name String 检验是否为主键的名字
     * @return
     */
    public static boolean isPrimaryKey(String name) {
        return __PrimaryKeys.contains(name);
    }

    public static Set<String> getPrimaryKeyNames() {
        return __PrimaryKeys;
    }

    /** 标准分类表 */
    static {
        Set<String> __tmpPKS = new HashSet<>();
        __tmpPKS.add("standardCategoryId");
        __PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
    }

    @DocBean(name = "standardCategoryId", value = "标准分类ID:", dataType = Long.class, required = true)
    private Long standardCategoryId;
    @DocBean(name = "parentCategory", value = "parentCategory:", dataType = Long.class, required = true)
    private Long parentCategory;
    @DocBean(name = "categoryLevel", value = "categoryLevel:", dataType = String.class, required = true)
    private String categoryLevel;
    @DocBean(name = "categoryName", value = "categoryName:", dataType = String.class, required = true)
    private String categoryName;
    @DocBean(name = "categoryDesc", value = "categoryDesc:", dataType = String.class, required = false)
    private String categoryDesc;
    @DocBean(name = "categoryCreator", value = "categoryCreator", dataType = String.class, required = true)
    private String categoryCreator;
    @DocBean(name = "creationDate", value = "creationDate", dataType = String.class, required = true)
    private String creationDate;
    @DocBean(name = "creationTime", value = "creationTime:", dataType = String.class, required = true)
    private String creationTime;
    @DocBean(name = "postStatus", value = "postStatus:", dataType = String.class, required = true)
    private String postStatus;
    @DocBean(name = "remark", value = "remark:", dataType = String.class, required = true)
    private String remark;

    public Long getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Long standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public void setStandardCategoryId(String standardCategoryId) {
        if (StringUtil.isNotBlank(standardCategoryId)) {
            this.standardCategoryId = new Long(standardCategoryId);
        }
    }

    public Long getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Long parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(String categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public String getCategoryCreator() {
        return categoryCreator;
    }

    public void setCategoryCreator(String categoryCreator) {
        this.categoryCreator = categoryCreator;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
