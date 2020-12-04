package hrds.k.biz.tdb.bean;

import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.Map;

public class NodeRelationBean extends ProjectTableEntity {
	//左边节点,map的key是nodeID,map的value是Node的属性
	private Map<Long, Map<String, Object>> leftNode;
	//右边节点,map的key是nodeID,map的value是Node的属性
	private Map<Long, Map<String, Object>> rightNode;
	//线的类型、有INCLUDE(这时候leftNode是表节点)、FK、BFD、SAME、EQUALS、FD
	private String relationType;
	//线的id
	private Long relationId;

	public Map<Long, Map<String, Object>> getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Map<Long, Map<String, Object>> leftNode) {
		this.leftNode = leftNode;
	}

	public Map<Long, Map<String, Object>> getRightNode() {
		return rightNode;
	}

	public void setRightNode(Map<Long, Map<String, Object>> rightNode) {
		this.rightNode = rightNode;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	@Override
	public String toString() {
		return "NodeRelationBean{" +
				"leftNode=" + leftNode +
				", rightNode=" + rightNode +
				", relationType='" + relationType + '\'' +
				", relationId='" + relationId + '\'' +
				'}';
	}
}
