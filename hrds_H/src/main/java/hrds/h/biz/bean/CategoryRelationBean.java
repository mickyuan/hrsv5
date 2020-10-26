package hrds.h.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "category_relation_bean")
public class CategoryRelationBean extends ProjectTableEntity {

	@DocBean(name = "category_id", value = "集市分类id:", dataType = Long.class, required = true)
	private Long category_id;
	@DocBean(name = "category_name", value = "分类名称:", dataType = String.class, required = false)
	private String category_name;
	@DocBean(name = "category_desc", value = "分类描述:", dataType = String.class, required = true)
	private String category_desc;
	@DocBean(name = "create_date", value = "创建日期:", dataType = String.class, required = true)
	private String create_date;
	@DocBean(name = "create_time", value = "创建时间:", dataType = String.class, required = true)
	private String create_time;
	@DocBean(name = "create_id", value = "创建用户:", dataType = Long.class, required = true)
	private Long create_id;
	@DocBean(name = "category_num", value = "分类编号:", dataType = String.class, required = false)
	private String category_num;
	@DocBean(name = "category_seq", value = "分类序号:", dataType = String.class, required = false)
	private String category_seq;
	@DocBean(name = "parent_category_id", value = "上级分类id:如果上级分类为集市工程", dataType = Long.class, required =
			true)
	private Long parent_category_id;
	@DocBean(name = "data_mart_id", value = "数据集市id:", dataType = Long.class, required = false)
	private Long data_mart_id;
	@DocBean(name = "parent_category_name", value = "父分类名称", dataType = String.class,
			required = true)
	private String parent_category_name;

	public Long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Long category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_desc() {
		return category_desc;
	}

	public void setCategory_desc(String category_desc) {
		this.category_desc = category_desc;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public Long getCreate_id() {
		return create_id;
	}

	public void setCreate_id(Long create_id) {
		this.create_id = create_id;
	}

	public String getCategory_num() {
		return category_num;
	}

	public void setCategory_num(String category_num) {
		this.category_num = category_num;
	}

	public String getCategory_seq() {
		return category_seq;
	}

	public void setCategory_seq(String category_seq) {
		this.category_seq = category_seq;
	}

	public Long getParent_category_id() {
		return parent_category_id;
	}

	public void setParent_category_id(Long parent_category_id) {
		this.parent_category_id = parent_category_id;
	}

	public Long getData_mart_id() {
		return data_mart_id;
	}

	public void setData_mart_id(Long data_mart_id) {
		this.data_mart_id = data_mart_id;
	}

	public String getParent_category_name() {
		return parent_category_name;
	}

	public void setParent_category_name(String parent_category_name) {
		this.parent_category_name = parent_category_name;
	}
}
