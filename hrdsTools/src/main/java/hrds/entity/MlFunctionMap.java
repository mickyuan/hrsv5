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
@Table(tableName = "ml_function_map")
public class MlFunctionMap extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_function_map";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("funmap_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String transfer_method;
	private BigDecimal tranfunt_num;
	private BigDecimal funmap_id;
	private String specific_fun;

	public String getTransfer_method() { return transfer_method; }
	public void setTransfer_method(String transfer_method) {
		if(transfer_method==null) throw new BusinessException("Entity : MlFunctionMap.transfer_method must not null!");
		this.transfer_method = transfer_method;
	}

	public BigDecimal getTranfunt_num() { return tranfunt_num; }
	public void setTranfunt_num(BigDecimal tranfunt_num) {
		if(tranfunt_num==null) throw new BusinessException("Entity : MlFunctionMap.tranfunt_num must not null!");
		this.tranfunt_num = tranfunt_num;
	}

	public BigDecimal getFunmap_id() { return funmap_id; }
	public void setFunmap_id(BigDecimal funmap_id) {
		if(funmap_id==null) throw new BusinessException("Entity : MlFunctionMap.funmap_id must not null!");
		this.funmap_id = funmap_id;
	}

	public String getSpecific_fun() { return specific_fun; }
	public void setSpecific_fun(String specific_fun) {
		if(specific_fun==null) throw new BusinessException("Entity : MlFunctionMap.specific_fun must not null!");
		this.specific_fun = specific_fun;
	}

}