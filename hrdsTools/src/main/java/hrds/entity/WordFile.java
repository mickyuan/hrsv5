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
@Table(tableName = "word_file")
public class WordFile extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_file";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("word_file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String word_file_name;
	private String create_id;
	private String create_date;
	private String create_time;
	private BigDecimal tokenizer_id;
	private BigDecimal word_file_id;

	public String getWord_file_name() { return word_file_name; }
	public void setWord_file_name(String word_file_name) {
		if(word_file_name==null) throw new BusinessException("Entity : WordFile.word_file_name must not null!");
		this.word_file_name = word_file_name;
	}

	public String getCreate_id() { return create_id; }
	public void setCreate_id(String create_id) {
		if(create_id==null) throw new BusinessException("Entity : WordFile.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : WordFile.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : WordFile.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getTokenizer_id() { return tokenizer_id; }
	public void setTokenizer_id(BigDecimal tokenizer_id) {
		if(tokenizer_id==null) throw new BusinessException("Entity : WordFile.tokenizer_id must not null!");
		this.tokenizer_id = tokenizer_id;
	}

	public BigDecimal getWord_file_id() { return word_file_id; }
	public void setWord_file_id(BigDecimal word_file_id) {
		if(word_file_id==null) throw new BusinessException("Entity : WordFile.word_file_id must not null!");
		this.word_file_id = word_file_id;
	}

}