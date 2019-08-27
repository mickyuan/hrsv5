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
@Table(tableName = "ml_efsearch")
public class MlEfsearch extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_efsearch";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exhafeats_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String algorithm_type;
	private BigDecimal crossveri_num;
	private String create_time;
	private String dv_column;
	private BigDecimal featlarg_num;
	private BigDecimal featmin_num;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal exhafeats_id;
	private String create_date;
	private String specific_algorithm;
	private String algo_eval_crit;

	public String getAlgorithm_type() { return algorithm_type; }
	public void setAlgorithm_type(String algorithm_type) {
		if(algorithm_type==null) throw new BusinessException("Entity : MlEfsearch.algorithm_type must not null!");
		this.algorithm_type = algorithm_type;
	}

	public BigDecimal getCrossveri_num() { return crossveri_num; }
	public void setCrossveri_num(BigDecimal crossveri_num) {
		if(crossveri_num==null) addNullValueField("crossveri_num");
		this.crossveri_num = crossveri_num;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlEfsearch.create_time must not null!");
		this.create_time = create_time;
	}

	public String getDv_column() { return dv_column; }
	public void setDv_column(String dv_column) {
		if(dv_column==null) throw new BusinessException("Entity : MlEfsearch.dv_column must not null!");
		this.dv_column = dv_column;
	}

	public BigDecimal getFeatlarg_num() { return featlarg_num; }
	public void setFeatlarg_num(BigDecimal featlarg_num) {
		if(featlarg_num==null) addNullValueField("featlarg_num");
		this.featlarg_num = featlarg_num;
	}

	public BigDecimal getFeatmin_num() { return featmin_num; }
	public void setFeatmin_num(BigDecimal featmin_num) {
		if(featmin_num==null) addNullValueField("featmin_num");
		this.featmin_num = featmin_num;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) addNullValueField("dtable_info_id");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getExhafeats_id() { return exhafeats_id; }
	public void setExhafeats_id(BigDecimal exhafeats_id) {
		if(exhafeats_id==null) throw new BusinessException("Entity : MlEfsearch.exhafeats_id must not null!");
		this.exhafeats_id = exhafeats_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlEfsearch.create_date must not null!");
		this.create_date = create_date;
	}

	public String getSpecific_algorithm() { return specific_algorithm; }
	public void setSpecific_algorithm(String specific_algorithm) {
		if(specific_algorithm==null) throw new BusinessException("Entity : MlEfsearch.specific_algorithm must not null!");
		this.specific_algorithm = specific_algorithm;
	}

	public String getAlgo_eval_crit() { return algo_eval_crit; }
	public void setAlgo_eval_crit(String algo_eval_crit) {
		if(algo_eval_crit==null) throw new BusinessException("Entity : MlEfsearch.algo_eval_crit must not null!");
		this.algo_eval_crit = algo_eval_crit;
	}

}