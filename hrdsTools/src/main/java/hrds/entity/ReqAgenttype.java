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
@Table(tableName = "req_agenttype")
public class ReqAgenttype extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "req_agenttype";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("req_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal req_id;
	private String req_name;
	private String req_remark;
	private String comp_id;
	private String req_no;

	public BigDecimal getReq_id() { return req_id; }
	public void setReq_id(BigDecimal req_id) {
		if(req_id==null) throw new BusinessException("Entity : ReqAgenttype.req_id must not null!");
		this.req_id = req_id;
	}

	public String getReq_name() { return req_name; }
	public void setReq_name(String req_name) {
		if(req_name==null) throw new BusinessException("Entity : ReqAgenttype.req_name must not null!");
		this.req_name = req_name;
	}

	public String getReq_remark() { return req_remark; }
	public void setReq_remark(String req_remark) {
		if(req_remark==null) addNullValueField("req_remark");
		this.req_remark = req_remark;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : ReqAgenttype.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getReq_no() { return req_no; }
	public void setReq_no(String req_no) {
		if(req_no==null) addNullValueField("req_no");
		this.req_no = req_no;
	}

}