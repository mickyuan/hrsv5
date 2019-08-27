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
@Table(tableName = "edw_job_alg")
public class EdwJobAlg extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_job_alg";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("algorithmcode");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String algorithmname;
	private String algorithmcode;

	public String getAlgorithmname() { return algorithmname; }
	public void setAlgorithmname(String algorithmname) {
		if(algorithmname==null) addNullValueField("algorithmname");
		this.algorithmname = algorithmname;
	}

	public String getAlgorithmcode() { return algorithmcode; }
	public void setAlgorithmcode(String algorithmcode) {
		if(algorithmcode==null) throw new BusinessException("Entity : EdwJobAlg.algorithmcode must not null!");
		this.algorithmcode = algorithmcode;
	}

}