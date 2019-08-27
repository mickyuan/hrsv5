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
@Table(tableName = "sdm_job_input")
public class SdmJobInput extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_job_input";

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

	private String input_en_name;
	private String input_source;
	private String input_data_type;
	private BigDecimal input_number;
	private String input_cn_name;
	private String input_table_name;
	private BigDecimal ssj_job_id;
	private String input_type;
	private BigDecimal sdm_info_id;

	public String getInput_en_name() { return input_en_name; }
	public void setInput_en_name(String input_en_name) {
		if(input_en_name==null) throw new BusinessException("Entity : SdmJobInput.input_en_name must not null!");
		this.input_en_name = input_en_name;
	}

	public String getInput_source() { return input_source; }
	public void setInput_source(String input_source) {
		if(input_source==null) throw new BusinessException("Entity : SdmJobInput.input_source must not null!");
		this.input_source = input_source;
	}

	public String getInput_data_type() { return input_data_type; }
	public void setInput_data_type(String input_data_type) {
		if(input_data_type==null) throw new BusinessException("Entity : SdmJobInput.input_data_type must not null!");
		this.input_data_type = input_data_type;
	}

	public BigDecimal getInput_number() { return input_number; }
	public void setInput_number(BigDecimal input_number) {
		if(input_number==null) throw new BusinessException("Entity : SdmJobInput.input_number must not null!");
		this.input_number = input_number;
	}

	public String getInput_cn_name() { return input_cn_name; }
	public void setInput_cn_name(String input_cn_name) {
		if(input_cn_name==null) addNullValueField("input_cn_name");
		this.input_cn_name = input_cn_name;
	}

	public String getInput_table_name() { return input_table_name; }
	public void setInput_table_name(String input_table_name) {
		if(input_table_name==null) throw new BusinessException("Entity : SdmJobInput.input_table_name must not null!");
		this.input_table_name = input_table_name;
	}

	public BigDecimal getSsj_job_id() { return ssj_job_id; }
	public void setSsj_job_id(BigDecimal ssj_job_id) {
		if(ssj_job_id==null) throw new BusinessException("Entity : SdmJobInput.ssj_job_id must not null!");
		this.ssj_job_id = ssj_job_id;
	}

	public String getInput_type() { return input_type; }
	public void setInput_type(String input_type) {
		if(input_type==null) throw new BusinessException("Entity : SdmJobInput.input_type must not null!");
		this.input_type = input_type;
	}

	public BigDecimal getSdm_info_id() { return sdm_info_id; }
	public void setSdm_info_id(BigDecimal sdm_info_id) {
		if(sdm_info_id==null) throw new BusinessException("Entity : SdmJobInput.sdm_info_id must not null!");
		this.sdm_info_id = sdm_info_id;
	}

}