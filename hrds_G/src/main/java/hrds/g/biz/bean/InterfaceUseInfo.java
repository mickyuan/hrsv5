package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "保存接口使用信息实体对象", author = "dhw", createdate = "2020/4/10 14:32")
@Table(tableName = "interface_use_info")
public class InterfaceUseInfo extends ProjectTableEntity {

	private static final long serialVersionUID = 542649091193588298L;

	public static final String TableName = "interface_use_info";

	@DocBean(name = "interface_id", value = "接口ID:", dataType = Long.class, required = true)
	private Long interface_id;
	@DocBean(name = "start_use_date", value = "开始日期:yyyyMMdd(8位)", dataType = String.class, required = true)
	private String start_use_date;
	@DocBean(name = "use_valid_date", value = "有效（结束）日期:yyyyMMdd(8位)", dataType = String.class, required = true)
	private String use_valid_date;

	public Long getInterface_id() {
		return interface_id;
	}

	public void setInterface_id(Long interface_id) {
		this.interface_id = interface_id;
	}

	public String getStart_use_date() {
		return start_use_date;
	}

	public void setStart_use_date(String start_use_date) {
		this.start_use_date = start_use_date;
	}

	public String getUse_valid_date() {
		return use_valid_date;
	}

	public void setUse_valid_date(String use_valid_date) {
		this.use_valid_date = use_valid_date;
	}
}
