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
 * 爬取数据项表
 */
@Table(tableName = "custom_data")
public class Custom_data extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "custom_data";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 爬取数据项表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cd_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cd_id; //爬取数据项表id
	private String need_data; //需提取数据项名称
	private String need_type; //需提取数据项类别
	private Long cr_id; //定制爬取要求id

	/** 取得：爬取数据项表id */
	public Long getCd_id(){
		return cd_id;
	}
	/** 设置：爬取数据项表id */
	public void setCd_id(Long cd_id){
		this.cd_id=cd_id;
	}
	/** 设置：爬取数据项表id */
	public void setCd_id(String cd_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cd_id)){
			this.cd_id=new Long(cd_id);
		}
	}
	/** 取得：需提取数据项名称 */
	public String getNeed_data(){
		return need_data;
	}
	/** 设置：需提取数据项名称 */
	public void setNeed_data(String need_data){
		this.need_data=need_data;
	}
	/** 取得：需提取数据项类别 */
	public String getNeed_type(){
		return need_type;
	}
	/** 设置：需提取数据项类别 */
	public void setNeed_type(String need_type){
		this.need_type=need_type;
	}
	/** 取得：定制爬取要求id */
	public Long getCr_id(){
		return cr_id;
	}
	/** 设置：定制爬取要求id */
	public void setCr_id(Long cr_id){
		this.cr_id=cr_id;
	}
	/** 设置：定制爬取要求id */
	public void setCr_id(String cr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cr_id)){
			this.cr_id=new Long(cr_id);
		}
	}
}
