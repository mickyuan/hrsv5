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
@Table(tableName = "translate_base")
public class TranslateBase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "translate_base";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("trn_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String eng_name;
	private String china_name;
	private BigDecimal trn_id;
	private String english_name;

	public String getEng_name() { return eng_name; }
	public void setEng_name(String eng_name) {
		if(eng_name==null) addNullValueField("eng_name");
		this.eng_name = eng_name;
	}

	public String getChina_name() { return china_name; }
	public void setChina_name(String china_name) {
		if(china_name==null) throw new BusinessException("Entity : TranslateBase.china_name must not null!");
		this.china_name = china_name;
	}

	public BigDecimal getTrn_id() { return trn_id; }
	public void setTrn_id(BigDecimal trn_id) {
		if(trn_id==null) throw new BusinessException("Entity : TranslateBase.trn_id must not null!");
		this.trn_id = trn_id;
	}

	public String getEnglish_name() { return english_name; }
	public void setEnglish_name(String english_name) {
		if(english_name==null) addNullValueField("english_name");
		this.english_name = english_name;
	}

}