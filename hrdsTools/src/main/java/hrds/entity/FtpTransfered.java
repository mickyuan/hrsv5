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
@Table(tableName = "ftp_transfered")
public class FtpTransfered extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ftp_transfered";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ftp_transfered_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ftp_date;
	private String ftp_time;
	private String remark;
	private BigDecimal ftp_id;
	private BigDecimal ftp_transfered_id;
	private String transfered_name;

	public String getFtp_date() { return ftp_date; }
	public void setFtp_date(String ftp_date) {
		if(ftp_date==null) throw new BusinessException("Entity : FtpTransfered.ftp_date must not null!");
		this.ftp_date = ftp_date;
	}

	public String getFtp_time() { return ftp_time; }
	public void setFtp_time(String ftp_time) {
		if(ftp_time==null) throw new BusinessException("Entity : FtpTransfered.ftp_time must not null!");
		this.ftp_time = ftp_time;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getFtp_id() { return ftp_id; }
	public void setFtp_id(BigDecimal ftp_id) {
		if(ftp_id==null) throw new BusinessException("Entity : FtpTransfered.ftp_id must not null!");
		this.ftp_id = ftp_id;
	}

	public BigDecimal getFtp_transfered_id() { return ftp_transfered_id; }
	public void setFtp_transfered_id(BigDecimal ftp_transfered_id) {
		if(ftp_transfered_id==null) throw new BusinessException("Entity : FtpTransfered.ftp_transfered_id must not null!");
		this.ftp_transfered_id = ftp_transfered_id;
	}

	public String getTransfered_name() { return transfered_name; }
	public void setTransfered_name(String transfered_name) {
		if(transfered_name==null) throw new BusinessException("Entity : FtpTransfered.transfered_name must not null!");
		this.transfered_name = transfered_name;
	}

}