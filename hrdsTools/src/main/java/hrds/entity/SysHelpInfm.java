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
@Table(tableName = "sys_help_infm")
public class SysHelpInfm extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_help_infm";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("help_infm_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String help_infm_id;
	private String help_infm_dtl;
	private String help_infm_desc;

	public String getHelp_infm_id() { return help_infm_id; }
	public void setHelp_infm_id(String help_infm_id) {
		if(help_infm_id==null) throw new BusinessException("Entity : SysHelpInfm.help_infm_id must not null!");
		this.help_infm_id = help_infm_id;
	}

	public String getHelp_infm_dtl() { return help_infm_dtl; }
	public void setHelp_infm_dtl(String help_infm_dtl) {
		if(help_infm_dtl==null) addNullValueField("help_infm_dtl");
		this.help_infm_dtl = help_infm_dtl;
	}

	public String getHelp_infm_desc() { return help_infm_desc; }
	public void setHelp_infm_desc(String help_infm_desc) {
		if(help_infm_desc==null) addNullValueField("help_infm_desc");
		this.help_infm_desc = help_infm_desc;
	}

}