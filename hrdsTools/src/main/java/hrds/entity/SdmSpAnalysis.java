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
@Table(tableName = "sdm_sp_analysis")
public class SdmSpAnalysis extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_analysis";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssa_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String analysis_table_name;
	private BigDecimal ssa_info_id;
	private BigDecimal analysis_number;
	private String analysis_sql;
	private BigDecimal ssj_job_id;

	public String getAnalysis_table_name() { return analysis_table_name; }
	public void setAnalysis_table_name(String analysis_table_name) {
		if(analysis_table_name==null) throw new BusinessException("Entity : SdmSpAnalysis.analysis_table_name must not null!");
		this.analysis_table_name = analysis_table_name;
	}

	public BigDecimal getSsa_info_id() { return ssa_info_id; }
	public void setSsa_info_id(BigDecimal ssa_info_id) {
		if(ssa_info_id==null) throw new BusinessException("Entity : SdmSpAnalysis.ssa_info_id must not null!");
		this.ssa_info_id = ssa_info_id;
	}

	public BigDecimal getAnalysis_number() { return analysis_number; }
	public void setAnalysis_number(BigDecimal analysis_number) {
		if(analysis_number==null) throw new BusinessException("Entity : SdmSpAnalysis.analysis_number must not null!");
		this.analysis_number = analysis_number;
	}

	public String getAnalysis_sql() { return analysis_sql; }
	public void setAnalysis_sql(String analysis_sql) {
		if(analysis_sql==null) throw new BusinessException("Entity : SdmSpAnalysis.analysis_sql must not null!");
		this.analysis_sql = analysis_sql;
	}

	public BigDecimal getSsj_job_id() { return ssj_job_id; }
	public void setSsj_job_id(BigDecimal ssj_job_id) {
		if(ssj_job_id==null) throw new BusinessException("Entity : SdmSpAnalysis.ssj_job_id must not null!");
		this.ssj_job_id = ssj_job_id;
	}

}