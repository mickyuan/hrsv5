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
@Table(tableName = "ml_datas_srex")
public class MlDatasSrex extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datas_srex";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rextr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal datasplit_id;
	private BigDecimal testset_perc;
	private BigDecimal rextrseed_num;
	private BigDecimal trainset_perc;
	private BigDecimal rextr_id;

	public BigDecimal getDatasplit_id() { return datasplit_id; }
	public void setDatasplit_id(BigDecimal datasplit_id) {
		if(datasplit_id==null) addNullValueField("datasplit_id");
		this.datasplit_id = datasplit_id;
	}

	public BigDecimal getTestset_perc() { return testset_perc; }
	public void setTestset_perc(BigDecimal testset_perc) {
		if(testset_perc==null) throw new BusinessException("Entity : MlDatasSrex.testset_perc must not null!");
		this.testset_perc = testset_perc;
	}

	public BigDecimal getRextrseed_num() { return rextrseed_num; }
	public void setRextrseed_num(BigDecimal rextrseed_num) {
		if(rextrseed_num==null) throw new BusinessException("Entity : MlDatasSrex.rextrseed_num must not null!");
		this.rextrseed_num = rextrseed_num;
	}

	public BigDecimal getTrainset_perc() { return trainset_perc; }
	public void setTrainset_perc(BigDecimal trainset_perc) {
		if(trainset_perc==null) throw new BusinessException("Entity : MlDatasSrex.trainset_perc must not null!");
		this.trainset_perc = trainset_perc;
	}

	public BigDecimal getRextr_id() { return rextr_id; }
	public void setRextr_id(BigDecimal rextr_id) {
		if(rextr_id==null) throw new BusinessException("Entity : MlDatasSrex.rextr_id must not null!");
		this.rextr_id = rextr_id;
	}

}