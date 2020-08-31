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
 * 轴线配置信息表
 */
@Table(tableName = "auto_axisline_info")
public class Auto_axisline_info extends ProjectTableEntity
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
	@DocBean(name ="axis_id",value="轴编号:",dataType = Long.class,required = false)
	private Long axis_id;
	@DocBean(name ="axisline_id",value="轴线编号:",dataType = Long.class,required = true)
	private Long axisline_id;
	@DocBean(name ="show",value="是否显示(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String show;
	@DocBean(name ="onzero",value="是否在0 刻度(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String onzero;
	@DocBean(name ="symbol",value="轴线箭头显示方式:",dataType = String.class,required = false)
	private String symbol;
	@DocBean(name ="symboloffset",value="轴线箭头偏移量:",dataType = Long.class,required = true)
	private Long symboloffset;

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
	public Long getSymboloffset(){
		return symboloffset;
	}
	/** 设置：轴线箭头偏移量 */
	public void setSymboloffset(Long symboloffset){
		this.symboloffset=symboloffset;
	}
	/** 设置：轴线箭头偏移量 */
	public void setSymboloffset(String symboloffset){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symboloffset)){
			this.symboloffset=new Long(symboloffset);
		}
	}
}
