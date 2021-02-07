package hrds.k.biz.tdbresult.echarts.graph;

/**
 * 关系节点对象
 */
public class GraphNode {

	/**
	 * 节点编号
	 */
	private String id;
	/**
	 * 节点名
	 */
	private String name;
	/**
	 * 节点大小
	 */
	private Double symbolSize;
	/**
	 * x轴位置
	 */
	private Double x;
	/**
	 * y轴位置
	 */
	private Double y;
	/**
	 * 节点详细信息
	 */
	private String value;
	/**
	 * 节点所属分类
	 */
	private Integer category;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSymbolSize() {
		return symbolSize;
	}

	public void setSymbolSize(Double symbolSize) {
		this.symbolSize = symbolSize;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}
}
