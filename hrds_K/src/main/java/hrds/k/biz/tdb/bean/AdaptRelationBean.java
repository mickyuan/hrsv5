package hrds.k.biz.tdb.bean;

import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.Map;

public class AdaptRelationBean extends ProjectTableEntity {
	//节点的集合,map的key是nodeID,map的value是Node的属性
	private Map<Long, Map<String, Object>> nodeCollection;
	//关系的集合,map的key是nodeID,map的value是relation的属性
	private Map<Long, Map<String, Object>> relationCollection;

	public Map<Long, Map<String, Object>> getNodeCollection() {
		return nodeCollection;
	}

	public void setNodeCollection(Map<Long, Map<String, Object>> nodeCollection) {
		this.nodeCollection = nodeCollection;
	}

	public Map<Long, Map<String, Object>> getRelationCollection() {
		return relationCollection;
	}

	public void setRelationCollection(Map<Long, Map<String, Object>> relationCollection) {
		this.relationCollection = relationCollection;
	}
}
