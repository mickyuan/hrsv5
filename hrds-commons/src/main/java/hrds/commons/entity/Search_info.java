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
 * 全文检索排序表
 */
@Table(tableName = "search_info")
public class Search_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "search_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 全文检索排序表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("si_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="si_id",value="si_id:",dataType = Long.class,required = true)
	private Long si_id;
	@DocBean(name ="word_name",value="关键字:",dataType = String.class,required = true)
	private String word_name;
	@DocBean(name ="si_count",value="点击量:",dataType = Long.class,required = true)
	private Long si_count;
	@DocBean(name ="si_remark",value="备注:",dataType = String.class,required = false)
	private String si_remark;
	@DocBean(name ="file_id",value="文件编号:",dataType = String.class,required = true)
	private String file_id;

	/** 取得：si_id */
	public Long getSi_id(){
		return si_id;
	}
	/** 设置：si_id */
	public void setSi_id(Long si_id){
		this.si_id=si_id;
	}
	/** 设置：si_id */
	public void setSi_id(String si_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(si_id)){
			this.si_id=new Long(si_id);
		}
	}
	/** 取得：关键字 */
	public String getWord_name(){
		return word_name;
	}
	/** 设置：关键字 */
	public void setWord_name(String word_name){
		this.word_name=word_name;
	}
	/** 取得：点击量 */
	public Long getSi_count(){
		return si_count;
	}
	/** 设置：点击量 */
	public void setSi_count(Long si_count){
		this.si_count=si_count;
	}
	/** 设置：点击量 */
	public void setSi_count(String si_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(si_count)){
			this.si_count=new Long(si_count);
		}
	}
	/** 取得：备注 */
	public String getSi_remark(){
		return si_remark;
	}
	/** 设置：备注 */
	public void setSi_remark(String si_remark){
		this.si_remark=si_remark;
	}
	/** 取得：文件编号 */
	public String getFile_id(){
		return file_id;
	}
	/** 设置：文件编号 */
	public void setFile_id(String file_id){
		this.file_id=file_id;
	}
}
