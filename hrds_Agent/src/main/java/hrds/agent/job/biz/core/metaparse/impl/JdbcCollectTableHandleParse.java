package hrds.agent.job.biz.core.metaparse.impl;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.CollectTableColumnBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.metaparse.AbstractCollectTableHandle;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UnloadType;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@DocClass(desc = "根据页面所选的表和字段对jdbc所返回的meta信息进行解析", author = "zxz", createdate = "2019/12/4 11:17")
public class JdbcCollectTableHandleParse extends AbstractCollectTableHandle {


	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、判断是增量抽取还是全量抽取" +
			"2、调用对应方法获取卸数元信息")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	public TableBean generateTableInfo(SourceDataConfBean sourceDataConfBean,
	                                   CollectTableBean collectTableBean) {
		if (UnloadType.QuanLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
			//全量卸数，根据采集的sql获取meta信息。
			return getFullAmountExtractTableBean(sourceDataConfBean, collectTableBean);
		} else if (UnloadType.ZengLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
			//增量卸数，根据table_column选择的字段获取meta信息
			return getIncrementExtractTableBean(sourceDataConfBean, collectTableBean);
		} else {
			throw new AppSystemException("数据库抽取方式参数不正确");
		}
	}

	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、根据采集表信息选择的列获取卸数元信息")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	private TableBean getIncrementExtractTableBean(SourceDataConfBean sourceDataConfBean,
	                                               CollectTableBean collectTableBean) {
		TableBean tableBean = new TableBean();
		//-----------------------------------获取所有列的数据字典信息----------------------------------
		StringBuilder columnMetaInfo = new StringBuilder();//生成的元信息列名
		StringBuilder allColumns = new StringBuilder();//要采集的列名
		StringBuilder colTypeMetaInfo = new StringBuilder();//生成的元信息列类型
		StringBuilder allType = new StringBuilder();//要采集的列类型
		StringBuilder colLengthInfo = new StringBuilder();//生成的元信息列长度
		StringBuilder primaryKeyInfo = new StringBuilder();//字段是否为主键
		List<CollectTableColumnBean> collectTableColumnBeanList = collectTableBean.getCollectTableColumnBeanList();
		for (CollectTableColumnBean column : collectTableColumnBeanList) {
			columnMetaInfo.append(column.getColumn_name()).append(STRSPLIT);
			allColumns.append(column.getColumn_name()).append(STRSPLIT);
			colTypeMetaInfo.append(column.getColumn_type()).append(STRSPLIT);
			allType.append(column.getColumn_type()).append(STRSPLIT);
			colLengthInfo.append(TypeTransLength.getLength(column.getColumn_type())).append(STRSPLIT);
			primaryKeyInfo.append(column.getIs_primary_key()).append(STRSPLIT);
		}
		columnMetaInfo.deleteCharAt(columnMetaInfo.length() - 1);//元信息列名
		allColumns.deleteCharAt(allColumns.length() - 1);//列名
		colLengthInfo.deleteCharAt(colLengthInfo.length() - 1);//列长度
		colTypeMetaInfo.deleteCharAt(colTypeMetaInfo.length() - 1);//列类型
		allType.deleteCharAt(allType.length() - 1);//列类型
		primaryKeyInfo.deleteCharAt(primaryKeyInfo.length() - 1);//是否主键
		// 页面定义的清洗格式进行卸数
		tableBean.setAllColumns(allColumns.toString());
		tableBean.setAllType(allType.toString());
		tableBean.setColLengthInfo(colLengthInfo.toString());
		tableBean.setColTypeMetaInfo(colTypeMetaInfo.toString());
		tableBean.setColumnMetaInfo(columnMetaInfo.toString().toUpperCase());
		tableBean.setPrimaryKeyInfo(primaryKeyInfo.toString());
		//-----------------------------------所有列的数据字典信息获取结束----------------------------------
		//获取查询的列
		getSqlSearchColumn(sourceDataConfBean, collectTableBean, tableBean);
		return tableBean;
	}

	private void getSqlSearchColumn(SourceDataConfBean sourceDataConfBean,
	                                CollectTableBean collectTableBean, TableBean tableBean) {
		//根据实际sql获取新增、删除、更新查询的列
		ResultSet resultSet = null;
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(sourceDataConfBean.getDatabase_drive(),
				sourceDataConfBean.getJdbc_url(), sourceDataConfBean.getUser_name(),
				sourceDataConfBean.getDatabase_pad(), sourceDataConfBean.getDatabase_type())) {
			String incrementSql = collectTableBean.getSql();
			JSONObject incrementSqlObject = JSONObject.parseObject(incrementSql);
			//遍历json根据json的key执行sql,拼接对应的操作方式,增量抽取是写到同一个文件，因此这里不使用多线程
			for (String key : incrementSqlObject.keySet()) {
				//获取增量的sql
				String sql = incrementSqlObject.getString(key);
				if (!StringUtil.isEmpty(sql)) {
					StringBuilder columnMetaInfo = new StringBuilder();
					//替换掉sql中需要传递的参数
					sql = AbstractCollectTableHandle.replaceSqlParam(sql, collectTableBean.getSqlParam());
					resultSet = getResultSet(sql, db);
					ResultSetMetaData rsMetaData = resultSet.getMetaData();
					// Write header
					for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
						String columnTmp = rsMetaData.getColumnName(i);
						//TODO 下一行未知
						if (!columnTmp.equalsIgnoreCase("hyren_rn")) {
							columnMetaInfo.append(columnTmp).append(STRSPLIT);
						}
					}
					columnMetaInfo.deleteCharAt(columnMetaInfo.length() - 1);//元信息列名
					//根据key，给tableBean塞值
					if ("insert".equals(key)) {
						tableBean.setInsertColumnInfo(columnMetaInfo.toString());
					} else if ("delete".equals(key)) {
						tableBean.setDeleteColumnInfo(columnMetaInfo.toString());
					} else if ("update".equals(key)) {
						tableBean.setUpdateColumnInfo(columnMetaInfo.toString());
					} else {
						throw new AppSystemException("增量数据采集不自持" + key + "操作");
					}
				}
			}
		} catch (Exception e) {
			throw new AppSystemException("根据数据源信息和采集表信息得到卸数元信息失败！", e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、根据数据源信息和采集表信息抽取SQL" +
			"2、根据数据源信息和抽取SQL，执行SQL，获取")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	@SuppressWarnings("unchecked")
	private TableBean getFullAmountExtractTableBean(SourceDataConfBean sourceDataConfBean,
	                                                CollectTableBean collectTableBean) {
		TableBean tableBean = new TableBean();
		ResultSet resultSet = null;
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(sourceDataConfBean.getDatabase_drive(),
				sourceDataConfBean.getJdbc_url(), sourceDataConfBean.getUser_name(),
				sourceDataConfBean.getDatabase_pad(), sourceDataConfBean.getDatabase_type(),
				sourceDataConfBean.getDatabase_name())) {
			//1、根据数据源信息和采集表信息获取数据库抽取SQL
			String collectSQL = getCollectSQL(collectTableBean, db, sourceDataConfBean.getDatabase_name());
			tableBean.setCollectSQL(collectSQL);
			//抽取sql可能包含分隔符，判断如果包含分隔符，取第一条sql获取meta信息（注：包含分隔符表明并行抽取）
			if (collectSQL.contains(Constant.SQLDELIMITER)) {
				resultSet = getResultSet(StringUtil.split(collectSQL, Constant.SQLDELIMITER).get(0), db);
			} else {
				resultSet = getResultSet(collectSQL, db);
			}
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
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");//字符拆分
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			//更新拆分和合并的列信息
			String colMeta = updateColumn(mergeIng, splitIng, columnMetaInfo, colTypeMetaInfo, colLengthInfo);
			columnMetaInfo.delete(0, columnMetaInfo.length()).append(colMeta);
			//默认拼一列字段海云开始日期（跑批日期）
			columnMetaInfo.append(STRSPLIT).append(Constant.SDATENAME);
			colTypeMetaInfo.append(STRSPLIT).append("char(8)");
			colLengthInfo.append(STRSPLIT).append("8");
			//根据是否算MD5判断是否追加结束日期和MD5两个字段
			if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5())) {
				columnMetaInfo.append(STRSPLIT).append(Constant.EDATENAME).append(STRSPLIT).append(Constant.MD5NAME);
				colTypeMetaInfo.append(STRSPLIT).append("char(8)").append(STRSPLIT).append("char(32)");
				colLengthInfo.append(STRSPLIT).append("8").append(STRSPLIT).append("32");
			}
			//根据字段名称和页面选择的信息，判断是否为主键
			StringBuilder primaryKeyInfo = new StringBuilder();//字段是否为主键
			List<String> column_list = StringUtil.split(columnMetaInfo.toString(), STRSPLIT);
			List<CollectTableColumnBean> collectTableColumnBeanList = collectTableBean.getCollectTableColumnBeanList();
			for (String col : column_list) {
				boolean flag = true;
				for (CollectTableColumnBean columnBean : collectTableColumnBeanList) {
					if (columnBean.getColumn_name().equals(col)) {
						primaryKeyInfo.append(columnBean.getIs_primary_key()).append(STRSPLIT);
						flag = false;
						break;
					}
				}
				if (flag) {
					primaryKeyInfo.append(IsFlag.Fou.getCode()).append(STRSPLIT);
				}
			}
			primaryKeyInfo.deleteCharAt(primaryKeyInfo.length() - 1);//主键
			// 页面定义的清洗格式进行卸数
			tableBean.setAllColumns(allColumns.toString());
			tableBean.setAllType(allType.toString());
			tableBean.setColLengthInfo(colLengthInfo.toString());
			tableBean.setColTypeMetaInfo(colTypeMetaInfo.toString());
			tableBean.setColumnMetaInfo(columnMetaInfo.toString().toUpperCase());
			tableBean.setTypeArray(typeArray);
			tableBean.setParseJson(parseJson);
			tableBean.setPrimaryKeyInfo(primaryKeyInfo.toString());
		} catch (Exception e) {
			throw new AppSystemException("根据数据源信息和采集表信息得到卸数元信息失败！", e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableBean;
	}
}
