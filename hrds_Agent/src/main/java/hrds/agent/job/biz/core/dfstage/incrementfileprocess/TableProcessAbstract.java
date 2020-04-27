package hrds.agent.job.biz.core.dfstage.incrementfileprocess;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.DataBaseCode;
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
public class TableProcessAbstract implements TableProcessInterface {
	//采集db文件的文件信息
	protected TableBean tableBean;
	//采集的db文件定义的表信息
	protected CollectTableBean collectTableBean;
	//读文件的全路径
	protected String readFile;
	//数据字典定义的所有的列类型
	private List<String> dictionaryTypeList;
	//解析db文件的所有列
	private List<String> dictionaryColumnList;

	protected TableProcessAbstract(TableBean tableBean, CollectTableBean collectTableBean, String readFile) {
		this.collectTableBean = collectTableBean;
		this.readFile = readFile;
		this.tableBean = tableBean;
		//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
		this.dictionaryColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
		this.dictionaryTypeList = StringUtil.split(tableBean.getAllType(), Constant.METAINFOSPLIT);
	}

	@Override
	public void parserFileToTable() {
		long fileRowCount = 0;
		String lineValue;
		String code = DataBaseCode.ofValueByCode(tableBean.getFile_code());
		List<Integer> lengthList = new ArrayList<>();
		for (String type : dictionaryTypeList) {
			lengthList.add(TypeTransLength.getLength(type));
		}
		// 存储全量插入信息的list
		List<Map<String, String>> valueList;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(readFile)), code))) {
			while ((lineValue = br.readLine()) != null) {
				fileRowCount++;
				//获取定长文件，解析每行数据转为list
				valueList = getDingChangValueList(lineValue, dictionaryColumnList, lengthList, code);
				//校验数据
			}
		} catch (Exception e) {
			throw new AppSystemException("解析非定长文件转存报错", e);
		}
	}

	private List<Map<String, String>> getDingChangValueList(String line, List<String> dictionaryColumnList,
	                                                        List<Integer> lengthList, String database_code)
			throws Exception {
		//先获取表的操作类型，前六个字符，分别是delete、update、insert
		List<Map<String, String>> valueList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		byte[] bytes = line.getBytes(database_code);
		int begin = 0;
		for (int i = 0; i < dictionaryColumnList.size(); i++) {
			int length = lengthList.get(i);
			byte[] byteTmp = new byte[length];
			System.arraycopy(bytes, begin, byteTmp, 0, length);
			begin += length;
			String columnValue = new String(byteTmp, database_code);
			if (!StringUtil.isEmpty(columnValue)) {
//				Map<String, String> map = new HashMap<>();
				map.put(dictionaryColumnList.get(i), columnValue);
				valueList.add(map);
			}
		}
		return valueList;
	}

}
