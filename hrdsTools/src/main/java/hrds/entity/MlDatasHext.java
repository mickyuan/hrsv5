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
@Table(tableName = "ml_datas_hext")
public class MlDatasHext extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datas_hext";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hierextr_id");
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
	private String extr_column;
	private BigDecimal hierextr_id;
	private BigDecimal trainset_perc;
	private BigDecimal hierextr_seedn;

	public BigDecimal getDatasplit_id() { return datasplit_id; }
	public void setDatasplit_id(BigDecimal datasplit_id) {
		if(datasplit_id==null) addNullValueField("datasplit_id");
		this.datasplit_id = datasplit_id;
	}

	public String getExtr_column() { return extr_column; }
	public void setExtr_column(String extr_column) {
		if(extr_column==null) throw new BusinessException("Entity : MlDatasHext.extr_column must not null!");
		this.extr_column = extr_column;
	}

	public BigDecimal getHierextr_id() { return hierextr_id; }
	public void setHierextr_id(BigDecimal hierextr_id) {
		if(hierextr_id==null) throw new BusinessException("Entity : MlDatasHext.hierextr_id must not null!");
		this.hierextr_id = hierextr_id;
	}

	public BigDecimal getTrainset_perc() { return trainset_perc; }
	public void setTrainset_perc(BigDecimal trainset_perc) {
		if(trainset_perc==null) throw new BusinessException("Entity : MlDatasHext.trainset_perc must not null!");
		this.trainset_perc = trainset_perc;
	}

	public BigDecimal getHierextr_seedn() { return hierextr_seedn; }
	public void setHierextr_seedn(BigDecimal hierextr_seedn) {
		if(hierextr_seedn==null) throw new BusinessException("Entity : MlDatasHext.hierextr_seedn must not null!");
		this.hierextr_seedn = hierextr_seedn;
	}

}