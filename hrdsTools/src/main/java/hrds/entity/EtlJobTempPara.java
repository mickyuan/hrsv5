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
@Table(tableName = "etl_job_temp_para")
public class EtlJobTempPara extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_temp_para";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_temp_para_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String etl_job_pro_para;
	private BigDecimal etl_temp_para_id;
	private String etl_para_type;
	private String etl_job_para_size;
	private BigDecimal etl_temp_id;
	private BigDecimal etl_pro_para_sort;

	public String getEtl_job_pro_para() { return etl_job_pro_para; }
	public void setEtl_job_pro_para(String etl_job_pro_para) {
		if(etl_job_pro_para==null) throw new BusinessException("Entity : EtlJobTempPara.etl_job_pro_para must not null!");
		this.etl_job_pro_para = etl_job_pro_para;
	}

	public BigDecimal getEtl_temp_para_id() { return etl_temp_para_id; }
	public void setEtl_temp_para_id(BigDecimal etl_temp_para_id) {
		if(etl_temp_para_id==null) throw new BusinessException("Entity : EtlJobTempPara.etl_temp_para_id must not null!");
		this.etl_temp_para_id = etl_temp_para_id;
	}

	public String getEtl_para_type() { return etl_para_type; }
	public void setEtl_para_type(String etl_para_type) {
		if(etl_para_type==null) throw new BusinessException("Entity : EtlJobTempPara.etl_para_type must not null!");
		this.etl_para_type = etl_para_type;
	}

	public String getEtl_job_para_size() { return etl_job_para_size; }
	public void setEtl_job_para_size(String etl_job_para_size) {
		if(etl_job_para_size==null) throw new BusinessException("Entity : EtlJobTempPara.etl_job_para_size must not null!");
		this.etl_job_para_size = etl_job_para_size;
	}

	public BigDecimal getEtl_temp_id() { return etl_temp_id; }
	public void setEtl_temp_id(BigDecimal etl_temp_id) {
		if(etl_temp_id==null) throw new BusinessException("Entity : EtlJobTempPara.etl_temp_id must not null!");
		this.etl_temp_id = etl_temp_id;
	}

	public BigDecimal getEtl_pro_para_sort() { return etl_pro_para_sort; }
	public void setEtl_pro_para_sort(BigDecimal etl_pro_para_sort) {
		if(etl_pro_para_sort==null) throw new BusinessException("Entity : EtlJobTempPara.etl_pro_para_sort must not null!");
		this.etl_pro_para_sort = etl_pro_para_sort;
	}

}