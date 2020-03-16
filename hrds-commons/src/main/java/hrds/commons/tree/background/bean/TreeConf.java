package hrds.commons.tree.background.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "树配置类", author = "BY-HLL", createdate = "2020/3/16 0016 下午 12:39")
public class TreeConf {

    //是否显示文件采集数据
    private Boolean isShowFileCollection;
    //0:入HBase树菜单,1:创建二级索引树菜单,"":默认获取所有表
    private String isIntoHBase = "";

    //获取 是否显示文件采集数据
    public Boolean getShowFileCollection() {
        return isShowFileCollection;
    }

    //设置 是否显示文件采集数据
    public void setShowFileCollection(Boolean showFileCollection) {
        isShowFileCollection = showFileCollection;
    }

    //获取 0:HBase树菜单,1:二级索引树菜单
    public String getIsIntoHBase() {
        return isIntoHBase;
    }

    //设置 0:HBase树菜单,1:二级索引树菜单
    public void setIsIntoHBase(String isIntoHBase) {
        this.isIntoHBase = isIntoHBase;
    }
}
