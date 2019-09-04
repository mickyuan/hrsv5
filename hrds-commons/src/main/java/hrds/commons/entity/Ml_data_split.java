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
 * 机器学习数据拆分表
 */
@Table(tableName = "ml_data_split")
public class Ml_data_split extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_data_split";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据拆分表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datasplit_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long datasplit_id; //数据拆分编号
	private String dsplit_extrtype; //数据拆分抽取类型
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注

	/** 取得：数据拆分编号 */
	public Long getDatasplit_id(){
		return datasplit_id;
	}
	/** 设置：数据拆分编号 */
	public void setDatasplit_id(Long datasplit_id){
		this.datasplit_id=datasplit_id;
	}
	/** 设置：数据拆分编号 */
	public void setDatasplit_id(String datasplit_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datasplit_id)){
			this.datasplit_id=new Long(datasplit_id);
		}
	}
	/** 取得：数据拆分抽取类型 */
	public String getDsplit_extrtype(){
		return dsplit_extrtype;
	}
	/** 设置：数据拆分抽取类型 */
	public void setDsplit_extrtype(String dsplit_extrtype){
		this.dsplit_extrtype=dsplit_extrtype;
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
}
