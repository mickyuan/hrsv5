package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@DocClass(desc = "表存储配置信息", author = "zxz", createdate = "2019/11/29 21:15")
public class DataStoreConfBean implements Serializable {
	@DocBean(name = "dsl_name", value = "配置属性名称:", dataType = String.class, required = true)
	private String dsl_name;
	@DocBean(name = "store_type", value = "存储类型(store_type):1-关系型数据库<DATABASE> 2-Hbase<HBASE> " +
			"3-solr<SOLR> 4-ElasticSearch<ElasticSearch> 5-mongodb<MONGODB> ", dataType = String.class, required = true)
	private String store_type;
	@DocBean(name = "data_store_connect_attr", value = "数据存储层连接配置属性:", dataType = Map.class, required = true)
	private Map<String, String> data_store_connect_attr;
	@DocBean(name = "additInfoField", value = "附加信息字段:", dataType = Map.class, required = true)
	private Map<String, Map<Integer, String>> additInfoFieldMap;
	@DocBean(name = "is_hadoopclient", value = "是否有hadoop客户端(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_hadoopclient;
	@DocBean(name = "dtcs_name", value = "类型对照名称:", dataType = String.class, required = true)
	private String dtcs_name;
	@DocBean(name = "dlcs_name", value = "长度对照名称:", dataType = String.class, required = true)
	private String dlcs_name;
	@DocBean(name = "data_store_layer_file", value = "数据存储层配置文件属性:", dataType = Map.class, required = true)
	private Map<String, String> data_store_layer_file;

	public String getDsl_name() {
		return dsl_name;
	}

	public void setDsl_name(String dsl_name) {
		this.dsl_name = dsl_name;
	}

	public String getStore_type() {
		return store_type;
	}

	public void setStore_type(String store_type) {
		this.store_type = store_type;
	}

	public Map<String, String> getData_store_connect_attr() {
		return data_store_connect_attr;
	}

	public void setData_store_connect_attr(Map<String, String> data_store_connect_attr) {
		this.data_store_connect_attr = data_store_connect_attr;
	}

	public String getIs_hadoopclient() {
		return is_hadoopclient;
	}

	public void setIs_hadoopclient(String is_hadoopclient) {
		this.is_hadoopclient = is_hadoopclient;
	}

	public String getDtcs_name() {
		return dtcs_name;
	}

	public void setDtcs_name(String dtcs_name) {
		this.dtcs_name = dtcs_name;
	}

	public String getDlcs_name() {
		return dlcs_name;
	}

	public void setDlcs_name(String dlcs_name) {
		this.dlcs_name = dlcs_name;
	}

	public Map<String, String> getData_store_layer_file() {
		return data_store_layer_file;
	}

	public void setData_store_layer_file(Map<String, String> data_store_layer_file) {
		this.data_store_layer_file = data_store_layer_file;
	}

	public Map<String, Map<Integer, String>> getAdditInfoFieldMap() {
		return additInfoFieldMap;
	}

	public void setAdditInfoFieldMap(Map<String, Map<Integer, String>> additInfoFieldMap) {
		Map<String, Map<Integer, String>> sortAdditInfoFieldMap = new HashMap<>();
		for (String key : additInfoFieldMap.keySet()) {
			Map<Integer, String> sortMap = new TreeMap<>(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1 - o2;
				}
			});
			sortMap.putAll(additInfoFieldMap.get(key));
			sortAdditInfoFieldMap.put(key, sortMap);
		}
		this.additInfoFieldMap = sortAdditInfoFieldMap;
	}

	public static void main(String[] args) {
		DataStoreConfBean dataStoreConfBean = new DataStoreConfBean();
		Map<String, Map<Integer, String>> sortAdditInfoFieldMap = new HashMap<>();
		Map<Integer, String> aaa = new HashMap<>();
		aaa.put(9, "aaa9");
		aaa.put(7, "aaa7");
		aaa.put(5, "aaa5");
		aaa.put(1, "aaa1");
		aaa.put(2, "aaa2");
		aaa.put(3, "aaa3");
		sortAdditInfoFieldMap.put("aaa", aaa);
		dataStoreConfBean.setAdditInfoFieldMap(sortAdditInfoFieldMap);
		for (Integer key : dataStoreConfBean.getAdditInfoFieldMap().get("aaa").keySet()) {
			System.out.println(dataStoreConfBean.getAdditInfoFieldMap().get("aaa").get(key));
		}
	}
}
