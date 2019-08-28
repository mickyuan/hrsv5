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
 * 机器学习降维字段表
 */
@Table(tableName = "ml_dreduction_column")
public class Ml_dreduction_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dreduction_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习降维字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("drcolumn_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long drcolumn_id; //降维字段编号
	private String dimer_column; //降维字段
	private Long dimeredu_id; //降维编号

	/** 取得：降维字段编号 */
	public Long getDrcolumn_id(){
		return drcolumn_id;
	}
	/** 设置：降维字段编号 */
	public void setDrcolumn_id(Long drcolumn_id){
		this.drcolumn_id=drcolumn_id;
	}
	/** 设置：降维字段编号 */
	public void setDrcolumn_id(String drcolumn_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(drcolumn_id)){
			this.drcolumn_id=new Long(drcolumn_id);
		}
	}
	/** 取得：降维字段 */
	public String getDimer_column(){
		return dimer_column;
	}
	/** 设置：降维字段 */
	public void setDimer_column(String dimer_column){
		this.dimer_column=dimer_column;
	}
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
}
