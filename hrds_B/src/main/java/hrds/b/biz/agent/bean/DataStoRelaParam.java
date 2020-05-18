package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

import java.util.Arrays;

@DocClass(desc = "数据存储关系参数表", author = "WangZhengcheng")
/*
 * 这个实体类用于定义存储目的地保存时，数据存储关系表的保存
 * */
public class DataStoRelaParam {

  // 表ID
  private Long tableId;
  // 数据存储层配置表ID，由于一张抽取并入库的表可以选择多个存储目的地，所以这里用数组表示
  private long[] dslIds;

  /** 最终的落地数据表名称*/
  private String hyren_name;

  public Long getTableId() {
    return tableId;
  }

  public void setTableId(Long tableId) {
    this.tableId = tableId;
  }

  public long[] getDslIds() {
    return dslIds;
  }

  public void setDslIds(long[] dslIds) {
    this.dslIds = dslIds;
  }

  public void setHyren_name(String hyren_name) {
    this.hyren_name = hyren_name;
  }

  public String getHyren_name() {
    return hyren_name;
  }

  @Override
  public String toString() {
    return "DataStoRelaParam{" + "tableId=" + tableId + ", dslIds=" + Arrays.toString(dslIds) + '}';
  }
}
