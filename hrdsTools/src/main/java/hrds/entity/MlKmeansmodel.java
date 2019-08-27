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
@Table(tableName = "ml_kmeansmodel")
public class MlKmeansmodel extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_kmeansmodel";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("model_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String model_runstate;
	private String observe_columns;
	private String model_name;
	private String create_time;
	private BigDecimal numiterations;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal model_id;
	private String create_date;
	private String model_path;
	private BigDecimal clusternum;

	public String getModel_runstate() { return model_runstate; }
	public void setModel_runstate(String model_runstate) {
		if(model_runstate==null) throw new BusinessException("Entity : MlKmeansmodel.model_runstate must not null!");
		this.model_runstate = model_runstate;
	}

	public String getObserve_columns() { return observe_columns; }
	public void setObserve_columns(String observe_columns) {
		if(observe_columns==null) addNullValueField("observe_columns");
		this.observe_columns = observe_columns;
	}

	public String getModel_name() { return model_name; }
	public void setModel_name(String model_name) {
		if(model_name==null) throw new BusinessException("Entity : MlKmeansmodel.model_name must not null!");
		this.model_name = model_name;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlKmeansmodel.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getNumiterations() { return numiterations; }
	public void setNumiterations(BigDecimal numiterations) {
		if(numiterations==null) throw new BusinessException("Entity : MlKmeansmodel.numiterations must not null!");
		this.numiterations = numiterations;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlKmeansmodel.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getModel_id() { return model_id; }
	public void setModel_id(BigDecimal model_id) {
		if(model_id==null) throw new BusinessException("Entity : MlKmeansmodel.model_id must not null!");
		this.model_id = model_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlKmeansmodel.create_date must not null!");
		this.create_date = create_date;
	}

	public String getModel_path() { return model_path; }
	public void setModel_path(String model_path) {
		if(model_path==null) addNullValueField("model_path");
		this.model_path = model_path;
	}

	public BigDecimal getClusternum() { return clusternum; }
	public void setClusternum(BigDecimal clusternum) {
		if(clusternum==null) throw new BusinessException("Entity : MlKmeansmodel.clusternum must not null!");
		this.clusternum = clusternum;
	}

}