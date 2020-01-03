package hrds.agent.job.biz.core.dbstage.service;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.SQLUtil;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.Platform;
import hrds.commons.utils.xlstoxml.Xls2xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@DocClass(desc = "根据页面所选的表和字段对jdbc所返回的meta信息进行解析", author = "zxz", createdate = "2019/12/4 11:17")
public class CollectTableHandleParse {
	private final static Logger LOGGER = LoggerFactory.getLogger(CollectTableHandleParse.class);
	private static final String SINGLEQUOTE = "'";
	public static final String STRSPLIT = "^";

	@SuppressWarnings("unchecked")
	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、根据数据源信息和采集表信息抽取SQL" +
			"2、根据数据源信息和抽取SQL，执行SQL，获取")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	public static TableBean generateTableInfo(SourceDataConfBean sourceDataConfBean,
	                                          CollectTableBean collectTableBean) {
		TableBean tableBean = new TableBean();
		try {
			//1、根据数据源信息和采集表信息抽取SQL
			String collectSQL = getCollectSQL(sourceDataConfBean, collectTableBean);
			tableBean.setCollectSQL(collectSQL);
			ResultSet resultSet = getResultSet(sourceDataConfBean, collectSQL);
			StringBuilder columnMetaInfo = new StringBuilder();//生成的元信息列名
			StringBuilder allColumns = new StringBuilder();//要采集的列名
			StringBuilder colTypeMetaInfo = new StringBuilder();//生成的元信息列类型
			StringBuilder allType = new StringBuilder();//要采集的列类型
			StringBuilder colLengthInfo = new StringBuilder();//生成的元信息列长度
			/* Get result set metadata */
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();//获得列的数量
			int[] typeArray = new int[numberOfColumns];//列类型数组
			// Write header
			for (int i = 1; i <= numberOfColumns; i++) {
				String columnTmp = rsMetaData.getColumnName(i);
				int columnType = rsMetaData.getColumnType(i);
				//TODO 下一行未知
				if (!columnTmp.equalsIgnoreCase("hyren_rn")) {
					columnMetaInfo.append(columnTmp).append(STRSPLIT);
					allColumns.append(columnTmp.toUpperCase()).append(STRSPLIT);
				}
				typeArray[i - 1] = columnType;
			}
			//判断使用最后采集的sql去进行处理
			Map<String, String> tableColTypeAndLength = getTableColTypeAndLengthSql(resultSet);
			//直接遍历
			for (String key : tableColTypeAndLength.keySet()) {
				List<String> split = StringUtil.split(tableColTypeAndLength.get(key), "::");
				colTypeMetaInfo.append(split.get(0)).append(STRSPLIT);
				allType.append(split.get(0)).append(STRSPLIT);
				colLengthInfo.append(split.get(1)).append(STRSPLIT);
			}
			columnMetaInfo.deleteCharAt(columnMetaInfo.length() - 1);//元信息列名
			allColumns.deleteCharAt(allColumns.length() - 1);//列名
			colLengthInfo.deleteCharAt(colLengthInfo.length() - 1);//列长度
			colTypeMetaInfo.deleteCharAt(colTypeMetaInfo.length() - 1);//列类型
			allType.deleteCharAt(allType.length() - 1);//列类型
			//清洗配置
			Map<String, Object> parseJson = parseJson(collectTableBean);
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>) parseJson.get("splitIng");//字符拆分
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			//更新拆分和合并的列信息
			String colMeta = updateColumn(mergeIng, splitIng, columnMetaInfo, colTypeMetaInfo, colLengthInfo);
			columnMetaInfo.delete(0, columnMetaInfo.length()).append(colMeta);
			//这里是根据不同存储目的地会有相同的拉链方式，则这新增拉链字段在这里增加
			columnMetaInfo.append(STRSPLIT).append(Constant.SDATENAME);
			colTypeMetaInfo.append(STRSPLIT).append("char(8)");
			colLengthInfo.append(STRSPLIT).append("8");
			//增量进数方式
			if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
				columnMetaInfo.append(STRSPLIT).append(Constant.EDATENAME).append(STRSPLIT).append(Constant.MD5NAME);
				colTypeMetaInfo.append(STRSPLIT).append("char(8)").append(STRSPLIT).append("char(32)");
				colLengthInfo.append(STRSPLIT).append("8").append("32");
			}
			// 页面定义的清洗格式进行卸数
			tableBean.setAllColumns(allColumns.toString());
			tableBean.setAllType(allType.toString());
			tableBean.setColLengthInfo(colLengthInfo.toString());
			tableBean.setColTypeMetaInfo(colTypeMetaInfo.toString());
			tableBean.setColumnMetaInfo(columnMetaInfo.toString());
			tableBean.setTypeArray(typeArray);
			tableBean.setParseJson(parseJson);
		} catch (Exception e) {
			throw new AppSystemException("根据数据源信息和采集表信息得到卸数元信息失败！", e);
		}
		return tableBean;
	}

	private static ResultSet getResultSet(SourceDataConfBean sourceDataConfBean, String collectSQL) {
		//TODO 这里要不要加WHERE 1=2 处理，待定
		Connection conn = null;
		ResultSet columnSet;
		try {
			//获取jdbc连接
			conn = ConnUtil.getConnection(sourceDataConfBean.getDatabase_drive(), sourceDataConfBean.getJdbc_url(),
					sourceDataConfBean.getUser_name(), sourceDataConfBean.getDatabase_pad());
			PreparedStatement statement = conn.prepareStatement(collectSQL);
			columnSet = statement.executeQuery();
		} catch (Exception e) {
			throw new AppSystemException("获取ResultSet异常", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return columnSet;
	}

	private static String getCollectSQL(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		String tableName = collectTableBean.getTable_name();
		//筛选出不是新列的字段
		Set<String> collectColumnNames = ColumnTool.getCollectColumnName(
				collectTableBean.getCollectTableColumnBeanList());
		//3、根据列名和表名获得采集SQL为空
		//获取自定义sql，如果自定义sql的sql语句
		String collectSQL;
		//如果是自定义sql,则使用自定义sql
		if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_user_defined())) {
			collectSQL = collectTableBean.getSql();
		} else {
			// TODO 缺少支持自定义SQL
			collectSQL = SQLUtil.getCollectSQL(tableName, collectColumnNames,
					sourceDataConfBean.getDatabase_type());
			//这时自定义sql这个字段可能会有过滤条件，需要拼接到collectSQL后面
			String selfSql = collectTableBean.getSql();
			//获取系统的所有日期参数格式定义
			if (!StringUtil.isEmpty(selfSql)) {
				SimpleDateFormat sysDatef = new SimpleDateFormat(JobConstant.SYS_DATEFORMAT);
				String eltDate = collectTableBean.getEtlDate();
				//输入的日期肯定是yyyyMMdd格式
				if (selfSql.contains("#{txdate}")) {
					Date dateByString = getDateByString(eltDate, "yyyyMMdd");
					selfSql = selfSql.replace("#{txdate}", SINGLEQUOTE + sysDatef.format(dateByString) + SINGLEQUOTE);
				}
				if (selfSql.contains("#{txdate_pre}")) {
					Date dateByString = getDateByString(getNextDateByNum(eltDate, -1), "yyyyMMdd");
					selfSql = selfSql.replace("#{txdate_pre}", SINGLEQUOTE + sysDatef.format(dateByString) + SINGLEQUOTE);
				}
				if (selfSql.contains("#{txdate_next}")) {
					Date dateByString = getDateByString(getNextDateByNum(eltDate, 1), "yyyyMMdd");
					selfSql = selfSql.replace("#{txdate_next}", SINGLEQUOTE + sysDatef.format(dateByString) + SINGLEQUOTE);
				}
				collectSQL = collectSQL + " where " + selfSql;
			}
		}
		LOGGER.info("采集要执行的sql为" + collectSQL);
		return collectSQL;
	}

	/**
	 * TODO 这个方法和下面的方法要加到DateUtil里面
	 * 根据指定格式将字符串转为日期
	 */
	private static Date getDateByString(String dateByString, String pattern) {
		Date date;
		//为空返回当前日期
		if (dateByString == null || dateByString.trim().equals("")) {
			date = new Date();
		} else {
			try {
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				date = format.parse(dateByString);
			} catch (Exception e) {
				throw new AppSystemException("输入的日期格式不正确，请输入" + pattern + "格式的日期", e);
			}
		}
		return date;
	}

	/**
	 * 得到输入日期+i天以后的日期
	 */
	private static String getNextDateByNum(String s, int i) {

		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		java.util.Date date = simpledateformat.parse(s, new ParsePosition(0));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, i);
		date = calendar.getTime();
		s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 使用sql获取每个字段的长度，类型，精度
	 */
	private static Map<String, String> getTableColTypeAndLengthSql(ResultSet columnSet) {
		//定义map存放 类型和长度
		Map<String, String> map = new LinkedHashMap<>();
		try {
			//获取所有采集字段的Meta信息
			ResultSetMetaData rsMetaData = columnSet.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();//获得列的数量
			// Write header
			for (int i = 1; i <= numberOfColumns; i++) {
				map.put(rsMetaData.getColumnName(i).toLowerCase(), Platform.getColType(rsMetaData.getColumnType(i),
						rsMetaData.getColumnTypeName(i), rsMetaData.getPrecision(i), rsMetaData.getScale(i))
						+ "::" + rsMetaData.getPrecision(i));
			}
		} catch (Exception e) {
			throw new AppSystemException("使用sql获取每个字段的长度，类型，精度失败", e);
		}
		return map;
	}

	private static Map<String, Object> parseJson(CollectTableBean collectTableBean) {
		Map<String, Object> all = new HashMap<>();
		Map<String, Map<String, String>> deleSpecialSpace = new HashMap<>();
		Map<String, String> strFilling = new HashMap<>();
		Map<String, String> strDateing = new HashMap<>();
		Map<String, Map<String, Column_split>> splitIng = new HashMap<>();
		Map<String, String> mergeIng = new LinkedHashMap<>();
		Map<String, String> codeIng = new HashMap<>();
		Map<String, String> Triming = new HashMap<>();
		Map<String, Map<Integer, String>> ordering = new HashMap<>();

		//在这里获取转义的逻辑（按任务清洗、按表清洗、按字段清洗）最终组成map传入
		List<CollectTableColumnBean> collectTableColumnBeanList = collectTableBean.getCollectTableColumnBeanList();
		if (!collectTableColumnBeanList.isEmpty()) {
			for (CollectTableColumnBean collectTableColumnBean : collectTableColumnBeanList) {
				String column_name_up = collectTableColumnBean.getColumn_name().toUpperCase();
				//替换
				Map<String, String> replaceMap = new HashMap<>();
				//获取字段的清洗顺序
				String order = collectTableColumnBean.getTc_or();
				Map<Integer, String> changeKeyValue = changeKeyValue(order);
				ordering.put(column_name_up, changeKeyValue);
				//获取字段的清洗规则，如果字段清洗规则表不为空
				List<ColumnCleanBean> column_clean_list = collectTableColumnBean.getColumnCleanBeanList();
				if (!column_clean_list.isEmpty()) {
					for (ColumnCleanBean columnCleanBean : column_clean_list) {
						//字符替换
						if (CleanType.ZiFuTiHuan.getCode().equals(columnCleanBean.getClean_type())) {
							replaceMap.put(columnCleanBean.getField(), columnCleanBean.getReplace_feild());
							deleSpecialSpace.put(column_name_up, replaceMap);
							//列补齐
						} else if (CleanType.ZiFuBuQi.getCode().equals(columnCleanBean.getClean_type())) {
							String filling = columnCleanBean.getFilling_length() + STRSPLIT + columnCleanBean.
									getFilling_type() + STRSPLIT + columnCleanBean.getCharacter_filling();
							strFilling.put(column_name_up, filling);
							//日期转换
						} else if (CleanType.ShiJianZhuanHuan.getCode().equals(columnCleanBean.getClean_type())) {
							String dateConvert = columnCleanBean.getConvert_format() + STRSPLIT
									+ columnCleanBean.getOld_format();
							strDateing.put(column_name_up, dateConvert);
							//字符去空
						} else if (CleanType.ZiFuTrim.getCode().equals(columnCleanBean.getClean_type())) {
							Triming.put(column_name_up, "trim");
						} else if (CleanType.MaZhiZhuanHuan.getCode().equals(columnCleanBean.getClean_type())) {
							codeIng.put(column_name_up, columnCleanBean.getCodeTransform());
						} else if (CleanType.ZiFuChaiFen.getCode().equals(columnCleanBean.getClean_type())) {
							Map<String, Column_split> map = new HashMap<>();
							List<Column_split> column_split_list = columnCleanBean.getColumn_split_list();
							for (Column_split column_split : column_split_list) {
								map.put(column_split.getCol_name(), column_split);
							}
							splitIng.put(column_name_up, map);
						} else {
							throw new AppSystemException("请选择正确的清洗方式");
						}
					}
				}
			}
		}
		//列合并
		List<Column_merge> column_merge_list = collectTableBean.getColumn_merge_list();
		if (column_merge_list != null && column_merge_list.size() > 0) {
			for (Column_merge column_merge : column_merge_list) {
				mergeIng.put(column_merge.getCol_name() + STRSPLIT + column_merge.getCol_type(),
						column_merge.getOld_name());
			}
		}
		all.put("deleSpecialSpace", deleSpecialSpace);
		all.put("strFilling", strFilling);
		all.put("dating", strDateing);
		all.put("splitIng", splitIng);
		all.put("mergeIng", mergeIng);
		all.put("codeIng", codeIng);
		all.put("Triming", Triming);
		all.put("ordering", ordering);
		return all;
	}

	private static Map<Integer, String> changeKeyValue(String order) {

		Map<Integer, String> map = new HashMap<>();
		if (!StringUtil.isEmpty(order)) {
			JSONObject jsonOrder = JSONObject.parseObject(order);
			Set<String> jsonSet = jsonOrder.keySet();
			for (String key : jsonSet) {
				map.put(jsonOrder.getIntValue(key), key);
			}
		}
		return map;

	}

	/**
	 * 更新因为合并字段或者字段拆分而生成新字段的数据meta信息
	 *
	 * @param mergeIng 合并字段信息
	 * @param splitIng 拆分字段信息
	 * @param columns  被更新的列
	 * @param colType  对应的类型
	 * @param lengths  对应的类型的长度
	 * @return 更新后的信息
	 */
	private static String updateColumn(Map<String, String> mergeIng, Map<String, Map<String, Column_split>> splitIng,
	                                   StringBuilder columns, StringBuilder colType, StringBuilder lengths) {
		if (!mergeIng.isEmpty()) {
			for (String key : mergeIng.keySet()) {
				//获取表名和类型
				List<String> split = StringUtil.split(key, STRSPLIT);
				columns.append(CollectTableHandleParse.STRSPLIT).append(split.get(0));
				colType.append(CollectTableHandleParse.STRSPLIT).append(split.get(1));
				lengths.append(CollectTableHandleParse.STRSPLIT).append(Xls2xml.getLength(split.get(1)));
			}
		}
		String columnMate = columns.toString();
		if (!splitIng.isEmpty()) {
			for (String key : splitIng.keySet()) {
				StringBuilder newColumn = new StringBuilder();
				StringBuilder newColumnType = new StringBuilder();
				StringBuilder newColumnLength = new StringBuilder();
				Map<String, Column_split> map = splitIng.get(key);
				if (map != null) {
					//默认保留原字段
					newColumn.append(key).append(CollectTableHandleParse.STRSPLIT);
					//找到列所在分隔符位置
					int findColIndex = ColumnTool.findColIndex(columnMate, key, CollectTableHandleParse.STRSPLIT);
					for (String newName : map.keySet()) {
						//获取表名和类型
						Column_split column_split = map.get(newName);
						newColumn.append(newName).append(CollectTableHandleParse.STRSPLIT);
						newColumnType.append(column_split.getCol_type()).append(CollectTableHandleParse.STRSPLIT);
						newColumnLength.append(Xls2xml.getLength(column_split.getCol_type())).append(CollectTableHandleParse.STRSPLIT);
					}
					newColumn.deleteCharAt(newColumn.length() - 1);
					newColumnType.deleteCharAt(newColumnType.length() - 1);
					newColumnLength.deleteCharAt(newColumnLength.length() - 1);
					//获取对应列类型的位置插入拆分后的列类型
					int searchIndex = ColumnTool.searchIndex(colType.toString(), findColIndex, CollectTableHandleParse.STRSPLIT);
					int lenIndex = ColumnTool.searchIndex(lengths.toString(), findColIndex, CollectTableHandleParse.STRSPLIT);
					//	Debug.info(logger, "searchIndex"+searchIndex);
					//	Debug.info(logger, "colType"+colType.length());
					//插入新加的类型
					if (searchIndex != -1) {
						colType.insert(searchIndex, CollectTableHandleParse.STRSPLIT + newColumnType.toString());
					} else {
						//拆分的为表的最后一个字段colType执行insert改为直接追加
						//	Debug.info(logger, "我是最后一个字段做拆分啊");
						colType.append(CollectTableHandleParse.STRSPLIT).append(newColumnType.toString());
					}
					if (lenIndex > 0) {
						lengths.insert(lenIndex, CollectTableHandleParse.STRSPLIT + newColumnLength.toString());
					}
					columnMate = StringUtil.replace(columnMate.toUpperCase(), key.toUpperCase(),
							newColumn.toString().toUpperCase());
				}
			}
		}
		return columnMate;
	}

}
