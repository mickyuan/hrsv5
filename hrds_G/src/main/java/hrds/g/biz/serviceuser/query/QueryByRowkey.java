package hrds.g.biz.serviceuser.query;

import fd.ng.core.utils.StringUtil;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryByRowkey implements Query {

	private static final Logger logger = LogManager.getLogger();
	//hbase表英文名
	private String cnTable = "";
	private String rowkey = "";
	//列英文名，可以以|分割
	private String cnColumn = "";
	//数据版本号，可以以|分割
	private String versions = "";

	private Map<String, Object> map;

	public QueryByRowkey(String cn_table, String rowkey, String cn_column, String versions) {
		super();
		this.cnTable = cn_table;
		this.rowkey = rowkey;
		this.cnColumn = cn_column;
		this.versions = versions;
	}

	@Override
	public Query query() {
		Map<String, Object> msg = new HashMap<>();
		Object message;
		if (StringUtil.isBlank(cnTable) || StringUtil.isBlank(rowkey)) {
			msg = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.getCode(),
					"表名或者rowkey不能为空");
		} else {
			try (HBaseHelper helper = HBaseHelper.getHelper(); Table table = helper.getTable(cnTable)) {
				Get get = new Get(rowkey.getBytes());
				//自定义搜索方式
				modifyGet(get, cnColumn, versions);
				Result result = table.get(get);
				List<Cell> cellList = result.listCells();
				if (cellList != null && cellList.size() > 0) {
					for (Cell cell : cellList) {
						msg.put(Bytes.toString(CellUtil.cloneQualifier(cell)).toLowerCase(),
								Bytes.toString(CellUtil.cloneValue(cell)));
					}
					List<Map<String, Object>> jsonList = new ArrayList<>();
					jsonList.add(msg);
					message = jsonList;
				} else {
					message = new ArrayList<>();
				}
				msg = StateType.getResponseInfo(StateType.NORMAL.getCode(), message);
			} catch (TableNotFoundException tableNotFoundException) {
				msg = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.getCode(),
						"TableNotFoundException: " + cnTable);
				logger.error(tableNotFoundException);
			} catch (Exception e) {
				msg = StateType.getResponseInfo(StateType.EXCEPTION.getCode(), e.getMessage());
				logger.error(e);
			}
		}
		this.map = msg;
		return this;
	}

	@Override
	public Map<String, Object> feedback() {
		return map;
	}

	/**
	 * @param get      hbase的get对象
	 * @param columns  （以'${列族}:'作为前缀）列，多列以|隔开
	 * @param versions 数据版本号
	 * @return
	 * @throws NumberFormatException
	 * @author yuanqi E-mail: 384077767@qq.com
	 * @date 创建时间：2019年4月4日 下午4:25:24
	 * @since jdk 1.8
	 */
	private Get modifyGet(Get get, String columns, String versions) throws NumberFormatException, IOException {

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
		return get;
	}

}
