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
 * 轴线配置信息表
 */
@Table(tableName = "auto_axisline_info")
public class Auto_axisline_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axisline_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 轴线配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axisline_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long axisline_id; //轴线编号
	private String show; //是否显示
	private String onzero; //是否在0 刻度
	private String symbol; //轴线箭头显示方式
	private Integer symboloffset; //轴线箭头偏移量
	private Long axis_id; //轴编号

	/** 取得：轴线编号 */
	public Long getAxisline_id(){
		return axisline_id;
	}
	/** 设置：轴线编号 */
	public void setAxisline_id(Long axisline_id){
		this.axisline_id=axisline_id;
	}
	/** 设置：轴线编号 */
	public void setAxisline_id(String axisline_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axisline_id)){
			this.axisline_id=new Long(axisline_id);
		}
	}
	/** 取得：是否显示 */
	public String getShow(){
		return show;
	}
	/** 设置：是否显示 */
	public void setShow(String show){
		this.show=show;
	}
	/** 取得：是否在0 刻度 */
	public String getOnzero(){
		return onzero;
	}
	/** 设置：是否在0 刻度 */
	public void setOnzero(String onzero){
		this.onzero=onzero;
	}
	/** 取得：轴线箭头显示方式 */
	public String getSymbol(){
		return symbol;
	}
	/** 设置：轴线箭头显示方式 */
	public void setSymbol(String symbol){
		this.symbol=symbol;
	}
	/** 取得：轴线箭头偏移量 */
	public Integer getSymboloffset(){
		return symboloffset;
	}
	/** 设置：轴线箭头偏移量 */
	public void setSymboloffset(Integer symboloffset){
		this.symboloffset=symboloffset;
	}
	/** 设置：轴线箭头偏移量 */
	public void setSymboloffset(String symboloffset){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symboloffset)){
			this.symboloffset=new Integer(symboloffset);
		}
	}
	/** 取得：轴编号 */
	public Long getAxis_id(){
		return axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(Long axis_id){
		this.axis_id=axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(String axis_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axis_id)){
			this.axis_id=new Long(axis_id);
		}
	}
}
