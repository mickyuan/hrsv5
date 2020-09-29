package hrds.g.biz.serviceuser.query;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import hrds.g.biz.enumerate.StateType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryByRowkey {

	private static final Logger logger = LogManager.getLogger();

	public static Map<String, Object> query(String en_table, String rowkey, String en_column, String versions,
	                                        String dsl_name, String platform, String prncipal_name,
	                                        String hadoop_user_name) {
		Map<String, Object> responseMap = new HashMap<>();
		Object message;
		if (StringUtil.isBlank(en_table) || StringUtil.isBlank(rowkey)) {
			responseMap = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.name(),
					"表名或者rowkey不能为空");
		} else {
			try (HBaseHelper helper = HBaseHelper.getHelper(ConfigReader.getConfiguration(
					FileNameUtils.normalize(
							Constant.STORECONFIGPATH + dsl_name + File.separator, true),
					platform, prncipal_name, hadoop_user_name)); Table table = helper.getTable(en_table)) {
				Get get = new Get(rowkey.getBytes());
				//自定义搜索方式
				modifyGet(get, en_column, versions);
				Result result = table.get(get);
				List<Cell> cellList = result.listCells();
				if (cellList != null && cellList.size() > 0) {
					for (Cell cell : cellList) {
						responseMap.put(Bytes.toString(CellUtil.cloneQualifier(cell)).toLowerCase(),
								Bytes.toString(CellUtil.cloneValue(cell)));
					}
					List<Map<String, Object>> jsonList = new ArrayList<>();
					jsonList.add(responseMap);
					message = jsonList;
				} else {
					message = new ArrayList<>();
				}
				responseMap = StateType.getResponseInfo(StateType.NORMAL.name(), message);
			} catch (TableNotFoundException tableNotFoundException) {
				responseMap = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.name(),
						"TableNotFoundException: " + en_table);
				logger.error(tableNotFoundException);
			} catch (Exception e) {
				responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(), e.getMessage());
				logger.error(e);
			}
		}
		return responseMap;
	}

	/**
	 * @param get      hbase的get对象
	 * @param columns  （以'${列族}:'作为前缀）列，多列以|隔开
	 * @param versions 数据版本号
	 * @throws NumberFormatException
	 * @author yuanqi E-mail: 384077767@qq.com
	 * @date 创建时间：2019年4月4日 下午4:25:24
	 * @since jdk 1.8
	 */
	private static void modifyGet(Get get, String columns, String versions) throws NumberFormatException,
			IOException {

		// set timestamp
		if (StringUtil.isNotBlank(versions)) {
			get.setTimeStamp(Long.parseLong(versions));
		}
		//
		if (StringUtil.isNotBlank(columns)) {
			String[] colArray = StringUtils.split(columns, '|');
			for (String colsWithCf : colArray) {
				String[] colWithCf = StringUtils.split(colsWithCf, ':');
				get.addColumn(colWithCf[0].getBytes(), colWithCf[1].getBytes());
			}
		}
	}

}
