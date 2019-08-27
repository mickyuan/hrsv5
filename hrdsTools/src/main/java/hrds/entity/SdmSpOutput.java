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
@Table(tableName = "sdm_sp_output")
public class SdmSpOutput extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_output";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String output_type;
	private String stream_tablename;
	private BigDecimal ssj_job_id;
	private String output_mode;
	private String output_table_name;
	private BigDecimal sdm_info_id;
	private BigDecimal output_number;

	public String getOutput_type() { return output_type; }
	public void setOutput_type(String output_type) {
		if(output_type==null) throw new BusinessException("Entity : SdmSpOutput.output_type must not null!");
		this.output_type = output_type;
	}

	public String getStream_tablename() { return stream_tablename; }
	public void setStream_tablename(String stream_tablename) {
		if(stream_tablename==null) addNullValueField("stream_tablename");
		this.stream_tablename = stream_tablename;
	}

	public BigDecimal getSsj_job_id() { return ssj_job_id; }
	public void setSsj_job_id(BigDecimal ssj_job_id) {
		if(ssj_job_id==null) throw new BusinessException("Entity : SdmSpOutput.ssj_job_id must not null!");
		this.ssj_job_id = ssj_job_id;
	}

	public String getOutput_mode() { return output_mode; }
	public void setOutput_mode(String output_mode) {
		if(output_mode==null) throw new BusinessException("Entity : SdmSpOutput.output_mode must not null!");
		this.output_mode = output_mode;
	}

	public String getOutput_table_name() { return output_table_name; }
	public void setOutput_table_name(String output_table_name) {
		if(output_table_name==null) throw new BusinessException("Entity : SdmSpOutput.output_table_name must not null!");
		this.output_table_name = output_table_name;
	}

	public BigDecimal getSdm_info_id() { return sdm_info_id; }
	public void setSdm_info_id(BigDecimal sdm_info_id) {
		if(sdm_info_id==null) throw new BusinessException("Entity : SdmSpOutput.sdm_info_id must not null!");
		this.sdm_info_id = sdm_info_id;
	}

	public BigDecimal getOutput_number() { return output_number; }
	public void setOutput_number(BigDecimal output_number) {
		if(output_number==null) throw new BusinessException("Entity : SdmSpOutput.output_number must not null!");
		this.output_number = output_number;
	}

}