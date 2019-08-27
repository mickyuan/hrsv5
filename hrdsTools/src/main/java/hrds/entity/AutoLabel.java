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
@Table(tableName = "auto_label")
public class AutoLabel extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_label";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("lable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String formatter;
	private String label_corr_tname;
	private BigDecimal lable_id;
	private Integer length2;
	private Integer length;
	private BigDecimal label_corr_id;
	private String position;
	private String show_label;
	private String show_line;
	private String smooth;

	public String getFormatter() { return formatter; }
	public void setFormatter(String formatter) {
		if(formatter==null) addNullValueField("formatter");
		this.formatter = formatter;
	}

	public String getLabel_corr_tname() { return label_corr_tname; }
	public void setLabel_corr_tname(String label_corr_tname) {
		if(label_corr_tname==null) throw new BusinessException("Entity : AutoLabel.label_corr_tname must not null!");
		this.label_corr_tname = label_corr_tname;
	}

	public BigDecimal getLable_id() { return lable_id; }
	public void setLable_id(BigDecimal lable_id) {
		if(lable_id==null) throw new BusinessException("Entity : AutoLabel.lable_id must not null!");
		this.lable_id = lable_id;
	}

	public Integer getLength2() { return length2; }
	public void setLength2(Integer length2) {
		if(length2==null) addNullValueField("length2");
		this.length2 = length2;
	}

	public Integer getLength() { return length; }
	public void setLength(Integer length) {
		if(length==null) addNullValueField("length");
		this.length = length;
	}

	public BigDecimal getLabel_corr_id() { return label_corr_id; }
	public void setLabel_corr_id(BigDecimal label_corr_id) {
		if(label_corr_id==null) throw new BusinessException("Entity : AutoLabel.label_corr_id must not null!");
		this.label_corr_id = label_corr_id;
	}

	public String getPosition() { return position; }
	public void setPosition(String position) {
		if(position==null) addNullValueField("position");
		this.position = position;
	}

	public String getShow_label() { return show_label; }
	public void setShow_label(String show_label) {
		if(show_label==null) throw new BusinessException("Entity : AutoLabel.show_label must not null!");
		this.show_label = show_label;
	}

	public String getShow_line() { return show_line; }
	public void setShow_line(String show_line) {
		if(show_line==null) throw new BusinessException("Entity : AutoLabel.show_line must not null!");
		this.show_line = show_line;
	}

	public String getSmooth() { return smooth; }
	public void setSmooth(String smooth) {
		if(smooth==null) throw new BusinessException("Entity : AutoLabel.smooth must not null!");
		this.smooth = smooth;
	}

}