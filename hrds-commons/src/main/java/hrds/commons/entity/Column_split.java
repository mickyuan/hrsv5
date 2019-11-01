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
 * 列拆分信息表
 */
@Table(tableName = "column_split")
public class Column_split extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_split";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 列拆分信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("col_split_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="col_split_id",value="字段编号:",dataType = Long.class,required = true)
	private Long col_split_id;
	@DocBean(name ="col_name",value="字段名称:",dataType = String.class,required = true)
	private String col_name;
	@DocBean(name ="col_offset",value="字段偏移量:",dataType = String.class,required = false)
	private String col_offset;
	@DocBean(name ="col_zhname",value="中文名称:",dataType = String.class,required = false)
	private String col_zhname;
	@DocBean(name ="col_type",value="字段类型:",dataType = String.class,required = true)
	private String col_type;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="col_clean_id",value="清洗参数编号:",dataType = Long.class,required = true)
	private Long col_clean_id;
	@DocBean(name ="column_id",value="字段ID:",dataType = Long.class,required = true)
	private Long column_id;
	@DocBean(name ="valid_s_date",value="有效开始日期:",dataType = String.class,required = true)
	private String valid_s_date;
	@DocBean(name ="valid_e_date",value="有效结束日期:",dataType = String.class,required = true)
	private String valid_e_date;
	@DocBean(name ="seq",value="拆分对应序号:",dataType = Long.class,required = false)
	private Long seq;
	@DocBean(name ="split_sep",value="拆分分隔符:",dataType = String.class,required = false)
	private String split_sep;
	@DocBean(name ="split_type",value="拆分方式(CharSplitType):1-偏移量<PianYiLiang> 2-自定符号<ZhiDingFuHao> ",dataType = String.class,required = true)
	private String split_type;

	/** 取得：字段编号 */
	public Long getCol_split_id(){
		return col_split_id;
	}
	/** 设置：字段编号 */
	public void setCol_split_id(Long col_split_id){
		this.col_split_id=col_split_id;
	}
	/** 设置：字段编号 */
	public void setCol_split_id(String col_split_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_split_id)){
			this.col_split_id=new Long(col_split_id);
		}
	}
	/** 取得：字段名称 */
	public String getCol_name(){
		return col_name;
	}
	/** 设置：字段名称 */
	public void setCol_name(String col_name){
		this.col_name=col_name;
	}
	/** 取得：字段偏移量 */
	public String getCol_offset(){
		return col_offset;
	}
	/** 设置：字段偏移量 */
	public void setCol_offset(String col_offset){
		this.col_offset=col_offset;
	}
	/** 取得：中文名称 */
	public String getCol_zhname(){
		return col_zhname;
	}
	/** 设置：中文名称 */
	public void setCol_zhname(String col_zhname){
		this.col_zhname=col_zhname;
	}
	/** 取得：字段类型 */
	public String getCol_type(){
		return col_type;
	}
	/** 设置：字段类型 */
	public void setCol_type(String col_type){
		this.col_type=col_type;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：清洗参数编号 */
	public Long getCol_clean_id(){
		return col_clean_id;
	}
	/** 设置：清洗参数编号 */
	public void setCol_clean_id(Long col_clean_id){
		this.col_clean_id=col_clean_id;
	}
	/** 设置：清洗参数编号 */
	public void setCol_clean_id(String col_clean_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_clean_id)){
			this.col_clean_id=new Long(col_clean_id);
		}
	}
	/** 取得：字段ID */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：有效开始日期 */
	public String getValid_s_date(){
		return valid_s_date;
	}
	/** 设置：有效开始日期 */
	public void setValid_s_date(String valid_s_date){
		this.valid_s_date=valid_s_date;
	}
	/** 取得：有效结束日期 */
	public String getValid_e_date(){
		return valid_e_date;
	}
	/** 设置：有效结束日期 */
	public void setValid_e_date(String valid_e_date){
		this.valid_e_date=valid_e_date;
	}
	/** 取得：拆分对应序号 */
	public Long getSeq(){
		return seq;
	}
	/** 设置：拆分对应序号 */
	public void setSeq(Long seq){
		this.seq=seq;
	}
	/** 设置：拆分对应序号 */
	public void setSeq(String seq){
		if(!fd.ng.core.utils.StringUtil.isEmpty(seq)){
			this.seq=new Long(seq);
		}
	}
	/** 取得：拆分分隔符 */
	public String getSplit_sep(){
		return split_sep;
	}
	/** 设置：拆分分隔符 */
	public void setSplit_sep(String split_sep){
		this.split_sep=split_sep;
	}
	/** 取得：拆分方式 */
	public String getSplit_type(){
		return split_type;
	}
	/** 设置：拆分方式 */
	public void setSplit_type(String split_type){
		this.split_type=split_type;
	}
}
