package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "保存接口使用信息实体对象", author = "dhw", createdate = "2020/4/10 14:32")
@Table(tableName = "interface_infos")
public class InterfaceInfos extends ProjectTableEntity {

	private static final long serialVersionUID = 542649091193588298L;

	public static final String TableName = "interface_infos";

	@DocBean(name = "user_id", value = "用户ID:", dataType = long[].class, required = true)
	private long[] user_id;
	@DocBean(name = "interface_note", value = "备注:", dataType = String.class, required = false)
	private String interface_note;
	@DocBean(name = "classify_name", value = "分类名称:", dataType = long[].class, required = false)
	private String classify_name;
	@DocBean(name = "interface_id", value = "接口ID:", dataType = String.class, required = true)
	private long[] interface_id;
	@DocBean(name = "start_use_date", value = "开始日期:yyyyMMdd(8位)", dataType = String[].class, required = true)
	private String[] start_use_date;
	@DocBean(name = "use_valid_date", value = "有效（结束）日期:yyyyMMdd(8位)", dataType = String[].class, required = true)
	private String[] use_valid_date;

	public long[] getUser_id() {
		return user_id;
	}

	public void setUser_id(long[] user_id) {
		this.user_id = user_id;
	}

	public String getInterface_note() {
		return interface_note;
	}

	public void setInterface_note(String interface_note) {
		this.interface_note = interface_note;
	}

	public String getClassify_name() {
		return classify_name;
	}

	public void setClassify_name(String classify_name) {
		this.classify_name = classify_name;
	}

	public long[] getInterface_id() {
		return interface_id;
	}

	public void setInterface_id(long[] interface_id) {
		this.interface_id = interface_id;
	}

	public String[] getStart_use_date() {
		return start_use_date;
	}

	public void setStart_use_date(String[] start_use_date) {
		this.start_use_date = start_use_date;
	}

	public String[] getUse_valid_date() {
		return use_valid_date;
	}

	public void setUse_valid_date(String[] use_valid_date) {
		this.use_valid_date = use_valid_date;
	}
}
