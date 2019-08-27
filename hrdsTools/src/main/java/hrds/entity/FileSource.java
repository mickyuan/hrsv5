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
@Table(tableName = "file_source")
public class FileSource extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "file_source";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal file_source_id;
	private String is_video;
	private String is_pdf;
	private String is_audio;
	private BigDecimal fcs_id;
	private BigDecimal agent_id;
	private String file_source_path;
	private String is_image;
	private String is_other;
	private String file_remark;
	private String is_office;
	private String is_text;

	public BigDecimal getFile_source_id() { return file_source_id; }
	public void setFile_source_id(BigDecimal file_source_id) {
		if(file_source_id==null) throw new BusinessException("Entity : FileSource.file_source_id must not null!");
		this.file_source_id = file_source_id;
	}

	public String getIs_video() { return is_video; }
	public void setIs_video(String is_video) {
		if(is_video==null) throw new BusinessException("Entity : FileSource.is_video must not null!");
		this.is_video = is_video;
	}

	public String getIs_pdf() { return is_pdf; }
	public void setIs_pdf(String is_pdf) {
		if(is_pdf==null) throw new BusinessException("Entity : FileSource.is_pdf must not null!");
		this.is_pdf = is_pdf;
	}

	public String getIs_audio() { return is_audio; }
	public void setIs_audio(String is_audio) {
		if(is_audio==null) throw new BusinessException("Entity : FileSource.is_audio must not null!");
		this.is_audio = is_audio;
	}

	public BigDecimal getFcs_id() { return fcs_id; }
	public void setFcs_id(BigDecimal fcs_id) {
		if(fcs_id==null) throw new BusinessException("Entity : FileSource.fcs_id must not null!");
		this.fcs_id = fcs_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : FileSource.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getFile_source_path() { return file_source_path; }
	public void setFile_source_path(String file_source_path) {
		if(file_source_path==null) throw new BusinessException("Entity : FileSource.file_source_path must not null!");
		this.file_source_path = file_source_path;
	}

	public String getIs_image() { return is_image; }
	public void setIs_image(String is_image) {
		if(is_image==null) throw new BusinessException("Entity : FileSource.is_image must not null!");
		this.is_image = is_image;
	}

	public String getIs_other() { return is_other; }
	public void setIs_other(String is_other) {
		if(is_other==null) throw new BusinessException("Entity : FileSource.is_other must not null!");
		this.is_other = is_other;
	}

	public String getFile_remark() { return file_remark; }
	public void setFile_remark(String file_remark) {
		if(file_remark==null) addNullValueField("file_remark");
		this.file_remark = file_remark;
	}

	public String getIs_office() { return is_office; }
	public void setIs_office(String is_office) {
		if(is_office==null) throw new BusinessException("Entity : FileSource.is_office must not null!");
		this.is_office = is_office;
	}

	public String getIs_text() { return is_text; }
	public void setIs_text(String is_text) {
		if(is_text==null) throw new BusinessException("Entity : FileSource.is_text must not null!");
		this.is_text = is_text;
	}

}