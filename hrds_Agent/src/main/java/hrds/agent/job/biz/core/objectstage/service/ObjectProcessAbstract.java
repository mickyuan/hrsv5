package hrds.agent.job.biz.core.objectstage.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.Node;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TableProcessAbstract
 * date: 2020/4/26 17:25
 * author: zxz
 */
public abstract class ObjectProcessAbstract implements ObjectProcessInterface {
	//打印日志
//	private static final Logger LOGGER = LogManager.getLogger();
	//采集db文件的文件信息
	protected TableBean tableBean;
	//采集的db文件定义的表信息
	protected ObjectTableBean objectTableBean;
	//数据字典定义的所有的列类型
	protected List<String> metaTypeList;
	//解析db文件的所有列
	protected List<String> metaColumnList;
	//新增的所有数据列信息
	protected List<String> selectColumnList;
	//是否为拉链更新或者直接更新的列
	protected Map<String, Boolean> isZipperKeyMap = new HashMap<>();
	private final List<Object_collect_struct> object_collect_structList;
	protected final Map<String, String> handleTypeMap;
	protected String operate_column;
	protected String etlDate;

	protected ObjectProcessAbstract(TableBean tableBean, ObjectTableBean objectTableBean) {
		this.objectTableBean = objectTableBean;
		this.tableBean = tableBean;
		this.operate_column = tableBean.getOperate_column();
		this.etlDate = objectTableBean.getEtlDate();
		//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
		this.selectColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
		this.metaColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		this.metaTypeList = StringUtil.split(tableBean.getColTypeMetaInfo(), Constant.METAINFOSPLIT);
		this.object_collect_structList = objectTableBean.getObject_collect_structList();
		this.handleTypeMap = getHandleTypeMap(objectTableBean.getObject_handle_typeList());
		//这里的主键其实是跟新列，不会在建表的时候设置主键属性的
		List<String> primaryKeyList = StringUtil.split(tableBean.getPrimaryKeyInfo(), Constant.METAINFOSPLIT);
		for (int i = 0; i < primaryKeyList.size(); i++) {
			this.isZipperKeyMap.put(selectColumnList.get(i), IsFlag.Shi.getCode().equals(primaryKeyList.get(i)));
		}
	}

	private Map<String, String> getHandleTypeMap(List<Object_handle_type> object_handle_typeList) {
		Map<String, String> hMap = new HashMap<>();
		for (Object_handle_type handle : object_handle_typeList) {
			hMap.put(handle.getHandle_value(), handle.getHandle_type());
		}
		return hMap;
	}

	protected List<Map<String, Object>> getListTiledAttributes(String lineValue, long num) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			Object object = JSONObject.parse(lineValue);
			List<Node> nodeList = new ArrayList<>();
			getNodeList(object, nodeList, null, 0);
			//遍历所有的nodeList
			for (Node node : nodeList) {
				Map<String, Object> attributes = new HashMap<>();
				getTiledAttributes(node, attributes);
				list.add(attributes);
			}
		} catch (JSONException e) {
			throw new AppSystemException("半结构化对象采集第" + num + "行数据格式不正确,数据为:" + lineValue);
		}
		return list;
	}

	private void getTiledAttributes(Node node, Map<String, Object> attributes) {
		if (node.getParentNode() != null) {
			attributes.putAll(node.getAttributes());
			getTiledAttributes(node.getParentNode(), attributes);
		} else {
			attributes.putAll(node.getAttributes());
		}
	}

	private void getNodeList(Object object, List<Node> nodeList, Node parentNode, int index) {
		if (object instanceof JSONObject) {
			boolean flag = true;
			Node node = new Node();
			node.setParentNode(parentNode);
			JSONObject jsonObject = (JSONObject) object;
			//过滤同一层已经进了下一层的key
			List<String> keyList = new ArrayList<>();
			for (String key : jsonObject.keySet()) {
				for (Object_collect_struct struct : object_collect_structList) {
					String[] split = struct.getColumnposition().split(",");
					if (split.length == index + 1 && key.equals(split[index])) {
						//表明这是最后一层，直接取这个值
						if (node.getAttributes() != null) {
							node.getAttributes().put(struct.getColumn_name(), getColumnValue(struct.getColumn_type(),
									jsonObject.get(key).toString()));
						} else {
							Map<String, Object> attributes = new HashMap<>();
							attributes.put(struct.getColumn_name(), getColumnValue(struct.getColumn_type(),
									jsonObject.get(key).toString()));
							node.setAttributes(attributes);
						}
					} else if (split.length == index + 2 && key.equals(split[index]) && !keyList.contains(key)) {
						keyList.add(key);
						flag = false;
						//表明一定是个json数组或者对象
						Object parse = JSONObject.parse(jsonObject.get(split[index]).toString());
						getNodeList(parse, nodeList, node, index + 1);
					}
				}
			}
			if (flag) {
				nodeList.add(node);
			}
		} else {
			JSONArray jsonArray = (JSONArray) object;
			for (Object object1 : jsonArray) {
				getNodeList(object1, nodeList, parentNode, index);
			}
		}
	}
	//TODO 这个json有一个前提条件，下面不管有多少层，operate必须在第一层 这里再想想
