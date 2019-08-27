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
@Table(tableName = "dq_list")
public class DqList extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_list";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("reg_num");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String mail_receive;
	private String opposite_key_fields;
	private String check_limit_condition;
	private String flags;
	private String range_min_val;
	private String remark;
	private String is_saveindex3;
	private String is_saveindex2;
	private String is_saveindex1;
	private String rule_tag;
	private String target_key_fields;
	private String reg_name;
	private String range_max_val;
	private String opposite_tab;
	private String app_updt_dt;
	private String target_tab;
	private BigDecimal reg_num;
	private String rule_src;
	private String case_type;
	private String index_desc2;
	private String index_desc3;
	private String app_updt_ti;
	private String specify_sql;
	private String index_desc1;
	private BigDecimal user_id;
	private String load_strategy;
	private String list_vals;
	private String err_data_sql;
	private String group_seq;

	public String getMail_receive() { return mail_receive; }
	public void setMail_receive(String mail_receive) {
		if(mail_receive==null) addNullValueField("mail_receive");
		this.mail_receive = mail_receive;
	}

	public String getOpposite_key_fields() { return opposite_key_fields; }
	public void setOpposite_key_fields(String opposite_key_fields) {
		if(opposite_key_fields==null) addNullValueField("opposite_key_fields");
		this.opposite_key_fields = opposite_key_fields;
	}

	public String getCheck_limit_condition() { return check_limit_condition; }
	public void setCheck_limit_condition(String check_limit_condition) {
		if(check_limit_condition==null) addNullValueField("check_limit_condition");
		this.check_limit_condition = check_limit_condition;
	}

	public String getFlags() { return flags; }
	public void setFlags(String flags) {
		if(flags==null) addNullValueField("flags");
		this.flags = flags;
	}

	public String getRange_min_val() { return range_min_val; }
	public void setRange_min_val(String range_min_val) {
		if(range_min_val==null) addNullValueField("range_min_val");
		this.range_min_val = range_min_val;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_saveindex3() { return is_saveindex3; }
	public void setIs_saveindex3(String is_saveindex3) {
		if(is_saveindex3==null) addNullValueField("is_saveindex3");
		this.is_saveindex3 = is_saveindex3;
	}

	public String getIs_saveindex2() { return is_saveindex2; }
	public void setIs_saveindex2(String is_saveindex2) {
		if(is_saveindex2==null) addNullValueField("is_saveindex2");
		this.is_saveindex2 = is_saveindex2;
	}

	public String getIs_saveindex1() { return is_saveindex1; }
	public void setIs_saveindex1(String is_saveindex1) {
		if(is_saveindex1==null) addNullValueField("is_saveindex1");
		this.is_saveindex1 = is_saveindex1;
	}

	public String getRule_tag() { return rule_tag; }
	public void setRule_tag(String rule_tag) {
		if(rule_tag==null) addNullValueField("rule_tag");
		this.rule_tag = rule_tag;
	}

	public String getTarget_key_fields() { return target_key_fields; }
	public void setTarget_key_fields(String target_key_fields) {
		if(target_key_fields==null) addNullValueField("target_key_fields");
		this.target_key_fields = target_key_fields;
	}

	public String getReg_name() { return reg_name; }
	public void setReg_name(String reg_name) {
		if(reg_name==null) addNullValueField("reg_name");
		this.reg_name = reg_name;
	}

	public String getRange_max_val() { return range_max_val; }
	public void setRange_max_val(String range_max_val) {
		if(range_max_val==null) addNullValueField("range_max_val");
		this.range_max_val = range_max_val;
	}

	public String getOpposite_tab() { return opposite_tab; }
	public void setOpposite_tab(String opposite_tab) {
		if(opposite_tab==null) addNullValueField("opposite_tab");
		this.opposite_tab = opposite_tab;
	}

	public String getApp_updt_dt() { return app_updt_dt; }
	public void setApp_updt_dt(String app_updt_dt) {
		if(app_updt_dt==null) throw new BusinessException("Entity : DqList.app_updt_dt must not null!");
		this.app_updt_dt = app_updt_dt;
	}

	public String getTarget_tab() { return target_tab; }
	public void setTarget_tab(String target_tab) {
		if(target_tab==null) addNullValueField("target_tab");
		this.target_tab = target_tab;
	}

	public BigDecimal getReg_num() { return reg_num; }
	public void setReg_num(BigDecimal reg_num) {
		if(reg_num==null) throw new BusinessException("Entity : DqList.reg_num must not null!");
		this.reg_num = reg_num;
	}

	public String getRule_src() { return rule_src; }
	public void setRule_src(String rule_src) {
		if(rule_src==null) addNullValueField("rule_src");
		this.rule_src = rule_src;
	}

	public String getCase_type() { return case_type; }
	public void setCase_type(String case_type) {
		if(case_type==null) throw new BusinessException("Entity : DqList.case_type must not null!");
		this.case_type = case_type;
	}

	public String getIndex_desc2() { return index_desc2; }
	public void setIndex_desc2(String index_desc2) {
		if(index_desc2==null) addNullValueField("index_desc2");
		this.index_desc2 = index_desc2;
	}

	public String getIndex_desc3() { return index_desc3; }
	public void setIndex_desc3(String index_desc3) {
		if(index_desc3==null) addNullValueField("index_desc3");
		this.index_desc3 = index_desc3;
	}

	public String getApp_updt_ti() { return app_updt_ti; }
	public void setApp_updt_ti(String app_updt_ti) {
		if(app_updt_ti==null) throw new BusinessException("Entity : DqList.app_updt_ti must not null!");
		this.app_updt_ti = app_updt_ti;
	}

	public String getSpecify_sql() { return specify_sql; }
	public void setSpecify_sql(String specify_sql) {
		if(specify_sql==null) addNullValueField("specify_sql");
		this.specify_sql = specify_sql;
	}

	public String getIndex_desc1() { return index_desc1; }
	public void setIndex_desc1(String index_desc1) {
		if(index_desc1==null) addNullValueField("index_desc1");
		this.index_desc1 = index_desc1;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : DqList.user_id must not null!");
		this.user_id = user_id;
	}

	public String getLoad_strategy() { return load_strategy; }
	public void setLoad_strategy(String load_strategy) {
		if(load_strategy==null) addNullValueField("load_strategy");
		this.load_strategy = load_strategy;
	}

	public String getList_vals() { return list_vals; }
	public void setList_vals(String list_vals) {
		if(list_vals==null) addNullValueField("list_vals");
		this.list_vals = list_vals;
	}

	public String getErr_data_sql() { return err_data_sql; }
	public void setErr_data_sql(String err_data_sql) {
		if(err_data_sql==null) addNullValueField("err_data_sql");
		this.err_data_sql = err_data_sql;
	}

	public String getGroup_seq() { return group_seq; }
	public void setGroup_seq(String group_seq) {
		if(group_seq==null) addNullValueField("group_seq");
		this.group_seq = group_seq;
	}

}