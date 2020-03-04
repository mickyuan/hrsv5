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
 * 数据对标元管理标准元表
 */
@Table(tableName = "dbm_normbasic")
public class Dbm_normbasic extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_normbasic";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标元管理标准元表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("basic_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="basic_id",value="标准元主键:",dataType = Long.class,required = true)
	private Long basic_id;
	@DocBean(name ="norm_code",value="标准编号:",dataType = String.class,required = false)
	private String norm_code;
	@DocBean(name ="norm_cname",value="标准中文名称:",dataType = String.class,required = true)
	private String norm_cname;
	@DocBean(name ="norm_ename",value="标准英文名称:",dataType = String.class,required = true)
	private String norm_ename;
	@DocBean(name ="norm_aname",value="标准别名:",dataType = String.class,required = false)
	private String norm_aname;
	@DocBean(name ="col_len",value="字段长度:",dataType = Long.class,required = true)
	private Long col_len;
	@DocBean(name ="decimal_point",value="小数长度:",dataType = Long.class,required = false)
	private Long decimal_point;
	@DocBean(name ="business_def",value="业务定义:",dataType = String.class,required = false)
	private String business_def;
	@DocBean(name ="business_rule",value="业务规则:",dataType = String.class,required = false)
	private String business_rule;
	@DocBean(name ="norm_basis",value="标准依据:",dataType = String.class,required = false)
	private String norm_basis;
	@DocBean(name ="manage_department",value="标准管理部门:",dataType = String.class,required = false)
	private String manage_department;
	@DocBean(name ="relevant_department",value="标准相关部门:",dataType = String.class,required = false)
	private String relevant_department;
	@DocBean(name ="origin_system",value="可信系统（数据源）:",dataType = String.class,required = false)
	private String origin_system;
	@DocBean(name ="related_system",value="相关标准:",dataType = String.class,required = false)
	private String related_system;
	@DocBean(name ="formulator",value="制定人:",dataType = String.class,required = true)
	private String formulator;
	@DocBean(name ="norm_status",value="标准元状态（是否已发布）(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String norm_status;
	@DocBean(name ="create_user",value="创建人:",dataType = String.class,required = true)
	private String create_user;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="sort_id",value="分类主键:",dataType = Long.class,required = true)
	private Long sort_id;
	@DocBean(name ="code_type_id",value="代码类主键:",dataType = Long.class,required = false)
	private Long code_type_id;
	@DocBean(name ="dbm_domain",value="值域:",dataType = String.class,required = false)
	private String dbm_domain;
	@DocBean(name ="data_type",value="数据类别(DbmDataType):101-编码类<BianMaLei> 102-标识类<BiaoShiLei> 103-代码类<DaiMaLei> 104-金额类<JinELei> 105-日期类<RiQiLei> 106-日期时间类<RiQiShiJianLei> 107-时间类<ShiJianLei> 108-数值类<ShuZhiLei> 109-文本类<WenBenLei> ",dataType = String.class,required = false)
	private String data_type;

	/** 取得：标准元主键 */
	public Long getBasic_id(){
		return basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(Long basic_id){
		this.basic_id=basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(String basic_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(basic_id)){
			this.basic_id=new Long(basic_id);
		}
	}
	/** 取得：标准编号 */
	public String getNorm_code(){
		return norm_code;
	}
	/** 设置：标准编号 */
	public void setNorm_code(String norm_code){
		this.norm_code=norm_code;
	}
	/** 取得：标准中文名称 */
	public String getNorm_cname(){
		return norm_cname;
	}
	/** 设置：标准中文名称 */
	public void setNorm_cname(String norm_cname){
		this.norm_cname=norm_cname;
	}
	/** 取得：标准英文名称 */
	public String getNorm_ename(){
		return norm_ename;
	}
	/** 设置：标准英文名称 */
	public void setNorm_ename(String norm_ename){
		this.norm_ename=norm_ename;
	}
	/** 取得：标准别名 */
	public String getNorm_aname(){
		return norm_aname;
	}
	/** 设置：标准别名 */
	public void setNorm_aname(String norm_aname){
		this.norm_aname=norm_aname;
	}
	/** 取得：字段长度 */
	public Long getCol_len(){
		return col_len;
	}
	/** 设置：字段长度 */
	public void setCol_len(Long col_len){
		this.col_len=col_len;
	}
	/** 设置：字段长度 */
	public void setCol_len(String col_len){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_len)){
			this.col_len=new Long(col_len);
		}
	}
	/** 取得：小数长度 */
	public Long getDecimal_point(){
		return decimal_point;
	}
	/** 设置：小数长度 */
	public void setDecimal_point(Long decimal_point){
		this.decimal_point=decimal_point;
	}
	/** 设置：小数长度 */
	public void setDecimal_point(String decimal_point){
		if(!fd.ng.core.utils.StringUtil.isEmpty(decimal_point)){
			this.decimal_point=new Long(decimal_point);
		}
	}
	/** 取得：业务定义 */
	public String getBusiness_def(){
		return business_def;
	}
	/** 设置：业务定义 */
	public void setBusiness_def(String business_def){
		this.business_def=business_def;
	}
	/** 取得：业务规则 */
	public String getBusiness_rule(){
		return business_rule;
	}
	/** 设置：业务规则 */
	public void setBusiness_rule(String business_rule){
		this.business_rule=business_rule;
	}
	/** 取得：标准依据 */
	public String getNorm_basis(){
		return norm_basis;
	}
	/** 设置：标准依据 */
	public void setNorm_basis(String norm_basis){
		this.norm_basis=norm_basis;
	}
	/** 取得：标准管理部门 */
	public String getManage_department(){
		return manage_department;
	}
	/** 设置：标准管理部门 */
	public void setManage_department(String manage_department){
		this.manage_department=manage_department;
	}
	/** 取得：标准相关部门 */
	public String getRelevant_department(){
		return relevant_department;
	}
	/** 设置：标准相关部门 */
	public void setRelevant_department(String relevant_department){
		this.relevant_department=relevant_department;
	}
	/** 取得：可信系统（数据源） */
	public String getOrigin_system(){
		return origin_system;
	}
	/** 设置：可信系统（数据源） */
	public void setOrigin_system(String origin_system){
		this.origin_system=origin_system;
	}
	/** 取得：相关标准 */
	public String getRelated_system(){
		return related_system;
	}
	/** 设置：相关标准 */
	public void setRelated_system(String related_system){
		this.related_system=related_system;
	}
	/** 取得：制定人 */
	public String getFormulator(){
		return formulator;
	}
	/** 设置：制定人 */
	public void setFormulator(String formulator){
		this.formulator=formulator;
	}
	/** 取得：标准元状态（是否已发布） */
	public String getNorm_status(){
		return norm_status;
	}
	/** 设置：标准元状态（是否已发布） */
	public void setNorm_status(String norm_status){
		this.norm_status=norm_status;
	}
	/** 取得：创建人 */
	public String getCreate_user(){
		return create_user;
	}
	/** 设置：创建人 */
	public void setCreate_user(String create_user){
		this.create_user=create_user;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：分类主键 */
	public Long getSort_id(){
		return sort_id;
	}
	/** 设置：分类主键 */
	public void setSort_id(Long sort_id){
		this.sort_id=sort_id;
	}
	/** 设置：分类主键 */
	public void setSort_id(String sort_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sort_id)){
			this.sort_id=new Long(sort_id);
		}
	}
	/** 取得：代码类主键 */
	public Long getCode_type_id(){
		return code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(Long code_type_id){
		this.code_type_id=code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(String code_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(code_type_id)){
			this.code_type_id=new Long(code_type_id);
		}
	}
	/** 取得：值域 */
	public String getDbm_domain(){
		return dbm_domain;
	}
	/** 设置：值域 */
	public void setDbm_domain(String dbm_domain){
		this.dbm_domain=dbm_domain;
	}
	/** 取得：数据类别 */
	public String getData_type(){
		return data_type;
	}
	/** 设置：数据类别 */
	public void setData_type(String data_type){
		this.data_type=data_type;
	}
}
