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
@Table(tableName = "ml_modelpredictinfo")
public class MlModelpredictinfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_modelpredictinfo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("predictinfo_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String predict_path;
	private String predictendtime;
	private String specificmodel;
	private String model_type;
	private BigDecimal dtable_info_id;
	private String model_path;
	private String predict_status;
	private BigDecimal predictinfo_id;
	private String filename;
	private BigDecimal project_id;
	private BigDecimal user_id;
	private String predictstratdate;
	private String predictstarttime;

	public String getPredict_path() { return predict_path; }
	public void setPredict_path(String predict_path) {
		if(predict_path==null) throw new BusinessException("Entity : MlModelpredictinfo.predict_path must not null!");
		this.predict_path = predict_path;
	}

	public String getPredictendtime() { return predictendtime; }
	public void setPredictendtime(String predictendtime) {
		if(predictendtime==null) throw new BusinessException("Entity : MlModelpredictinfo.predictendtime must not null!");
		this.predictendtime = predictendtime;
	}

	public String getSpecificmodel() { return specificmodel; }
	public void setSpecificmodel(String specificmodel) {
		if(specificmodel==null) throw new BusinessException("Entity : MlModelpredictinfo.specificmodel must not null!");
		this.specificmodel = specificmodel;
	}

	public String getModel_type() { return model_type; }
	public void setModel_type(String model_type) {
		if(model_type==null) throw new BusinessException("Entity : MlModelpredictinfo.model_type must not null!");
		this.model_type = model_type;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlModelpredictinfo.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getModel_path() { return model_path; }
	public void setModel_path(String model_path) {
		if(model_path==null) throw new BusinessException("Entity : MlModelpredictinfo.model_path must not null!");
		this.model_path = model_path;
	}

	public String getPredict_status() { return predict_status; }
	public void setPredict_status(String predict_status) {
		if(predict_status==null) throw new BusinessException("Entity : MlModelpredictinfo.predict_status must not null!");
		this.predict_status = predict_status;
	}

	public BigDecimal getPredictinfo_id() { return predictinfo_id; }
	public void setPredictinfo_id(BigDecimal predictinfo_id) {
		if(predictinfo_id==null) throw new BusinessException("Entity : MlModelpredictinfo.predictinfo_id must not null!");
		this.predictinfo_id = predictinfo_id;
	}

	public String getFilename() { return filename; }
	public void setFilename(String filename) {
		if(filename==null) throw new BusinessException("Entity : MlModelpredictinfo.filename must not null!");
		this.filename = filename;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : MlModelpredictinfo.project_id must not null!");
		this.project_id = project_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : MlModelpredictinfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getPredictstratdate() { return predictstratdate; }
	public void setPredictstratdate(String predictstratdate) {
		if(predictstratdate==null) throw new BusinessException("Entity : MlModelpredictinfo.predictstratdate must not null!");
		this.predictstratdate = predictstratdate;
	}

	public String getPredictstarttime() { return predictstarttime; }
	public void setPredictstarttime(String predictstarttime) {
		if(predictstarttime==null) throw new BusinessException("Entity : MlModelpredictinfo.predictstarttime must not null!");
		this.predictstarttime = predictstarttime;
	}

}