//	private void getJSONValues(Object object, int i, int j, Map<Integer, Map<String, Map<String, Object>>> valueMap) {
//		if (object instanceof JSONObject) {
//			JSONObject jsonObject = (JSONObject) object;
//			Map<String, Map<String, Object>> stringMapMap;
//			Map<String, Object> map = null;
//			if (valueMap.get(i) != null) {
//				stringMapMap = valueMap.get(i);
//				if (!stringMapMap.isEmpty()) {
//					for (String key : stringMapMap.keySet()) {
//						map = stringMapMap.get(key);
//						break;
//					}
//				} else {
//					map = new HashMap<>();
//				}
//			} else {
//				stringMapMap = new HashMap<>();
//				map = new HashMap<>();
//			}
//			boolean flag = true;
//			for (String key : jsonObject.keySet()) {
//				for (Object_collect_struct struct : object_collect_structList) {
//					String[] split = struct.getColumnposition().split(",");
//					if (split.length == j + 1 && key.equals(split[j])) {
//						//表明这是最后一层，直接取这个值
//						if (IsFlag.Shi.getCode().equals(struct.getIs_operate())) {
//							//是操作列
//							if (OperationType.INSERT.getCode().equals(handleTypeMap.get(jsonObject.getString(key)))) {
//								//新增
//								stringMapMap.put("insert", map);
//							} else if (OperationType.UPDATE.getCode().equals(handleTypeMap.get(jsonObject.getString(key)))) {
//								//更新
//								stringMapMap.put("update", map);
//							} else if (OperationType.DELETE.getCode().equals(handleTypeMap.get(jsonObject.getString(key)))) {
//								//删除
//								stringMapMap.put("delete", map);
//							} else {
//								throw new AppSystemException("错误的数据更新类型");
//							}
//						} else {
//							//不是操作列
//							map.put(struct.getColumn_name(), getColumnValue(struct.getColumn_type(),
//									jsonObject.get(key).toString()));
//						}
//					} else if (split.length > j + 1) {
//						flag = false;
//						valueMap.put(i, stringMapMap);
//						//表明一定是个json数组或者对象
//						Object parse = JSONObject.parse(jsonObject.get(split[j]).toString());
//						getJSONValues(parse, i, j + 1, valueMap);
//					}
//				}
//			}
//			if (flag) {
//				i = i + 1;
//			}
//			valueMap.put(i, stringMapMap);
//		} else if (object instanceof JSONArray) {
//			JSONArray jsonArray = (JSONArray) object;
//			for (Object object1 : jsonArray) {
//				getJSONValues(object1, i, j, valueMap);
//			}
//		}
//	}

	private Object getColumnValue(String columnType, String columnValue) {
		Object str;
		columnType = columnType.toLowerCase();
		if (columnType.contains(DataTypeConstant.BOOLEAN.getMessage())) {
			// 如果取出的值为null则给空字符串
			str = Boolean.parseBoolean(columnValue.trim());
		} else if (columnType.contains(DataTypeConstant.INT8.getMessage())
				|| columnType.contains(DataTypeConstant.BIGINT.getMessage())
				|| columnType.contains(DataTypeConstant.LONG.getMessage())) {
			str = Long.parseLong(columnValue.trim());
		} else if (columnType.contains(DataTypeConstant.INT.getMessage())) {
			str = Integer.parseInt(columnValue.trim());
		} else if (columnType.contains(DataTypeConstant.FLOAT.getMessage())) {
			str = Float.parseFloat(columnValue.trim());
		} else if (columnType.contains(DataTypeConstant.DOUBLE.getMessage())
				|| columnType.contains(DataTypeConstant.DECIMAL.getMessage())
				|| columnType.contains(DataTypeConstant.NUMERIC.getMessage())) {
			str = Double.parseDouble(columnValue.trim());
		} else {
			// 如果取出的值为null则给空字符串
			if (columnValue == null) {
				str = "";
			} else {
				str = columnValue.trim();
			}
			//TODO 这里应该有好多类型需要支持，然后在else里面报错
		}
		return str;
	}

	public static void main(String[] args) {
		String str = "aaa";
		Object parse = JSONArray.parse(str);
		if (parse instanceof JSONObject) {
			System.out.println(parse.toString());
		} else if (parse instanceof JSONArray) {
			System.out.println(parse.toString());
		} else {
			System.out.println(parse.toString());
		}
	}
}
