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
@Table(tableName = "etl_job_temp")
public class EtlJobTemp extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_temp";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_temp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal etl_temp_id;
	private String etl_temp_type;
	private String pro_dic;
	private String pro_name;

	public BigDecimal getEtl_temp_id() { return etl_temp_id; }
	public void setEtl_temp_id(BigDecimal etl_temp_id) {
		if(etl_temp_id==null) throw new BusinessException("Entity : EtlJobTemp.etl_temp_id must not null!");
		this.etl_temp_id = etl_temp_id;
	}

	public String getEtl_temp_type() { return etl_temp_type; }
	public void setEtl_temp_type(String etl_temp_type) {
		if(etl_temp_type==null) throw new BusinessException("Entity : EtlJobTemp.etl_temp_type must not null!");
		this.etl_temp_type = etl_temp_type;
	}

	public String getPro_dic() { return pro_dic; }
	public void setPro_dic(String pro_dic) {
		if(pro_dic==null) throw new BusinessException("Entity : EtlJobTemp.pro_dic must not null!");
		this.pro_dic = pro_dic;
	}

	public String getPro_name() { return pro_name; }
	public void setPro_name(String pro_name) {
		if(pro_name==null) throw new BusinessException("Entity : EtlJobTemp.pro_name must not null!");
		this.pro_name = pro_name;
	}

}