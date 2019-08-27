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
@Table(tableName = "word_database")
public class WordDatabase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_database";

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

	private String world_frequency;
	private String word_property;
	private BigDecimal word_id;
	private BigDecimal create_id;
	private String create_time;
	private String word_nature;
	private String is_flag_select;
	private String create_date;
	private String word;
	private String is_flag_product;
	private BigDecimal word_file_id;

	public String getWorld_frequency() { return world_frequency; }
	public void setWorld_frequency(String world_frequency) {
		if(world_frequency==null) throw new BusinessException("Entity : WordDatabase.world_frequency must not null!");
		this.world_frequency = world_frequency;
	}

	public String getWord_property() { return word_property; }
	public void setWord_property(String word_property) {
		if(word_property==null) throw new BusinessException("Entity : WordDatabase.word_property must not null!");
		this.word_property = word_property;
	}

	public BigDecimal getWord_id() { return word_id; }
	public void setWord_id(BigDecimal word_id) {
		if(word_id==null) throw new BusinessException("Entity : WordDatabase.word_id must not null!");
		this.word_id = word_id;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : WordDatabase.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : WordDatabase.create_time must not null!");
		this.create_time = create_time;
	}

	public String getWord_nature() { return word_nature; }
	public void setWord_nature(String word_nature) {
		if(word_nature==null) throw new BusinessException("Entity : WordDatabase.word_nature must not null!");
		this.word_nature = word_nature;
	}

	public String getIs_flag_select() { return is_flag_select; }
	public void setIs_flag_select(String is_flag_select) {
		if(is_flag_select==null) throw new BusinessException("Entity : WordDatabase.is_flag_select must not null!");
		this.is_flag_select = is_flag_select;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : WordDatabase.create_date must not null!");
		this.create_date = create_date;
	}

	public String getWord() { return word; }
	public void setWord(String word) {
		if(word==null) throw new BusinessException("Entity : WordDatabase.word must not null!");
		this.word = word;
	}

	public String getIs_flag_product() { return is_flag_product; }
	public void setIs_flag_product(String is_flag_product) {
		if(is_flag_product==null) throw new BusinessException("Entity : WordDatabase.is_flag_product must not null!");
		this.is_flag_product = is_flag_product;
	}

	public BigDecimal getWord_file_id() { return word_file_id; }
	public void setWord_file_id(BigDecimal word_file_id) {
		if(word_file_id==null) throw new BusinessException("Entity : WordDatabase.word_file_id must not null!");
		this.word_file_id = word_file_id;
	}

}