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
@Table(tableName = "search_info")
public class SearchInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "search_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("si_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal si_id;
	private String word_name;
	private String si_remark;
	private String file_id;
	private BigDecimal si_count;

	public BigDecimal getSi_id() { return si_id; }
	public void setSi_id(BigDecimal si_id) {
		if(si_id==null) throw new BusinessException("Entity : SearchInfo.si_id must not null!");
		this.si_id = si_id;
	}

	public String getWord_name() { return word_name; }
	public void setWord_name(String word_name) {
		if(word_name==null) throw new BusinessException("Entity : SearchInfo.word_name must not null!");
		this.word_name = word_name;
	}

	public String getSi_remark() { return si_remark; }
	public void setSi_remark(String si_remark) {
		if(si_remark==null) addNullValueField("si_remark");
		this.si_remark = si_remark;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : SearchInfo.file_id must not null!");
		this.file_id = file_id;
	}

	public BigDecimal getSi_count() { return si_count; }
	public void setSi_count(BigDecimal si_count) {
		if(si_count==null) throw new BusinessException("Entity : SearchInfo.si_count must not null!");
		this.si_count = si_count;
	}

}