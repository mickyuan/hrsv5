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
 * 仓库脚本算法
 */
@Table(tableName = "edw_job_alg")
public class Edw_job_alg extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_job_alg";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 仓库脚本算法 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("algorithmcode");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String algorithmcode; //算法编号
	private String algorithmname; //算法名称

	/** 取得：算法编号 */
	public String getAlgorithmcode(){
		return algorithmcode;
	}
	/** 设置：算法编号 */
	public void setAlgorithmcode(String algorithmcode){
		this.algorithmcode=algorithmcode;
	}
	/** 取得：算法名称 */
	public String getAlgorithmname(){
		return algorithmname;
	}
	/** 设置：算法名称 */
	public void setAlgorithmname(String algorithmname){
		this.algorithmname=algorithmname;
	}
}
