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
 * 流数据管理消费目的地管理
 */
@Table(tableName = "sdm_consume_des")
public class Sdm_consume_des extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_consume_des";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理消费目的地管理 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_des_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_des_id; //配置id
	private String sdm_thr_partition; //消费线程与分区的关系
	private String sdm_cons_des; //消费端目的地
	private String remark; //备注
	private Integer thread_num; //线程数
	private String partition; //分区
	private String sdm_bus_pro_cla; //业务处理类
	private Long sdm_consum_id; //消费端配置id
	private String sdm_conf_describe; //海云外部流数据管理消费端目的地
	private String hyren_consumedes; //海云内部消费目的地
	private String hdfs_file_type; //hdfs文件类型
	private String des_class; //目的地业务处理类
	private String external_file_type; //外部文件类型
	private String cus_des_type; //自定义业务类类型
	private String descustom_buscla; //目的地业务类类型

	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：消费线程与分区的关系 */
	public String getSdm_thr_partition(){
		return sdm_thr_partition;
	}
	/** 设置：消费线程与分区的关系 */
	public void setSdm_thr_partition(String sdm_thr_partition){
		this.sdm_thr_partition=sdm_thr_partition;
	}
	/** 取得：消费端目的地 */
	public String getSdm_cons_des(){
		return sdm_cons_des;
	}
	/** 设置：消费端目的地 */
	public void setSdm_cons_des(String sdm_cons_des){
		this.sdm_cons_des=sdm_cons_des;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：线程数 */
	public Integer getThread_num(){
		return thread_num;
	}
	/** 设置：线程数 */
	public void setThread_num(Integer thread_num){
		this.thread_num=thread_num;
	}
	/** 设置：线程数 */
	public void setThread_num(String thread_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(thread_num)){
			this.thread_num=new Integer(thread_num);
		}
	}
	/** 取得：分区 */
	public String getPartition(){
		return partition;
	}
	/** 设置：分区 */
	public void setPartition(String partition){
		this.partition=partition;
	}
	/** 取得：业务处理类 */
	public String getSdm_bus_pro_cla(){
		return sdm_bus_pro_cla;
	}
	/** 设置：业务处理类 */
	public void setSdm_bus_pro_cla(String sdm_bus_pro_cla){
		this.sdm_bus_pro_cla=sdm_bus_pro_cla;
	}
	/** 取得：消费端配置id */
	public Long getSdm_consum_id(){
		return sdm_consum_id;
	}
	/** 设置：消费端配置id */
	public void setSdm_consum_id(Long sdm_consum_id){
		this.sdm_consum_id=sdm_consum_id;
	}
	/** 设置：消费端配置id */
	public void setSdm_consum_id(String sdm_consum_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_consum_id)){
			this.sdm_consum_id=new Long(sdm_consum_id);
		}
	}
	/** 取得：海云外部流数据管理消费端目的地 */
	public String getSdm_conf_describe(){
		return sdm_conf_describe;
	}
	/** 设置：海云外部流数据管理消费端目的地 */
	public void setSdm_conf_describe(String sdm_conf_describe){
		this.sdm_conf_describe=sdm_conf_describe;
	}
	/** 取得：海云内部消费目的地 */
	public String getHyren_consumedes(){
		return hyren_consumedes;
	}
	/** 设置：海云内部消费目的地 */
	public void setHyren_consumedes(String hyren_consumedes){
		this.hyren_consumedes=hyren_consumedes;
	}
	/** 取得：hdfs文件类型 */
	public String getHdfs_file_type(){
		return hdfs_file_type;
	}
	/** 设置：hdfs文件类型 */
	public void setHdfs_file_type(String hdfs_file_type){
		this.hdfs_file_type=hdfs_file_type;
	}
	/** 取得：目的地业务处理类 */
	public String getDes_class(){
		return des_class;
	}
	/** 设置：目的地业务处理类 */
	public void setDes_class(String des_class){
		this.des_class=des_class;
	}
	/** 取得：外部文件类型 */
	public String getExternal_file_type(){
		return external_file_type;
	}
	/** 设置：外部文件类型 */
	public void setExternal_file_type(String external_file_type){
		this.external_file_type=external_file_type;
	}
	/** 取得：自定义业务类类型 */
	public String getCus_des_type(){
		return cus_des_type;
	}
	/** 设置：自定义业务类类型 */
	public void setCus_des_type(String cus_des_type){
		this.cus_des_type=cus_des_type;
	}
	/** 取得：目的地业务类类型 */
	public String getDescustom_buscla(){
		return descustom_buscla;
	}
	/** 设置：目的地业务类类型 */
	public void setDescustom_buscla(String descustom_buscla){
		this.descustom_buscla=descustom_buscla;
	}
}
