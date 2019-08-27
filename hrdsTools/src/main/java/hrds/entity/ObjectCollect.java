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
@Table(tableName = "object_collect")
public class ObjectCollect extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("odc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String object_collect_type;
	private String obj_collect_name;
	private String file_path;
	private BigDecimal agent_id;
	private String system_name;
	private String remark;
	private String server_date;
	private String e_date;
	private String is_sendok;
	private String run_way;
	private String local_time;
	private BigDecimal odc_id;
	private String database_code;
	private String s_date;
	private String obj_number;
	private String host_name;

	public String getObject_collect_type() { return object_collect_type; }
	public void setObject_collect_type(String object_collect_type) {
		if(object_collect_type==null) throw new BusinessException("Entity : ObjectCollect.object_collect_type must not null!");
		this.object_collect_type = object_collect_type;
	}

	public String getObj_collect_name() { return obj_collect_name; }
	public void setObj_collect_name(String obj_collect_name) {
		if(obj_collect_name==null) throw new BusinessException("Entity : ObjectCollect.obj_collect_name must not null!");
		this.obj_collect_name = obj_collect_name;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) throw new BusinessException("Entity : ObjectCollect.file_path must not null!");
		this.file_path = file_path;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : ObjectCollect.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getSystem_name() { return system_name; }
	public void setSystem_name(String system_name) {
		if(system_name==null) throw new BusinessException("Entity : ObjectCollect.system_name must not null!");
		this.system_name = system_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getServer_date() { return server_date; }
	public void setServer_date(String server_date) {
		if(server_date==null) throw new BusinessException("Entity : ObjectCollect.server_date must not null!");
		this.server_date = server_date;
	}

	public String getE_date() { return e_date; }
	public void setE_date(String e_date) {
		if(e_date==null) throw new BusinessException("Entity : ObjectCollect.e_date must not null!");
		this.e_date = e_date;
	}

	public String getIs_sendok() { return is_sendok; }
	public void setIs_sendok(String is_sendok) {
		if(is_sendok==null) throw new BusinessException("Entity : ObjectCollect.is_sendok must not null!");
		this.is_sendok = is_sendok;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : ObjectCollect.run_way must not null!");
		this.run_way = run_way;
	}

	public String getLocal_time() { return local_time; }
	public void setLocal_time(String local_time) {
		if(local_time==null) throw new BusinessException("Entity : ObjectCollect.local_time must not null!");
		this.local_time = local_time;
	}

	public BigDecimal getOdc_id() { return odc_id; }
	public void setOdc_id(BigDecimal odc_id) {
		if(odc_id==null) throw new BusinessException("Entity : ObjectCollect.odc_id must not null!");
		this.odc_id = odc_id;
	}

	public String getDatabase_code() { return database_code; }
	public void setDatabase_code(String database_code) {
		if(database_code==null) throw new BusinessException("Entity : ObjectCollect.database_code must not null!");
		this.database_code = database_code;
	}

	public String getS_date() { return s_date; }
	public void setS_date(String s_date) {
		if(s_date==null) throw new BusinessException("Entity : ObjectCollect.s_date must not null!");
		this.s_date = s_date;
	}

	public String getObj_number() { return obj_number; }
	public void setObj_number(String obj_number) {
		if(obj_number==null) throw new BusinessException("Entity : ObjectCollect.obj_number must not null!");
		this.obj_number = obj_number;
	}

	public String getHost_name() { return host_name; }
	public void setHost_name(String host_name) {
		if(host_name==null) throw new BusinessException("Entity : ObjectCollect.host_name must not null!");
		this.host_name = host_name;
	}

}