package hrds.k.biz.tdbresult.echarts.graph;

/**
 * 关系对象
 */
public class Link {

	/**
	 * 关系来源节点id
	 */
	private String source;
	/**
	 * 关系目标节点id
	 */
	private String target;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
