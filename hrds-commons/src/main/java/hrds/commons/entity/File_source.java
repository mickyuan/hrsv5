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
 * 文件源设置
 */
@Table(tableName = "file_source")
public class File_source extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "file_source";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 文件源设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="file_source_id",value="文件源ID:",dataType = Long.class,required = true)
	private Long file_source_id;
	@DocBean(name ="file_source_path",value="文件源路径:",dataType = String.class,required = true)
	private String file_source_path;
	@DocBean(name ="is_pdf",value="PDF文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_pdf;
	@DocBean(name ="is_office",value="office文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_office;
	@DocBean(name ="is_text",value="文本文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_text;
	@DocBean(name ="is_video",value="视频文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_video;
	@DocBean(name ="is_audio",value="音频文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_audio;
	@DocBean(name ="is_image",value="图片文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_image;
	@DocBean(name ="is_other",value="其他(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_other;
	@DocBean(name ="file_remark",value="备注:",dataType = String.class,required = false)
	private String file_remark;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="is_compress",value="压缩文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_compress;
	@DocBean(name ="custom_suffix",value="自定义后缀:",dataType = String.class,required = false)
	private String custom_suffix;
	@DocBean(name ="fcs_id",value="文件系统采集ID:",dataType = Long.class,required = true)
	private Long fcs_id;

	/** 取得：文件源ID */
	public Long getFile_source_id(){
		return file_source_id;
	}
	/** 设置：文件源ID */
	public void setFile_source_id(Long file_source_id){
		this.file_source_id=file_source_id;
	}
	/** 设置：文件源ID */
	public void setFile_source_id(String file_source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_source_id)){
			this.file_source_id=new Long(file_source_id);
		}
	}
	/** 取得：文件源路径 */
	public String getFile_source_path(){
		return file_source_path;
	}
	/** 设置：文件源路径 */
	public void setFile_source_path(String file_source_path){
		this.file_source_path=file_source_path;
	}
	/** 取得：PDF文件 */
	public String getIs_pdf(){
		return is_pdf;
	}
	/** 设置：PDF文件 */
	public void setIs_pdf(String is_pdf){
		this.is_pdf=is_pdf;
	}
	/** 取得：office文件 */
	public String getIs_office(){
		return is_office;
	}
	/** 设置：office文件 */
	public void setIs_office(String is_office){
		this.is_office=is_office;
	}
	/** 取得：文本文件 */
	public String getIs_text(){
		return is_text;
	}
	/** 设置：文本文件 */
	public void setIs_text(String is_text){
		this.is_text=is_text;
	}
	/** 取得：视频文件 */
	public String getIs_video(){
		return is_video;
	}
	/** 设置：视频文件 */
	public void setIs_video(String is_video){
		this.is_video=is_video;
	}
	/** 取得：音频文件 */
	public String getIs_audio(){
		return is_audio;
	}
	/** 设置：音频文件 */
	public void setIs_audio(String is_audio){
		this.is_audio=is_audio;
	}
	/** 取得：图片文件 */
	public String getIs_image(){
		return is_image;
	}
	/** 设置：图片文件 */
	public void setIs_image(String is_image){
		this.is_image=is_image;
	}
	/** 取得：其他 */
	public String getIs_other(){
		return is_other;
	}
	/** 设置：其他 */
	public void setIs_other(String is_other){
		this.is_other=is_other;
	}
	/** 取得：备注 */
	public String getFile_remark(){
		return file_remark;
	}
	/** 设置：备注 */
	public void setFile_remark(String file_remark){
		this.file_remark=file_remark;
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：压缩文件 */
	public String getIs_compress(){
		return is_compress;
	}
	/** 设置：压缩文件 */
	public void setIs_compress(String is_compress){
		this.is_compress=is_compress;
	}
	/** 取得：自定义后缀 */
	public String getCustom_suffix(){
		return custom_suffix;
	}
	/** 设置：自定义后缀 */
	public void setCustom_suffix(String custom_suffix){
		this.custom_suffix=custom_suffix;
	}
	/** 取得：文件系统采集ID */
	public Long getFcs_id(){
		return fcs_id;
	}
	/** 设置：文件系统采集ID */
	public void setFcs_id(Long fcs_id){
		this.fcs_id=fcs_id;
	}
	/** 设置：文件系统采集ID */
	public void setFcs_id(String fcs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fcs_id)){
			this.fcs_id=new Long(fcs_id);
		}
	}
}
