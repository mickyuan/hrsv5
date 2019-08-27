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
@Table(tableName = "word_production")
public class WordProduction extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_production";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("word_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal word_id;
	private String word_nature;
	private String word;
	private BigDecimal tokenizer_id;

	public BigDecimal getWord_id() { return word_id; }
	public void setWord_id(BigDecimal word_id) {
		if(word_id==null) throw new BusinessException("Entity : WordProduction.word_id must not null!");
		this.word_id = word_id;
	}

	public String getWord_nature() { return word_nature; }
	public void setWord_nature(String word_nature) {
		if(word_nature==null) throw new BusinessException("Entity : WordProduction.word_nature must not null!");
		this.word_nature = word_nature;
	}

	public String getWord() { return word; }
	public void setWord(String word) {
		if(word==null) throw new BusinessException("Entity : WordProduction.word must not null!");
		this.word = word;
	}

	public BigDecimal getTokenizer_id() { return tokenizer_id; }
	public void setTokenizer_id(BigDecimal tokenizer_id) {
		if(tokenizer_id==null) throw new BusinessException("Entity : WordProduction.tokenizer_id must not null!");
		this.tokenizer_id = tokenizer_id;
	}

}