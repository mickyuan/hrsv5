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
@Table(tableName = "tokenizer_list")
public class TokenizerList extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "tokenizer_list";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tokenizer_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal tokenizer_id;
	private String tokenizer_name;
	private String is_flag;

	public BigDecimal getTokenizer_id() { return tokenizer_id; }
	public void setTokenizer_id(BigDecimal tokenizer_id) {
		if(tokenizer_id==null) throw new BusinessException("Entity : TokenizerList.tokenizer_id must not null!");
		this.tokenizer_id = tokenizer_id;
	}

	public String getTokenizer_name() { return tokenizer_name; }
	public void setTokenizer_name(String tokenizer_name) {
		if(tokenizer_name==null) throw new BusinessException("Entity : TokenizerList.tokenizer_name must not null!");
		this.tokenizer_name = tokenizer_name;
	}

	public String getIs_flag() { return is_flag; }
	public void setIs_flag(String is_flag) {
		if(is_flag==null) throw new BusinessException("Entity : TokenizerList.is_flag must not null!");
		this.is_flag = is_flag;
	}

}