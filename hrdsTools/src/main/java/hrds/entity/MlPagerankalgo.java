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
@Table(tableName = "ml_pagerankalgo")
public class MlPagerankalgo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_pagerankalgo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("algorithm_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String bevoted_link;
	private String create_time;
	private String algo_runstate;
	private String vote_link;
	private BigDecimal numiterations;
	private String remark;
	private BigDecimal dtable_info_id;
	private String algorithm_name;
	private String create_date;
	private BigDecimal algorithm_id;
	private String result_path;

	public String getBevoted_link() { return bevoted_link; }
	public void setBevoted_link(String bevoted_link) {
		if(bevoted_link==null) throw new BusinessException("Entity : MlPagerankalgo.bevoted_link must not null!");
		this.bevoted_link = bevoted_link;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlPagerankalgo.create_time must not null!");
		this.create_time = create_time;
	}

	public String getAlgo_runstate() { return algo_runstate; }
	public void setAlgo_runstate(String algo_runstate) {
		if(algo_runstate==null) throw new BusinessException("Entity : MlPagerankalgo.algo_runstate must not null!");
		this.algo_runstate = algo_runstate;
	}

	public String getVote_link() { return vote_link; }
	public void setVote_link(String vote_link) {
		if(vote_link==null) throw new BusinessException("Entity : MlPagerankalgo.vote_link must not null!");
		this.vote_link = vote_link;
	}

	public BigDecimal getNumiterations() { return numiterations; }
	public void setNumiterations(BigDecimal numiterations) {
		if(numiterations==null) throw new BusinessException("Entity : MlPagerankalgo.numiterations must not null!");
		this.numiterations = numiterations;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlPagerankalgo.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getAlgorithm_name() { return algorithm_name; }
	public void setAlgorithm_name(String algorithm_name) {
		if(algorithm_name==null) throw new BusinessException("Entity : MlPagerankalgo.algorithm_name must not null!");
		this.algorithm_name = algorithm_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlPagerankalgo.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getAlgorithm_id() { return algorithm_id; }
	public void setAlgorithm_id(BigDecimal algorithm_id) {
		if(algorithm_id==null) throw new BusinessException("Entity : MlPagerankalgo.algorithm_id must not null!");
		this.algorithm_id = algorithm_id;
	}

	public String getResult_path() { return result_path; }
	public void setResult_path(String result_path) {
		if(result_path==null) addNullValueField("result_path");
		this.result_path = result_path;
	}

}