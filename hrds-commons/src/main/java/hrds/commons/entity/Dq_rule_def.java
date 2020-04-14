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
 * 数据质量规则类型定义表
 */
@Table(tableName = "dq_rule_def")
public class Dq_rule_def extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_rule_def";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据质量规则类型定义表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("case_type");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="case_type",value="规则类型:",dataType = String.class,required = true)
	private String case_type;
	@DocBean(name ="case_type_desc",value="规则类型描述:",dataType = String.class,required = false)
	private String case_type_desc;
	@DocBean(name ="index_desc1",value="检测指标1含义:",dataType = String.class,required = false)
	private String index_desc1;
	@DocBean(name ="index_desc2",value="检测指标2含义:",dataType = String.class,required = false)
	private String index_desc2;
	@DocBean(name ="index_desc3",value="检测指标3含义:",dataType = String.class,required = false)
	private String index_desc3;
	@DocBean(name ="remark",value="说明:",dataType = String.class,required = false)
	private String remark;

	/** 取得：规则类型 */
	public String getCase_type(){
		return case_type;
	}
	/** 设置：规则类型 */
	public void setCase_type(String case_type){
		this.case_type=case_type;
	}
	/** 取得：规则类型描述 */
	public String getCase_type_desc(){
		return case_type_desc;
	}
	/** 设置：规则类型描述 */
	public void setCase_type_desc(String case_type_desc){
		this.case_type_desc=case_type_desc;
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
	/** 取得：说明 */
	public String getRemark(){
		return remark;
	}
	/** 设置：说明 */
	public void setRemark(String remark){
		this.remark=remark;
	}
}
