package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "spark表数据插入参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "spark_table")
public class SparkTable extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "spark_table";

	@DocBean(name = "data", value = "插入方式为 json(最大支持1000条)\n" +
			"[{\"age\":27,\"phone\":\"1111111111\"},{\"age\":28,\"phone\":\"1111111111\"}]，" +
			"age表示为字段名称,27表示为字段参数,最大支持1000条\n" +
			"插入方式为 sql\n" +
			"INSERT INTO emp VALUES(27,'1111111111') 或者 INSERT INTO emp SELECT age,phone FROM emp2:",
			dataType = String.class, required = true)
	private String data;
	@DocBean(name = "tabspace", value = "表空间:", dataType = String.class, required = true)
	private String tabspace;
	@DocBean(name = "entabname", value = "需要创建的表名:", dataType = String.class, required = true)
	private String entabname;
	@DocBean(name = "addtype", value = "1为json更新、0为SQL更新:", dataType = String.class, required = true)
	private String addtype;
	@DocBean(name = "etl", value = ":", dataType = String.class, required = true)
	private String etl;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTabspace() {
		return tabspace;
	}

	public void setTabspace(String tabspace) {
		this.tabspace = tabspace;
	}

	public String getEntabname() {
		return entabname;
	}

	public void setEntabname(String entabname) {
		this.entabname = entabname;
	}

	public String getAddtype() {
		return addtype;
	}

	public void setAddtype(String addtype) {
		this.addtype = addtype;
	}

	public String getEtl() {
		return etl;
	}

	public void setEtl(String etl) {
		this.etl = etl;
	}
}
