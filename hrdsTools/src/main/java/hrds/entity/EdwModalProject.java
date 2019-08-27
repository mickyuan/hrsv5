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
@Table(tableName = "edw_modal_project")
public class EdwModalProject extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_modal_project";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("modal_pro_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private BigDecimal create_id;
	private BigDecimal modal_pro_id;
	private String create_date;
	private String pro_number;
	private String pro_desc;
	private String pro_name;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : EdwModalProject.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : EdwModalProject.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getModal_pro_id() { return modal_pro_id; }
	public void setModal_pro_id(BigDecimal modal_pro_id) {
		if(modal_pro_id==null) throw new BusinessException("Entity : EdwModalProject.modal_pro_id must not null!");
		this.modal_pro_id = modal_pro_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : EdwModalProject.create_date must not null!");
		this.create_date = create_date;
	}

	public String getPro_number() { return pro_number; }
	public void setPro_number(String pro_number) {
		if(pro_number==null) throw new BusinessException("Entity : EdwModalProject.pro_number must not null!");
		this.pro_number = pro_number;
	}

	public String getPro_desc() { return pro_desc; }
	public void setPro_desc(String pro_desc) {
		if(pro_desc==null) addNullValueField("pro_desc");
		this.pro_desc = pro_desc;
	}

	public String getPro_name() { return pro_name; }
	public void setPro_name(String pro_name) {
		if(pro_name==null) throw new BusinessException("Entity : EdwModalProject.pro_name must not null!");
		this.pro_name = pro_name;
	}

}