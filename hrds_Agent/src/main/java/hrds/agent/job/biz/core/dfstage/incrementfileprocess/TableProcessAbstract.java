package hrds.agent.job.biz.core.dfstage.incrementfileprocess;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TableProcessAbstract
 * date: 2020/4/26 17:25
 * author: zxz
 */
public abstract class TableProcessAbstract implements TableProcessInterface {
	//采集db文件的文件信息
	protected TableBean tableBean;
	//采集的db文件定义的表信息
	protected CollectTableBean collectTableBean;
	//数据字典定义的所有的列类型
	private List<String> dictionaryTypeList;
	//解析db文件的所有列
	private List<String> dictionaryColumnList;
	//是否为主键的列
	protected List<Boolean> isPrimaryKeyList = new ArrayList<>();
	//新增的所有数据列信息
	protected List<String> insertColumnList;
	//更新的所有数据列信息
	protected List<String> updateColumnList;
	//删除的所有数据列信息
	protected List<String> deleteColumnList;

	protected TableProcessAbstract(TableBean tableBean, CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
		//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
		this.dictionaryColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
		this.dictionaryTypeList = StringUtil.split(tableBean.getAllType(), Constant.METAINFOSPLIT);
		this.insertColumnList = StringUtil.split(tableBean.getInsertColumnInfo().toUpperCase(), Constant.METAINFOSPLIT);
		this.updateColumnList = StringUtil.split(tableBean.getUpdateColumnInfo().toUpperCase(), Constant.METAINFOSPLIT);
		this.deleteColumnList = StringUtil.split(tableBean.getDeleteColumnInfo().toUpperCase(), Constant.METAINFOSPLIT);
		for (String isFlag : StringUtil.split(tableBean.getPrimaryKeyInfo(), Constant.METAINFOSPLIT)) {
			this.isPrimaryKeyList.add(IsFlag.Shi.getCode().equals(isFlag));
		}
	}

	@Override
	public void parserFileToTable(String readFile) {
		String lineValue;
		String code = DataBaseCode.ofValueByCode(tableBean.getFile_code());
		List<Integer> lengthList = new ArrayList<>();
		for (String type : dictionaryTypeList) {
			lengthList.add(TypeTransLength.getLength(type));
		}
		// 存储全量插入信息的list
		Map<String, Map<String, Object>> valueList;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(readFile)), code))) {
			while ((lineValue = br.readLine()) != null) {
				//获取定长文件，解析每行数据转为list
				valueList = getDingChangValueList(lineValue, dictionaryColumnList, lengthList, code);
				//处理数据
				dealData(valueList);
			}
			//读完了，再执行一次，确保数据完全执行完
			excute();
		} catch (Exception e) {
			throw new AppSystemException("解析非定长文件转存报错", e);
		}
	}

	private Map<String, Map<String, Object>> getDingChangValueList(String line, List<String> dictionaryColumnList,
	                                                               List<Integer> lengthList, String database_code)
			throws Exception {
		//先获取表的操作类型，前六个字符，分别是delete、update、insert
		Map<String, Map<String, Object>> valueList = new HashMap<>();
		String operate = line.substring(0, 6);
		line = line.substring(6);
		Map<String, Object> map = new HashMap<>();
		byte[] bytes = line.getBytes(database_code);
		int begin = 0;
		for (int i = 0; i < dictionaryColumnList.size(); i++) {
			int length = lengthList.get(i);
			byte[] byteTmp = new byte[length];
			System.arraycopy(bytes, begin, byteTmp, 0, length);
			begin += length;
			String columnValue = new String(byteTmp, database_code);
			switch (operate) {
				case "insert":
					if (insertColumnList.contains(dictionaryColumnList.get(i))) {
						map.put(dictionaryColumnList.get(i), getColumnValue(dictionaryTypeList.get(i), columnValue));
					}
					break;
				case "update":
					if (updateColumnList.contains(dictionaryColumnList.get(i))) {
						map.put(dictionaryColumnList.get(i), getColumnValue(dictionaryTypeList.get(i), columnValue));
					}
					break;
				case "delete":
					if (deleteColumnList.contains(dictionaryColumnList.get(i))) {
						map.put(dictionaryColumnList.get(i), getColumnValue(dictionaryTypeList.get(i), columnValue));
					}
					break;
				default:
					throw new AppSystemException("增量数据采集不自持" + operate + "操作");
			}
		}
		valueList.put(operate, map);
		return valueList;
	}

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
			}else{
				str = columnValue.trim();
			}
			//TODO 这里应该有好多类型需要支持，然后在else里面报错
		}
		return str;
	}

}
