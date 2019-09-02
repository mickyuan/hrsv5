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
 * 进库信息统计表
 */
@Table(tableName = "num_info")
public class Num_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "num_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 进库信息统计表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("icm_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long icm_id; //信息id
	private String hbase_name; //Hbase表名
	private Long increase_num; //增加的条数
	private Long decrease_num; //删除的条数
	private String exec_date; //日期
	private String maintype; //储存方式

	/** 取得：信息id */
	public Long getIcm_id(){
		return icm_id;
	}
	/** 设置：信息id */
	public void setIcm_id(Long icm_id){
		this.icm_id=icm_id;
	}
	/** 设置：信息id */
	public void setIcm_id(String icm_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(icm_id)){
			this.icm_id=new Long(icm_id);
		}
	}
	/** 取得：Hbase表名 */
	public String getHbase_name(){
		return hbase_name;
	}
	/** 设置：Hbase表名 */
	public void setHbase_name(String hbase_name){
		this.hbase_name=hbase_name;
	}
	/** 取得：增加的条数 */
	public Long getIncrease_num(){
		return increase_num;
	}
	/** 设置：增加的条数 */
	public void setIncrease_num(Long increase_num){
		this.increase_num=increase_num;
	}
	/** 设置：增加的条数 */
	public void setIncrease_num(String increase_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(increase_num)){
			this.increase_num=new Long(increase_num);
		}
	}
	/** 取得：删除的条数 */
	public Long getDecrease_num(){
		return decrease_num;
	}
	/** 设置：删除的条数 */
	public void setDecrease_num(Long decrease_num){
		this.decrease_num=decrease_num;
	}
	/** 设置：删除的条数 */
	public void setDecrease_num(String decrease_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(decrease_num)){
			this.decrease_num=new Long(decrease_num);
		}
	}
	/** 取得：日期 */
	public String getExec_date(){
		return exec_date;
	}
	/** 设置：日期 */
	public void setExec_date(String exec_date){
		this.exec_date=exec_date;
	}
	/** 取得：储存方式 */
	public String getMaintype(){
		return maintype;
	}
	/** 设置：储存方式 */
	public void setMaintype(String maintype){
		this.maintype=maintype;
	}
}
