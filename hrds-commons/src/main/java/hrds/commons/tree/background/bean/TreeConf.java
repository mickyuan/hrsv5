package hrds.commons.tree.background.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "树配置类", author = "BY-HLL", createdate = "2020/3/16 0016 下午 12:39")
public class TreeConf {

    //是否显示文件采集数据
    private String isFileCollection;

    public String getIsFileCollection() {
        return isFileCollection;
    }

    public void setIsFileCollection(String isFileCollection) {
        this.isFileCollection = isFileCollection;
    }
}
