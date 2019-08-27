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
@Table(tableName = "ml_rand_fore_cm")
public class MlRandForeCm extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_rand_fore_cm";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("model_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal tree_num;
	private String model_runstate;
	private String create_time;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal model_id;
	private BigDecimal segmmaxinumb;
	private String model_path;
	private String model_name;
	private String nodeimpmeamet;
	private String dv_column;
	private BigDecimal rnum_seed;
	private String create_date;
	private BigDecimal tree_max_depth;

	public BigDecimal getTree_num() { return tree_num; }
	public void setTree_num(BigDecimal tree_num) {
		if(tree_num==null) throw new BusinessException("Entity : MlRandForeCm.tree_num must not null!");
		this.tree_num = tree_num;
	}

	public String getModel_runstate() { return model_runstate; }
	public void setModel_runstate(String model_runstate) {
		if(model_runstate==null) throw new BusinessException("Entity : MlRandForeCm.model_runstate must not null!");
		this.model_runstate = model_runstate;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlRandForeCm.create_time must not null!");
		this.create_time = create_time;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlRandForeCm.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getModel_id() { return model_id; }
	public void setModel_id(BigDecimal model_id) {
		if(model_id==null) throw new BusinessException("Entity : MlRandForeCm.model_id must not null!");
		this.model_id = model_id;
	}

	public BigDecimal getSegmmaxinumb() { return segmmaxinumb; }
	public void setSegmmaxinumb(BigDecimal segmmaxinumb) {
		if(segmmaxinumb==null) addNullValueField("segmmaxinumb");
		this.segmmaxinumb = segmmaxinumb;
	}

	public String getModel_path() { return model_path; }
	public void setModel_path(String model_path) {
		if(model_path==null) addNullValueField("model_path");
		this.model_path = model_path;
	}

	public String getModel_name() { return model_name; }
	public void setModel_name(String model_name) {
		if(model_name==null) throw new BusinessException("Entity : MlRandForeCm.model_name must not null!");
		this.model_name = model_name;
	}

	public String getNodeimpmeamet() { return nodeimpmeamet; }
	public void setNodeimpmeamet(String nodeimpmeamet) {
		if(nodeimpmeamet==null) throw new BusinessException("Entity : MlRandForeCm.nodeimpmeamet must not null!");
		this.nodeimpmeamet = nodeimpmeamet;
	}

	public String getDv_column() { return dv_column; }
	public void setDv_column(String dv_column) {
		if(dv_column==null) throw new BusinessException("Entity : MlRandForeCm.dv_column must not null!");
		this.dv_column = dv_column;
	}

	public BigDecimal getRnum_seed() { return rnum_seed; }
	public void setRnum_seed(BigDecimal rnum_seed) {
		if(rnum_seed==null) throw new BusinessException("Entity : MlRandForeCm.rnum_seed must not null!");
		this.rnum_seed = rnum_seed;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlRandForeCm.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getTree_max_depth() { return tree_max_depth; }
	public void setTree_max_depth(BigDecimal tree_max_depth) {
		if(tree_max_depth==null) throw new BusinessException("Entity : MlRandForeCm.tree_max_depth must not null!");
		this.tree_max_depth = tree_max_depth;
	}

}