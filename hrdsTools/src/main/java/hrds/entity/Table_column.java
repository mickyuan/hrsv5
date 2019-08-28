package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 表对应的字段
 */
@Table(tableName = "table_column")
public class Table_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表对应的字段 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long column_id; //字段ID
	private String is_primary_key; //是否为主键
	private String colume_name; //列名
	private String colume_ch_name; //列中文名称
	private String valid_s_date; //有效开始日期
	private String valid_e_date; //有效结束日期
	private String is_get; //是否采集
	private String column_type; //列字段类型
	private Long table_id; //表名ID
	private String is_solr; //是否solr索引
	private String remark; //备注
	private String is_alive; //是否保留原字段
	private String is_new; //是否为变化生成
	private String tc_or; //清洗顺序
	private String is_pre; //是否carbondata聚合列
	private String is_sortcolumns; //是否为carbondata的排序列

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
	/** 取得：是否为主键 */
	public String getIs_primary_key(){
		return is_primary_key;
	}
	/** 设置：是否为主键 */
	public void setIs_primary_key(String is_primary_key){
		this.is_primary_key=is_primary_key;
	}
	/** 取得：列名 */
	public String getColume_name(){
		return colume_name;
	}
	/** 设置：列名 */
	public void setColume_name(String colume_name){
		this.colume_name=colume_name;
	}
	/** 取得：列中文名称 */
	public String getColume_ch_name(){
		return colume_ch_name;
	}
	/** 设置：列中文名称 */
	public void setColume_ch_name(String colume_ch_name){
		this.colume_ch_name=colume_ch_name;
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
	/** 取得：是否采集 */
	public String getIs_get(){
		return is_get;
	}
	/** 设置：是否采集 */
	public void setIs_get(String is_get){
		this.is_get=is_get;
	}
	/** 取得：列字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：列字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：表名ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
	/** 取得：是否solr索引 */
	public String getIs_solr(){
		return is_solr;
	}
	/** 设置：是否solr索引 */
	public void setIs_solr(String is_solr){
		this.is_solr=is_solr;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：是否保留原字段 */
	public String getIs_alive(){
		return is_alive;
	}
	/** 设置：是否保留原字段 */
	public void setIs_alive(String is_alive){
		this.is_alive=is_alive;
	}
	/** 取得：是否为变化生成 */
	public String getIs_new(){
		return is_new;
	}
	/** 设置：是否为变化生成 */
	public void setIs_new(String is_new){
		this.is_new=is_new;
	}
	/** 取得：清洗顺序 */
	public String getTc_or(){
		return tc_or;
	}
	/** 设置：清洗顺序 */
	public void setTc_or(String tc_or){
		this.tc_or=tc_or;
	}
	/** 取得：是否carbondata聚合列 */
	public String getIs_pre(){
		return is_pre;
	}
	/** 设置：是否carbondata聚合列 */
	public void setIs_pre(String is_pre){
		this.is_pre=is_pre;
	}
	/** 取得：是否为carbondata的排序列 */
	public String getIs_sortcolumns(){
		return is_sortcolumns;
	}
	/** 设置：是否为carbondata的排序列 */
	public void setIs_sortcolumns(String is_sortcolumns){
		this.is_sortcolumns=is_sortcolumns;
	}
}
