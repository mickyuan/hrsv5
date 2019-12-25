package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "配置采集表的字段参数信息实体类", author = "WangZhengcheng")
public class CollTbConfParam {

	/*
	* 一张表对应的所有要被采集的列组成的json格式的字符串
	* 一个json对象中应该包括列名(column_name)、字段类型(column_type)、列中文名(column_ch_name)、是否采集(is_get)
	* 如果用户没有选择采集列，则传空字符串，系统默认采集该张表所有列
	* */
	private String collColumnString;

	public String getCollColumnString() {
		return collColumnString;
	}

	public void setCollColumnString(String collColumnString) {
		this.collColumnString = collColumnString;
	}

	@Override
	public String toString() {
		return "CollTbConfParam{" +
				"collColumnString='" + collColumnString + '\'' +
				'}';
	}
}
