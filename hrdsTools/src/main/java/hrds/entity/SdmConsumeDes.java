package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "sdm_consume_des")
public class SdmConsumeDes extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_consume_des";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_des_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal sdm_consum_id;
	private String remark;
	private String des_class;
	private String sdm_cons_des;
	private String sdm_conf_describe;
	private String cus_des_type;
	private Integer thread_num;
	private String partition;
	private String external_file_type;
	private BigDecimal sdm_des_id;
	private String sdm_thr_partition;
	private String sdm_bus_pro_cla;
	private String hyren_consumedes;
	private String descustom_buscla;
	private String hdfs_file_type;

	public BigDecimal getSdm_consum_id() { return sdm_consum_id; }
	public void setSdm_consum_id(BigDecimal sdm_consum_id) {
		if(sdm_consum_id==null) throw new BusinessException("Entity : SdmConsumeDes.sdm_consum_id must not null!");
		this.sdm_consum_id = sdm_consum_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getDes_class() { return des_class; }
	public void setDes_class(String des_class) {
		if(des_class==null) addNullValueField("des_class");
		this.des_class = des_class;
	}

	public String getSdm_cons_des() { return sdm_cons_des; }
	public void setSdm_cons_des(String sdm_cons_des) {
		if(sdm_cons_des==null) throw new BusinessException("Entity : SdmConsumeDes.sdm_cons_des must not null!");
		this.sdm_cons_des = sdm_cons_des;
	}

	public String getSdm_conf_describe() { return sdm_conf_describe; }
	public void setSdm_conf_describe(String sdm_conf_describe) {
		if(sdm_conf_describe==null) addNullValueField("sdm_conf_describe");
		this.sdm_conf_describe = sdm_conf_describe;
	}

	public String getCus_des_type() { return cus_des_type; }
	public void setCus_des_type(String cus_des_type) {
		if(cus_des_type==null) throw new BusinessException("Entity : SdmConsumeDes.cus_des_type must not null!");
		this.cus_des_type = cus_des_type;
	}

	public Integer getThread_num() { return thread_num; }
	public void setThread_num(Integer thread_num) {
		if(thread_num==null) addNullValueField("thread_num");
		this.thread_num = thread_num;
	}

	public String getPartition() { return partition; }
	public void setPartition(String partition) {
		if(partition==null) addNullValueField("partition");
		this.partition = partition;
	}

	public String getExternal_file_type() { return external_file_type; }
	public void setExternal_file_type(String external_file_type) {
		if(external_file_type==null) addNullValueField("external_file_type");
		this.external_file_type = external_file_type;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConsumeDes.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getSdm_thr_partition() { return sdm_thr_partition; }
	public void setSdm_thr_partition(String sdm_thr_partition) {
		if(sdm_thr_partition==null) throw new BusinessException("Entity : SdmConsumeDes.sdm_thr_partition must not null!");
		this.sdm_thr_partition = sdm_thr_partition;
	}

	public String getSdm_bus_pro_cla() { return sdm_bus_pro_cla; }
	public void setSdm_bus_pro_cla(String sdm_bus_pro_cla) {
		if(sdm_bus_pro_cla==null) addNullValueField("sdm_bus_pro_cla");
		this.sdm_bus_pro_cla = sdm_bus_pro_cla;
	}

	public String getHyren_consumedes() { return hyren_consumedes; }
	public void setHyren_consumedes(String hyren_consumedes) {
		if(hyren_consumedes==null) addNullValueField("hyren_consumedes");
		this.hyren_consumedes = hyren_consumedes;
	}

	public String getDescustom_buscla() { return descustom_buscla; }
	public void setDescustom_buscla(String descustom_buscla) {
		if(descustom_buscla==null) throw new BusinessException("Entity : SdmConsumeDes.descustom_buscla must not null!");
		this.descustom_buscla = descustom_buscla;
	}

	public String getHdfs_file_type() { return hdfs_file_type; }
	public void setHdfs_file_type(String hdfs_file_type) {
		if(hdfs_file_type==null) addNullValueField("hdfs_file_type");
		this.hdfs_file_type = hdfs_file_type;
	}

}