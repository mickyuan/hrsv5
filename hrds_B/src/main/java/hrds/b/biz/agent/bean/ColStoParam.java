package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

import java.util.Arrays;

@DocClass(desc = "定义字段存储目的地参数类", author = "WangZhengcheng")
public class ColStoParam {

	//字段ID
	private Long columnId;
	//数据存储附加信息ID，因为一个列的附加信息可能不唯一，所以使用数组来接收
	private long[] dsladIds;
	//序号位置，如果该字段和其他几个字段一起做hbase的rowkey，那么需要用这个值来定义字段顺序，对其他的附加信息，如主键，这个值可以不传
	private Long csiNumber;

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public long[] getDsladIds() {
		return dsladIds;
	}

	public void setDsladIds(long[] dsladIds) {
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
