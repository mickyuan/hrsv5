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
@Table(tableName = "user_fav")
public class UserFav extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "user_fav";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fav_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String fav_flag;
	private BigDecimal fav_id;
	private BigDecimal user_id;
	private String original_name;
	private String file_id;

	public String getFav_flag() { return fav_flag; }
	public void setFav_flag(String fav_flag) {
		if(fav_flag==null) throw new BusinessException("Entity : UserFav.fav_flag must not null!");
		this.fav_flag = fav_flag;
	}

	public BigDecimal getFav_id() { return fav_id; }
	public void setFav_id(BigDecimal fav_id) {
		if(fav_id==null) throw new BusinessException("Entity : UserFav.fav_id must not null!");
		this.fav_id = fav_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) addNullValueField("user_id");
		this.user_id = user_id;
	}

	public String getOriginal_name() { return original_name; }
	public void setOriginal_name(String original_name) {
		if(original_name==null) throw new BusinessException("Entity : UserFav.original_name must not null!");
		this.original_name = original_name;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : UserFav.file_id must not null!");
		this.file_id = file_id;
	}

}