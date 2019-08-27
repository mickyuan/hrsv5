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
@Table(tableName = "object_collect_struct")
public class ObjectCollectStruct extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect_struct";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("struct_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal struct_id;
	private BigDecimal ocs_id;
	private String remark;
	private String coll_name;
	private String struct_type;
	private String data_desc;

	public BigDecimal getStruct_id() { return struct_id; }
	public void setStruct_id(BigDecimal struct_id) {
		if(struct_id==null) throw new BusinessException("Entity : ObjectCollectStruct.struct_id must not null!");
		this.struct_id = struct_id;
	}

	public BigDecimal getOcs_id() { return ocs_id; }
	public void setOcs_id(BigDecimal ocs_id) {
		if(ocs_id==null) throw new BusinessException("Entity : ObjectCollectStruct.ocs_id must not null!");
		this.ocs_id = ocs_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getColl_name() { return coll_name; }
	public void setColl_name(String coll_name) {
		if(coll_name==null) throw new BusinessException("Entity : ObjectCollectStruct.coll_name must not null!");
		this.coll_name = coll_name;
	}

	public String getStruct_type() { return struct_type; }
	public void setStruct_type(String struct_type) {
		if(struct_type==null) throw new BusinessException("Entity : ObjectCollectStruct.struct_type must not null!");
		this.struct_type = struct_type;
	}

	public String getData_desc() { return data_desc; }
	public void setData_desc(String data_desc) {
		if(data_desc==null) addNullValueField("data_desc");
		this.data_desc = data_desc;
	}

}