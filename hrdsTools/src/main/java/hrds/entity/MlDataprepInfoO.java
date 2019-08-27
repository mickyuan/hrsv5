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
@Table(tableName = "ml_dataprep_info_o")
public class MlDataprepInfoO extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dataprep_info_o";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datapo_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal new_filesize;
	private String create_time;
	private BigDecimal datapo_info_id;
	private String generate_is_flag;
	private String remark;
	private BigDecimal dtable_info_id;
	private String create_date;
	private String datamapmode;
	private String new_filepath;
	private String newtable_name;

	public BigDecimal getNew_filesize() { return new_filesize; }
	public void setNew_filesize(BigDecimal new_filesize) {
		if(new_filesize==null) throw new BusinessException("Entity : MlDataprepInfoO.new_filesize must not null!");
		this.new_filesize = new_filesize;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlDataprepInfoO.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getDatapo_info_id() { return datapo_info_id; }
	public void setDatapo_info_id(BigDecimal datapo_info_id) {
		if(datapo_info_id==null) throw new BusinessException("Entity : MlDataprepInfoO.datapo_info_id must not null!");
		this.datapo_info_id = datapo_info_id;
	}

	public String getGenerate_is_flag() { return generate_is_flag; }
	public void setGenerate_is_flag(String generate_is_flag) {
		if(generate_is_flag==null) throw new BusinessException("Entity : MlDataprepInfoO.generate_is_flag must not null!");
		this.generate_is_flag = generate_is_flag;
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

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlDataprepInfoO.create_date must not null!");
		this.create_date = create_date;
	}

	public String getDatamapmode() { return datamapmode; }
	public void setDatamapmode(String datamapmode) {
		if(datamapmode==null) throw new BusinessException("Entity : MlDataprepInfoO.datamapmode must not null!");
		this.datamapmode = datamapmode;
	}

	public String getNew_filepath() { return new_filepath; }
	public void setNew_filepath(String new_filepath) {
		if(new_filepath==null) throw new BusinessException("Entity : MlDataprepInfoO.new_filepath must not null!");
		this.new_filepath = new_filepath;
	}

	public String getNewtable_name() { return newtable_name; }
	public void setNewtable_name(String newtable_name) {
		if(newtable_name==null) throw new BusinessException("Entity : MlDataprepInfoO.newtable_name must not null!");
		this.newtable_name = newtable_name;
	}

}