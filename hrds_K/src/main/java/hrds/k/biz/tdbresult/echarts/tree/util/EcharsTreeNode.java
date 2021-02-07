package hrds.k.biz.tdbresult.echarts.tree.util;

import java.util.ArrayList;
import java.util.List;

/**
 * echars tree 节点类
 */
public class EcharsTreeNode {

	/**
	 * 节点id,唯一值保证节点的唯一性
	 */
	private String id;
	/**
	 * 节点名
	 */
	private String name;
	/**
	 * 节点值
	 */
	private String value;
	/**
	 * 父节点编号
	 */
	private String parent_id;
	/**
	 * 孩子节点列表
	 */
	private List<EcharsTreeNode> children = new ArrayList<>();

	// 先序遍历，拼接JSON字符串
	public String toString() {
		String str = "{"
			+ "name : '" + name + "'"
			+ ", value : '" + value + "'"
			+ ", parent_id : '" + parent_id + "'";
		if (children != null && children.size() != 0) {
			str += ", children : " + children.toString();
		}
		return str + "}";
	}

	// 添加孩子节点
	void addChild(EcharsTreeNode echarsTreeNode) {
		this.children.add(echarsTreeNode);
	}

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public List<EcharsTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<EcharsTreeNode> children) {
		this.children = children;
	}
}
