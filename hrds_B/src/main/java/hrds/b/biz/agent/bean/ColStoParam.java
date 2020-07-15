package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.Arrays;

@DocClass(desc = "定义字段存储目的地参数类", author = "WangZhengcheng")
public class ColStoParam extends ProjectTableEntity {

	private static final long serialVersionUID = -1666378328728519042L;

	@DocBean(name = "columnId", value = "字段ID:", dataType = Long.class, required = false)
	private Long columnId;
	@DocBean(name = "dsladIds", value = "数据存储附加信息ID:因为一个列的附加信息可能不唯一，所以使用数组来接收",
			dataType = Long[].class, required = false)
	private Long[] dsladIds;
	@DocBean(name = "csiNumber", value = "序号位置:如果该字段和其他几个字段一起做hbase的rowkey" +
			"，那么需要用这个值来定义字段顺序，对其他的附加信息，如主键，这个值可以不传",
			dataType = Long.class, required = true)
	private Long csiNumber;

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long[] getDsladIds() {
		return dsladIds;
	}

	public void setDsladIds(Long[] dsladIds) {
		this.dsladIds = dsladIds;
	}

	public Long getCsiNumber() {
		return csiNumber;
	}

	public void setCsiNumber(Long csiNumber) {
		this.csiNumber = csiNumber;
	}

	@Override
	public String toString() {
		return "ColStoParam{" +
				"columnId=" + columnId +
				", dsladIds=" + Arrays.toString(dsladIds) +
				", csiNumber=" + csiNumber +
				'}';
	}
}
