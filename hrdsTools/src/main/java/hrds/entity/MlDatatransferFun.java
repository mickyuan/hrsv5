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
@Table(tableName = "ml_datatransfer_fun")
public class MlDatatransferFun extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datatransfer_fun";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tranfunt_num");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal tranfunt_num;
	private String funtype_name;

	public BigDecimal getTranfunt_num() { return tranfunt_num; }
	public void setTranfunt_num(BigDecimal tranfunt_num) {
		if(tranfunt_num==null) throw new BusinessException("Entity : MlDatatransferFun.tranfunt_num must not null!");
		this.tranfunt_num = tranfunt_num;
	}

	public String getFuntype_name() { return funtype_name; }
	public void setFuntype_name(String funtype_name) {
		if(funtype_name==null) throw new BusinessException("Entity : MlDatatransferFun.funtype_name must not null!");
		this.funtype_name = funtype_name;
	}

}