package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据质量规则配置清单表
 */
@Table(tableName = "dq_definition")
public class Dq_definition extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_definition";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据质量规则配置清单表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("reg_num");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="reg_num",value="规则编号:",dataType = Long.class,required = true)
	private Long reg_num;
	@DocBean(name ="reg_name",value="规则名称:",dataType = String.class,required = false)
	private String reg_name;
	@DocBean(name ="load_strategy",value="加载策略:",dataType = String.class,required = false)
	private String load_strategy;
	@DocBean(name ="group_seq",value="分组序号:",dataType = String.class,required = false)
	private String group_seq;
	@DocBean(name ="target_tab",value="目标表名:",dataType = String.class,required = false)
	private String target_tab;
	@DocBean(name ="target_key_fields",value="目标表关键字段:",dataType = String.class,required = false)
	private String target_key_fields;
	@DocBean(name ="opposite_tab",value="比对表名:",dataType = String.class,required = false)
	private String opposite_tab;
	@DocBean(name ="opposite_key_fields",value="比对表关键字段:",dataType = String.class,required = false)
	private String opposite_key_fields;
	@DocBean(name ="range_min_val",value="范围区间的最小值:",dataType = String.class,required = false)
	private String range_min_val;
	@DocBean(name ="range_max_val",value="范围区间的最大值:",dataType = String.class,required = false)
	private String range_max_val;
	@DocBean(name ="list_vals",value="清单值域:",dataType = String.class,required = false)
	private String list_vals;
	@DocBean(name ="check_limit_condition",value="检查范围限定条件:",dataType = String.class,required = false)
	private String check_limit_condition;
	@DocBean(name ="specify_sql",value="指定SQL:",dataType = String.class,required = false)
	private String specify_sql;
	@DocBean(name ="err_data_sql",value="异常数据SQL:",dataType = String.class,required = false)
	private String err_data_sql;
	@DocBean(name ="index_desc1",value="检测指标1含义:",dataType = String.class,required = false)
	private String index_desc1;
	@DocBean(name ="index_desc2",value="检测指标2含义:",dataType = String.class,required = false)
	private String index_desc2;
	@DocBean(name ="index_desc3",value="检测指标3含义:",dataType = String.class,required = false)
	private String index_desc3;
	@DocBean(name ="flags",value="标志域(EdRuleLevel):0-警告<JingGao> 1-严重<YanZhong> ",dataType = String.class,required = false)
	private String flags;
	@DocBean(name ="remark",value="描述:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="app_updt_dt",value="更新日期:",dataType = String.class,required = true)
	private String app_updt_dt;
	@DocBean(name ="app_updt_ti",value="更新时间:",dataType = String.class,required = true)
	private String app_updt_ti;
	@DocBean(name ="rule_tag",value="规则标签:",dataType = String.class,required = false)
	private String rule_tag;
	@DocBean(name ="mail_receive",value="接收邮箱:",dataType = String.class,required = false)
	private String mail_receive;
	@DocBean(name ="rule_src",value="规则来源:",dataType = String.class,required = false)
	private String rule_src;
	@DocBean(name ="is_saveindex3",value="是否保存指标3数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_saveindex3;
	@DocBean(name ="is_saveindex1",value="是否保存指标1数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_saveindex1;
	@DocBean(name ="is_saveindex2",value="是否保存指标2数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_saveindex2;
	@DocBean(name ="case_type",value="规则类型:",dataType = String.class,required = true)
	private String case_type;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;

	/** 取得：规则编号 */
	public Long getReg_num(){
		return reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(Long reg_num){
		this.reg_num=reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(String reg_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(reg_num)){
			this.reg_num=new Long(reg_num);
		}
	}
	/** 取得：规则名称 */
	public String getReg_name(){
		return reg_name;
	}
	/** 设置：规则名称 */
	public void setReg_name(String reg_name){
		this.reg_name=reg_name;
	}
	/** 取得：加载策略 */
	public String getLoad_strategy(){
		return load_strategy;
	}
	/** 设置：加载策略 */
	public void setLoad_strategy(String load_strategy){
		this.load_strategy=load_strategy;
	}
	/** 取得：分组序号 */
	public String getGroup_seq(){
		return group_seq;
	}
	/** 设置：分组序号 */
	public void setGroup_seq(String group_seq){
		this.group_seq=group_seq;
	}
	/** 取得：目标表名 */
	public String getTarget_tab(){
		return target_tab;
	}
	/** 设置：目标表名 */
	public void setTarget_tab(String target_tab){
		this.target_tab=target_tab;
	}
	/** 取得：目标表关键字段 */
	public String getTarget_key_fields(){
		return target_key_fields;
	}
	/** 设置：目标表关键字段 */
	public void setTarget_key_fields(String target_key_fields){
		this.target_key_fields=target_key_fields;
	}
	/** 取得：比对表名 */
	public String getOpposite_tab(){
		return opposite_tab;
	}
	/** 设置：比对表名 */
	public void setOpposite_tab(String opposite_tab){
		this.opposite_tab=opposite_tab;
	}
	/** 取得：比对表关键字段 */
	public String getOpposite_key_fields(){
		return opposite_key_fields;
	}
	/** 设置：比对表关键字段 */
	public void setOpposite_key_fields(String opposite_key_fields){
		this.opposite_key_fields=opposite_key_fields;
	}
	/** 取得：范围区间的最小值 */
	public String getRange_min_val(){
		return range_min_val;
	}
	/** 设置：范围区间的最小值 */
	public void setRange_min_val(String range_min_val){
		this.range_min_val=range_min_val;
	}
	/** 取得：范围区间的最大值 */
	public String getRange_max_val(){
		return range_max_val;
	}
	/** 设置：范围区间的最大值 */
	public void setRange_max_val(String range_max_val){
		this.range_max_val=range_max_val;
	}
	/** 取得：清单值域 */
	public String getList_vals(){
		return list_vals;
	}
	/** 设置：清单值域 */
	public void setList_vals(String list_vals){
		this.list_vals=list_vals;
	}
	/** 取得：检查范围限定条件 */
	public String getCheck_limit_condition(){
		return check_limit_condition;
	}
	/** 设置：检查范围限定条件 */
	public void setCheck_limit_condition(String check_limit_condition){
		this.check_limit_condition=check_limit_condition;
	}
	/** 取得：指定SQL */
	public String getSpecify_sql(){
		return specify_sql;
	}
	/** 设置：指定SQL */
	public void setSpecify_sql(String specify_sql){
		this.specify_sql=specify_sql;
	}
	/** 取得：异常数据SQL */
	public String getErr_data_sql(){
		return err_data_sql;
	}
	/** 设置：异常数据SQL */
	public void setErr_data_sql(String err_data_sql){
		this.err_data_sql=err_data_sql;
	}
	/** 取得：检测指标1含义 */
	public String getIndex_desc1(){
		return index_desc1;
	}
	/** 设置：检测指标1含义 */
	public void setIndex_desc1(String index_desc1){
		this.index_desc1=index_desc1;
	}
	/** 取得：检测指标2含义 */
	public String getIndex_desc2(){
		return index_desc2;
	}
	/** 设置：检测指标2含义 */
	public void setIndex_desc2(String index_desc2){
		this.index_desc2=index_desc2;
	}
	/** 取得：检测指标3含义 */
	public String getIndex_desc3(){
		return index_desc3;
	}
	/** 设置：检测指标3含义 */
	public void setIndex_desc3(String index_desc3){
		this.index_desc3=index_desc3;
	}
	/** 取得：标志域 */
	public String getFlags(){
		return flags;
	}
	/** 设置：标志域 */
	public void setFlags(String flags){
		this.flags=flags;
	}
	/** 取得：描述 */
	public String getRemark(){
		return remark;
	}
	/** 设置：描述 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：更新日期 */
	public String getApp_updt_dt(){
		return app_updt_dt;
	}
	/** 设置：更新日期 */
	public void setApp_updt_dt(String app_updt_dt){
		this.app_updt_dt=app_updt_dt;
	}
	/** 取得：更新时间 */
	public String getApp_updt_ti(){
		return app_updt_ti;
	}
	/** 设置：更新时间 */
	public void setApp_updt_ti(String app_updt_ti){
		this.app_updt_ti=app_updt_ti;
	}
	/** 取得：规则标签 */
	public String getRule_tag(){
		return rule_tag;
	}
	/** 设置：规则标签 */
	public void setRule_tag(String rule_tag){
		this.rule_tag=rule_tag;
	}
	/** 取得：接收邮箱 */
	public String getMail_receive(){
		return mail_receive;
	}
	/** 设置：接收邮箱 */
	public void setMail_receive(String mail_receive){
		this.mail_receive=mail_receive;
	}
	/** 取得：规则来源 */
	public String getRule_src(){
		return rule_src;
	}
	/** 设置：规则来源 */
	public void setRule_src(String rule_src){
		this.rule_src=rule_src;
	}
	/** 取得：是否保存指标3数据 */
	public String getIs_saveindex3(){
		return is_saveindex3;
	}
	/** 设置：是否保存指标3数据 */
	public void setIs_saveindex3(String is_saveindex3){
		this.is_saveindex3=is_saveindex3;
	}
	/** 取得：是否保存指标1数据 */
	public String getIs_saveindex1(){
		return is_saveindex1;
	}
	/** 设置：是否保存指标1数据 */
	public void setIs_saveindex1(String is_saveindex1){
		this.is_saveindex1=is_saveindex1;
	}
	/** 取得：是否保存指标2数据 */
	public String getIs_saveindex2(){
		return is_saveindex2;
	}
	/** 设置：是否保存指标2数据 */
	public void setIs_saveindex2(String is_saveindex2){
		this.is_saveindex2=is_saveindex2;
	}
	/** 取得：规则类型 */
	public String getCase_type(){
		return case_type;
	}
	/** 设置：规则类型 */
	public void setCase_type(String case_type){
		this.case_type=case_type;
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
}
