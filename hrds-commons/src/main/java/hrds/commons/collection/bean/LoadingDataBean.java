package hrds.commons.collection.bean;

import fd.ng.core.annotation.DocClass;

import java.util.Map;

@DocClass(desc = "加载数据的实体Bean", author = "博彦科技", createdate = "2020/4/22 0022 下午 01:43")
public class LoadingDataBean {

    //存储层信息
    private Map<String, String> layerMap;
    //是否批量
    private boolean isBatch = true;
    private boolean isDirTran = true;
    //批量值
    private int batchNum = 50000;
    //表名
    private String tableName;

    public Map<String, String> getLayerMap() {
        return layerMap;
    }

    public void setLayerMap(Map<String, String> layerMap) {
        this.layerMap = layerMap;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }

    public boolean isDirTran() {
        return isDirTran;
    }

    public void setDirTran(boolean dirTran) {
        isDirTran = dirTran;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
