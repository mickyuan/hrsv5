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
@Table(tableName = "object_storage")
public class ObjectStorage extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_storage";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("obj_stid");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_hdfs;
	private BigDecimal ocs_id;
	private String remark;
	private BigDecimal obj_stid;
	private String is_hbase;

	public String getIs_hdfs() { return is_hdfs; }
	public void setIs_hdfs(String is_hdfs) {
		if(is_hdfs==null) throw new BusinessException("Entity : ObjectStorage.is_hdfs must not null!");
		this.is_hdfs = is_hdfs;
	}

	public BigDecimal getOcs_id() { return ocs_id; }
	public void setOcs_id(BigDecimal ocs_id) {
		if(ocs_id==null) addNullValueField("ocs_id");
		this.ocs_id = ocs_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getObj_stid() { return obj_stid; }
	public void setObj_stid(BigDecimal obj_stid) {
		if(obj_stid==null) throw new BusinessException("Entity : ObjectStorage.obj_stid must not null!");
		this.obj_stid = obj_stid;
	}

	public String getIs_hbase() { return is_hbase; }
	public void setIs_hbase(String is_hbase) {
		if(is_hbase==null) throw new BusinessException("Entity : ObjectStorage.is_hbase must not null!");
		this.is_hbase = is_hbase;
	}

}