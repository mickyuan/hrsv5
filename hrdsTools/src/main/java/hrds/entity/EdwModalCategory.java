package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "edw_modal_category")
public class EdwModalCategory extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_modal_category";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("category_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal parent_category_id;
	private String category_seq;
	private String category_name;
	private BigDecimal category_id;
	private String create_time;
	private BigDecimal create_id;
	private String category_desc;
	private String category_num;
	private String create_date;
	private BigDecimal modal_pro_id;

	public BigDecimal getParent_category_id() { return parent_category_id; }
	public void setParent_category_id(BigDecimal parent_category_id) {
		if(parent_category_id==null) throw new BusinessException("Entity : EdwModalCategory.parent_category_id must not null!");
		this.parent_category_id = parent_category_id;
	}

	public String getCategory_seq() { return category_seq; }
	public void setCategory_seq(String category_seq) {
		if(category_seq==null) addNullValueField("category_seq");
		this.category_seq = category_seq;
	}

	public String getCategory_name() { return category_name; }
	public void setCategory_name(String category_name) {
		if(category_name==null) throw new BusinessException("Entity : EdwModalCategory.category_name must not null!");
		this.category_name = category_name;
	}

	public BigDecimal getCategory_id() { return category_id; }
	public void setCategory_id(BigDecimal category_id) {
		if(category_id==null) throw new BusinessException("Entity : EdwModalCategory.category_id must not null!");
		this.category_id = category_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : EdwModalCategory.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : EdwModalCategory.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCategory_desc() { return category_desc; }
	public void setCategory_desc(String category_desc) {
		if(category_desc==null) addNullValueField("category_desc");
		this.category_desc = category_desc;
	}

	public String getCategory_num() { return category_num; }
	public void setCategory_num(String category_num) {
		if(category_num==null) throw new BusinessException("Entity : EdwModalCategory.category_num must not null!");
		this.category_num = category_num;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : EdwModalCategory.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getModal_pro_id() { return modal_pro_id; }
	public void setModal_pro_id(BigDecimal modal_pro_id) {
		if(modal_pro_id==null) throw new BusinessException("Entity : EdwModalCategory.modal_pro_id must not null!");
		this.modal_pro_id = modal_pro_id;
	}

}