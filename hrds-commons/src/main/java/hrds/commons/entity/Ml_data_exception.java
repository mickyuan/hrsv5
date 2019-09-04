package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据预处理数据异常值处理表
 */
@Table(tableName = "ml_data_exception")
public class Ml_data_exception extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_data_exception";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据预处理数据异常值处理表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dataexce_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long dataexce_id; //数据异常处理编号
	private String dataexce_column; //数据异常值处理字段
	private BigDecimal max_condition; //最高条件值
	private String process_mode; //处理方式
	private BigDecimal min_condition; //最低条件值
	private Long dtable_info_id; //数据表信息编号
	private String identify_cond; //标识条件
	private String replacement; //替换方式
	private String newcolumn_name; //新字段名称
	private BigDecimal replace_percent; //替换百分数
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private Long fixed_quantity; //固定数量
	private BigDecimal standard_devi; //标准差

	/** 取得：数据异常处理编号 */
	public Long getDataexce_id(){
		return dataexce_id;
	}
	/** 设置：数据异常处理编号 */
	public void setDataexce_id(Long dataexce_id){
		this.dataexce_id=dataexce_id;
	}
	/** 设置：数据异常处理编号 */
	public void setDataexce_id(String dataexce_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dataexce_id)){
			this.dataexce_id=new Long(dataexce_id);
		}
	}
	/** 取得：数据异常值处理字段 */
	public String getDataexce_column(){
		return dataexce_column;
	}
	/** 设置：数据异常值处理字段 */
	public void setDataexce_column(String dataexce_column){
		this.dataexce_column=dataexce_column;
	}
	/** 取得：最高条件值 */
	public BigDecimal getMax_condition(){
		return max_condition;
	}
	/** 设置：最高条件值 */
	public void setMax_condition(BigDecimal max_condition){
		this.max_condition=max_condition;
	}
	/** 设置：最高条件值 */
	public void setMax_condition(String max_condition){
		if(!fd.ng.core.utils.StringUtil.isEmpty(max_condition)){
			this.max_condition=new BigDecimal(max_condition);
		}
	}
	/** 取得：处理方式 */
	public String getProcess_mode(){
		return process_mode;
	}
	/** 设置：处理方式 */
	public void setProcess_mode(String process_mode){
		this.process_mode=process_mode;
	}
	/** 取得：最低条件值 */
	public BigDecimal getMin_condition(){
		return min_condition;
	}
	/** 设置：最低条件值 */
	public void setMin_condition(BigDecimal min_condition){
		this.min_condition=min_condition;
	}
	/** 设置：最低条件值 */
	public void setMin_condition(String min_condition){
		if(!fd.ng.core.utils.StringUtil.isEmpty(min_condition)){
			this.min_condition=new BigDecimal(min_condition);
		}
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
	/** 取得：标识条件 */
	public String getIdentify_cond(){
		return identify_cond;
	}
	/** 设置：标识条件 */
	public void setIdentify_cond(String identify_cond){
		this.identify_cond=identify_cond;
	}
	/** 取得：替换方式 */
	public String getReplacement(){
		return replacement;
	}
	/** 设置：替换方式 */
	public void setReplacement(String replacement){
		this.replacement=replacement;
	}
	/** 取得：新字段名称 */
	public String getNewcolumn_name(){
		return newcolumn_name;
	}
	/** 设置：新字段名称 */
	public void setNewcolumn_name(String newcolumn_name){
		this.newcolumn_name=newcolumn_name;
	}
	/** 取得：替换百分数 */
	public BigDecimal getReplace_percent(){
		return replace_percent;
	}
	/** 设置：替换百分数 */
	public void setReplace_percent(BigDecimal replace_percent){
		this.replace_percent=replace_percent;
	}
	/** 设置：替换百分数 */
	public void setReplace_percent(String replace_percent){
		if(!fd.ng.core.utils.StringUtil.isEmpty(replace_percent)){
			this.replace_percent=new BigDecimal(replace_percent);
		}
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：固定数量 */
	public Long getFixed_quantity(){
		return fixed_quantity;
	}
	/** 设置：固定数量 */
	public void setFixed_quantity(Long fixed_quantity){
		this.fixed_quantity=fixed_quantity;
	}
	/** 设置：固定数量 */
	public void setFixed_quantity(String fixed_quantity){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fixed_quantity)){
			this.fixed_quantity=new Long(fixed_quantity);
		}
	}
	/** 取得：标准差 */
	public BigDecimal getStandard_devi(){
		return standard_devi;
	}
	/** 设置：标准差 */
	public void setStandard_devi(BigDecimal standard_devi){
		this.standard_devi=standard_devi;
	}
	/** 设置：标准差 */
	public void setStandard_devi(String standard_devi){
		if(!fd.ng.core.utils.StringUtil.isEmpty(standard_devi)){
			this.standard_devi=new BigDecimal(standard_devi);
		}
	}
}
