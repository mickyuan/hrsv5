package hrds.commons.collection.bean;

import fd.ng.core.annotation.DocBean;

import java.util.*;

/**
 * @program: hrsv5
 * @description: 存储层实体
 * @author: xchao
 * @create: 2020-04-13 17:57
 */
public class LayerBean {
	private static final long serialVersionUID = 321566870187324L;
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;
	@DocBean(name ="dsl_name",value="配置属性名称:",dataType = String.class,required = true)
	private String dsl_name;
	@DocBean(name ="store_type",value="存储类型(Store_type):1-关系型数据库<DATABASE> 2-hive<HIVE> 3-Hbase<HBASE> 4-solr<SOLR> 5-ElasticSearch<ElasticSearch> 6-mongodb<MONGODB> ",dataType = String.class,required = true)
	private String store_type;
	@DocBean(name ="dst",value="存储位置:",dataType = String.class,required = false)
	private String dst;
	private Map<String, String> layerAttr;

	public Map<String, String> getLayerAttr() {
		return layerAttr;
	}

	public void setLayerAttr(Map<String, String> layerAttr) {
		this.layerAttr = layerAttr;
	}

	public Set<String> getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(Set<String> tableNameList) {
		this.tableNameList = tableNameList;
	}

	@DocBean(name ="tableNameList",value="该存储层牵扯的表:",dataType = List.class,required = false)
	private Set<String> tableNameList;

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	/** 取得：存储层配置ID */
	public Long getDsl_id(){
		return dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(Long dsl_id){
		this.dsl_id=dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(String dsl_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsl_id)){
			this.dsl_id=new Long(dsl_id);
		}
	}
	/** 取得：配置属性名称 */
	public String getDsl_name(){
		return dsl_name;
	}
	/** 设置：配置属性名称 */
	public void setDsl_name(String dsl_name){
		this.dsl_name=dsl_name;
	}
	/** 取得：存储类型 */
	public String getStore_type(){
		return store_type;
	}
	/** 设置：存储类型 */
	public void setStore_type(String store_type){
		this.store_type=store_type;
	}

}
