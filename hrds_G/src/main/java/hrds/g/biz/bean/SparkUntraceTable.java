package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "创建无溯源Spark实体", author = "dhw", createdate = "2020/4/1 17:51")
@Table(tableName = "spark_untrace_table")
public class SparkUntraceTable extends ProjectTableEntity {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "spark_untrace_table";

	@DocBean(name = "toragetype", value = "存储方式:选择其中的一种方式(追加 =2，替换=3，增量=1或者不填)",
			dataType = String.class, required = false)
	private String toragetype;
	@DocBean(name = "tabspace", value = "表空间", dataType = String.class, required = true)
	private String tabspace;
	@DocBean(name = "entabname", value = "表名称", dataType = String.class, required = true)
	private String entabname;
	@DocBean(name = "cntabname", value = "表中文描述", dataType = String.class, required = false)
	private String cntabname;

	public String getToragetype() {
		return toragetype;
	}

	public void setToragetype(String toragetype) {
		this.toragetype = toragetype;
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

	public String getCntabname() {
		return cntabname;
	}

	public void setCntabname(String cntabname) {
		this.cntabname = cntabname;
	}
}
