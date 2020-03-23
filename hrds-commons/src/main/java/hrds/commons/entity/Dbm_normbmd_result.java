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
 * 数据对标标准对标检测结果表
 */
@Table(tableName = "dbm_normbmd_result")
public class Dbm_normbmd_result extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_normbmd_result";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标标准对标检测结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("result_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="result_id",value="结果主键:",dataType = String.class,required = true)
	private String result_id;
	@DocBean(name ="col_similarity",value="字段相似度:",dataType = BigDecimal.class,required = true)
	private BigDecimal col_similarity;
	@DocBean(name ="remark_similarity",value="描述相似度:",dataType = BigDecimal.class,required = true)
	private BigDecimal remark_similarity;
	@DocBean(name ="col_id",value="字段主键:",dataType = Long.class,required = true)
	private Long col_id;
	@DocBean(name ="basic_id",value="标准元主键:",dataType = Long.class,required = true)
	private Long basic_id;
	@DocBean(name ="is_artificial",value="是否人工(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_artificial;
	@DocBean(name ="detect_id",value="检测主键:",dataType = String.class,required = true)
	private String detect_id;
	@DocBean(name ="is_tag",value="是否标记为最终结果(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_tag;

	/** 取得：结果主键 */
	public String getResult_id(){
		return result_id;
	}
	/** 设置：结果主键 */
	public void setResult_id(String result_id){
		this.result_id=result_id;
	}
	/** 取得：字段相似度 */
	public BigDecimal getCol_similarity(){
		return col_similarity;
	}
	/** 设置：字段相似度 */
	public void setCol_similarity(BigDecimal col_similarity){
		this.col_similarity=col_similarity;
	}
	/** 设置：字段相似度 */
	public void setCol_similarity(String col_similarity){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_similarity)){
			this.col_similarity=new BigDecimal(col_similarity);
		}
	}
	/** 取得：描述相似度 */
	public BigDecimal getRemark_similarity(){
		return remark_similarity;
	}
	/** 设置：描述相似度 */
	public void setRemark_similarity(BigDecimal remark_similarity){
		this.remark_similarity=remark_similarity;
	}
	/** 设置：描述相似度 */
	public void setRemark_similarity(String remark_similarity){
		if(!fd.ng.core.utils.StringUtil.isEmpty(remark_similarity)){
			this.remark_similarity=new BigDecimal(remark_similarity);
		}
	}
	/** 取得：字段主键 */
	public Long getCol_id(){
		return col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(Long col_id){
		this.col_id=col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(String col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_id)){
			this.col_id=new Long(col_id);
		}
	}
	/** 取得：标准元主键 */
	public Long getBasic_id(){
		return basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(Long basic_id){
		this.basic_id=basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(String basic_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(basic_id)){
			this.basic_id=new Long(basic_id);
		}
	}
	/** 取得：是否人工 */
	public String getIs_artificial(){
		return is_artificial;
	}
	/** 设置：是否人工 */
	public void setIs_artificial(String is_artificial){
		this.is_artificial=is_artificial;
	}
	/** 取得：检测主键 */
	public String getDetect_id(){
		return detect_id;
	}
	/** 设置：检测主键 */
	public void setDetect_id(String detect_id){
		this.detect_id=detect_id;
	}
	/** 取得：是否标记为最终结果 */
	public String getIs_tag(){
		return is_tag;
	}
	/** 设置：是否标记为最终结果 */
	public void setIs_tag(String is_tag){
		this.is_tag=is_tag;
	}
}
