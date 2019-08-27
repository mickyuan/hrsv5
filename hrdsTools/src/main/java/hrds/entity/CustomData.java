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
@Table(tableName = "custom_data")
public class CustomData extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "custom_data";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cd_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal cd_id;
	private String need_data;
	private String need_type;
	private BigDecimal cr_id;

	public BigDecimal getCd_id() { return cd_id; }
	public void setCd_id(BigDecimal cd_id) {
		if(cd_id==null) throw new BusinessException("Entity : CustomData.cd_id must not null!");
		this.cd_id = cd_id;
	}

	public String getNeed_data() { return need_data; }
	public void setNeed_data(String need_data) {
		if(need_data==null) addNullValueField("need_data");
		this.need_data = need_data;
	}

	public String getNeed_type() { return need_type; }
	public void setNeed_type(String need_type) {
		if(need_type==null) addNullValueField("need_type");
		this.need_type = need_type;
	}

	public BigDecimal getCr_id() { return cr_id; }
	public void setCr_id(BigDecimal cr_id) {
		if(cr_id==null) throw new BusinessException("Entity : CustomData.cr_id must not null!");
		this.cr_id = cr_id;
	}

}