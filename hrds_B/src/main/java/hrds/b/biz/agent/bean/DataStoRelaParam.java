package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.Arrays;

@DocClass(desc = "数据存储关系参数表", author = "WangZhengcheng")
/*
 * 这个实体类用于定义存储目的地保存时，数据存储关系表的保存
 * */
public class DataStoRelaParam extends ProjectTableEntity {

	private static final long serialVersionUID = 321677610043865203L;

	@DocBean(name = "tableId", value = "表ID:", dataType = Long.class, required = false)
	private Long tableId;
	@DocBean(name = "dslIds", value = "数据存储层配置表ID:由于一张抽取并入库的表可以选择多个存储目的地，所以这里用数组表示",
			dataType = Long[].class, required = false)
	private Long[] dslIds;
	@DocBean(name = "hyren_name", value = "最终的落地数据表名称:半结构化采集时不传递", dataType = String.class)
	private String hyren_name;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long[] getDslIds() {
		return dslIds;
	}

	public void setDslIds(Long[] dslIds) {
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
