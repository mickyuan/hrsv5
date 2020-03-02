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
 * 数据对标元管理代码类信息表
 */
@Table(tableName = "dbm_code_type_info")
public class Dbm_code_type_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_code_type_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标元管理代码类信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("code_type_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="code_type_id",value="代码类主键:",dataType = Long.class,required = true)
	private Long code_type_id;
	@DocBean(name ="code_encode",value="代码编码:",dataType = String.class,required = false)
	private String code_encode;
	@DocBean(name ="code_type_name",value="代码类名:",dataType = String.class,required = true)
	private String code_type_name;
	@DocBean(name ="code_remark",value="代码描述:",dataType = String.class,required = false)
	private String code_remark;
	@DocBean(name ="code_status",value="代码状态（是否发布）(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String code_status;
	@DocBean(name ="create_user",value="创建人:",dataType = String.class,required = true)
	private String create_user;
	@DocBean(name ="create_date",value="日期创建:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;

	/** 取得：代码类主键 */
	public Long getCode_type_id(){
		return code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(Long code_type_id){
		this.code_type_id=code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(String code_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(code_type_id)){
			this.code_type_id=new Long(code_type_id);
		}
	}
	/** 取得：代码编码 */
	public String getCode_encode(){
		return code_encode;
	}
	/** 设置：代码编码 */
	public void setCode_encode(String code_encode){
		this.code_encode=code_encode;
	}
	/** 取得：代码类名 */
	public String getCode_type_name(){
		return code_type_name;
	}
	/** 设置：代码类名 */
	public void setCode_type_name(String code_type_name){
		this.code_type_name=code_type_name;
	}
	/** 取得：代码描述 */
	public String getCode_remark(){
		return code_remark;
	}
	/** 设置：代码描述 */
	public void setCode_remark(String code_remark){
		this.code_remark=code_remark;
	}
	/** 取得：代码状态（是否发布） */
	public String getCode_status(){
		return code_status;
	}
	/** 设置：代码状态（是否发布） */
	public void setCode_status(String code_status){
		this.code_status=code_status;
	}
	/** 取得：创建人 */
	public String getCreate_user(){
		return create_user;
	}
	/** 设置：创建人 */
	public void setCreate_user(String create_user){
		this.create_user=create_user;
	}
	/** 取得：日期创建 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：日期创建 */
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
}
