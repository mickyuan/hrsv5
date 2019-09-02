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
 * 机器学习降维表
 */
@Table(tableName = "ml_dreduction")
public class Ml_dreduction extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dreduction";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习降维表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dimeredu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long dimeredu_id; //降维编号
	private String kbtest_is_flag; //KMOBartlets检验
	private String serep_is_flag; //碎石图
	private String dimer_method; //降维分析方法
	private Long extrfactor_num; //提取因子数
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注

	/** 取得：降维编号 */
	public Long getDimeredu_id(){
		return dimeredu_id;
	}
	/** 设置：降维编号 */
	public void setDimeredu_id(Long dimeredu_id){
		this.dimeredu_id=dimeredu_id;
	}
	/** 设置：降维编号 */
	public void setDimeredu_id(String dimeredu_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dimeredu_id)){
			this.dimeredu_id=new Long(dimeredu_id);
		}
	}
	/** 取得：KMOBartlets检验 */
	public String getKbtest_is_flag(){
		return kbtest_is_flag;
	}
	/** 设置：KMOBartlets检验 */
	public void setKbtest_is_flag(String kbtest_is_flag){
		this.kbtest_is_flag=kbtest_is_flag;
	}
	/** 取得：碎石图 */
	public String getSerep_is_flag(){
		return serep_is_flag;
	}
	/** 设置：碎石图 */
	public void setSerep_is_flag(String serep_is_flag){
		this.serep_is_flag=serep_is_flag;
	}
	/** 取得：降维分析方法 */
	public String getDimer_method(){
		return dimer_method;
	}
	/** 设置：降维分析方法 */
	public void setDimer_method(String dimer_method){
		this.dimer_method=dimer_method;
	}
	/** 取得：提取因子数 */
	public Long getExtrfactor_num(){
		return extrfactor_num;
	}
	/** 设置：提取因子数 */
	public void setExtrfactor_num(Long extrfactor_num){
		this.extrfactor_num=extrfactor_num;
	}
	/** 设置：提取因子数 */
	public void setExtrfactor_num(String extrfactor_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(extrfactor_num)){
			this.extrfactor_num=new Long(extrfactor_num);
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
