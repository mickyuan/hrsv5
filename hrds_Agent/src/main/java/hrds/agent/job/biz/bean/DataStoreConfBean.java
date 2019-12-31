package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;

import java.io.Serializable;
import java.util.*;

@DocClass(desc = "表存储配置信息", author = "zxz", createdate = "2019/11/29 21:15")
public class DataStoreConfBean implements Serializable {
	@DocBean(name = "dsl_name", value = "配置属性名称:", dataType = String.class, required = true)
	private String dsl_name;
	@DocBean(name = "store_type", value = "存储类型(store_type):1-关系型数据库<DATABASE> 2-Hbase<HBASE> " +
			"3-solr<SOLR> 4-ElasticSearch<ElasticSearch> 5-mongodb<MONGODB> ", dataType = String.class, required = true)
	private String store_type;
	@DocBean(name = "data_store_connect_attr", value = "数据存储层连接配置属性:", dataType = Map.class, required = true)
	private Map<String, String> data_store_connect_attr;
	@DocBean(name = "additInfoField", value = "附加信息字段，外层的Map集合,key为dsla_storelayer，内层的Map合集，key为字段名，value为csi_number"
			, dataType = Map.class, required = true)
	private Map<String, Map<String, Integer>> additInfoFieldMap;
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

	public Map<String, Map<String, Integer>> getAdditInfoFieldMap() {
		return additInfoFieldMap;
	}

	public void setAdditInfoFieldMap(Map<String, Map<String, Integer>> additInfoFieldMap) {
		Map<String, Map<String, Integer>> sortAdditInfoFieldMap = new HashMap<>();
		for (String key : additInfoFieldMap.keySet()) {
			List<Map.Entry<String, Integer>> list = new ArrayList<>(additInfoFieldMap.get(key).entrySet());

			//比较器排序
//			list.sort(new Comparator<Map.Entry<String, Integer>>() {
//				@Override
//				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//					//升序
//					return o1.getValue().compareTo(o2.getValue());
//				}
//			});
			//lambda表达式
//			list.sort((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
//					-> o1.getValue().compareTo(o2.getValue()));
			//lambda表达式简化写法
			list.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue));

			Map<String, Integer> sortMap = new TreeMap<>();
			for (Map.Entry<String, Integer> map : list) {
				sortMap.put(map.getKey(), map.getValue());
			}
			sortAdditInfoFieldMap.put(key, sortMap);
		}
		this.additInfoFieldMap = sortAdditInfoFieldMap;
	}

	public static void main(String[] args) {
		DataStoreConfBean dataStoreConfBean = new DataStoreConfBean();
		Map<String, Map<String, Integer>> sortAdditInfoFieldMap = new HashMap<>();
		Map<String, Integer> aaa = new HashMap<>();
		aaa.put("aaa9", 9);
		aaa.put("aaa7", 7);
		aaa.put("aaa5", 5);
		aaa.put("aaa1", 1);
		aaa.put("aaa2", 2);
		aaa.put("aaa3", 3);
		sortAdditInfoFieldMap.put("aaa", aaa);
		dataStoreConfBean.setAdditInfoFieldMap(sortAdditInfoFieldMap);
		for (String key : dataStoreConfBean.getAdditInfoFieldMap().get("aaa").keySet()) {
			System.out.println(dataStoreConfBean.getAdditInfoFieldMap().get("aaa").get(key));
		}
	}
}
