package hrds.agent.job.biz.bean;

import java.util.Map;

public class Node {
	private Node parentNode;
	private Map<String, Object> attributes;

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "Node{" +
				"parentNode=" + parentNode +
				", attributes=" + attributes +
				'}';
	}
}
