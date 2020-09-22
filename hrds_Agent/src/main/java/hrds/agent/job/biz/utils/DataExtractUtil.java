package hrds.agent.job.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataExtractUtil {

	//打印日志
	private static final Logger logger = LogManager.getLogger();
	private static final String DATADICTIONARY_SUFFIX = ".json";

	/**
	 * 生成数据字典
	 */
	public static void writeDataDictionary(String dictionaryPath, String tableName, String allColumns
			, String allType, List<Data_extraction_def> ext_defList, String unload_type, String primaryKeyInfo
			, String insertColumnInfo, String updateColumnInfo, String deleteColumnInfo, String hbase_name,
										   String task_name) {
		RandomAccessFile randomAccessFile = null;
		FileChannel fileChannel = null;
		FileLock fileLock = null;
		try {
			//给该文件加锁
			randomAccessFile = new RandomAccessFile(new File(dictionaryPath + task_name +
					DATADICTIONARY_SUFFIX), "rw");
			fileChannel = randomAccessFile.getChannel();
			while (true) {
				try {
					//tryLock()是非阻塞式的，它设法获取锁，但如果不能获得，
					// 例如因为其他一些进程已经持有相同的锁，而且不共享时，它将直接从方法调用返回。
					fileLock = fileChannel.tryLock();
					break;
				} catch (Exception e) {
					logger.info("有其他线程正在操作该文件，当前线程休眠100毫秒");
					TimeUnit.MILLISECONDS.sleep(100);
				}
			}
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = randomAccessFile.readLine()) != null) {
				//数据字典的编码默认直接使用utf-8
				sb.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			}
			String dd_data = parseJsonDictionary(sb.toString().trim(), tableName, allColumns, allType, ext_defList,
					unload_type, primaryKeyInfo, insertColumnInfo, updateColumnInfo, deleteColumnInfo, hbase_name);
			//覆盖文件内容
			randomAccessFile.setLength(0);
			//数据字典的编码默认直接使用utf-8
			randomAccessFile.write(dd_data.getBytes(StandardCharsets.ISO_8859_1));
		} catch (Exception e) {
			throw new AppSystemException("使用独占锁读取数据字典失败", e);
		} finally {
			try {
				if (fileLock != null)
					fileLock.release();
				if (fileChannel != null)
					fileChannel.close();
				if (randomAccessFile != null)
					randomAccessFile.close();
			} catch (IOException e) {
				logger.error("读取数据字典关闭流失败", e);
			}
		}
	}

	/**
	 * 写信号文件
	 */
	public static synchronized void writeSignalFile(String midName, String tableName, String sqlQuery, StringBuilder
			allColumns, StringBuilder allType, StringBuilder lengths, String is_fixed_extract, String fixed_separator,
													long lineCounter, long collect_database_size,
													String eltDate, String charset) {
		BufferedWriter bufferOutputWriter = null;
		OutputStreamWriter outputFileWriter = null;
		String create_date = DateUtil.getSysDate();
		String create_time = DateUtil.getSysTime();
		String signalFile = midName + ".flg";
		String fileName = tableName + "_" + eltDate + ".flg";
		try {
			File file = new File(signalFile);
			outputFileWriter = new OutputStreamWriter(new FileOutputStream(file), charset);
			bufferOutputWriter = new BufferedWriter(outputFileWriter, 4096);
			StringBuilder sb = new StringBuilder();
			sb.append(fileName).append(" ").append(collect_database_size).append(" ").append(lineCounter)
					.append(" ").append(create_date).append(" ")
					.append(create_time).append("\n\n");
			sb.append("FILENAME=").append(fileName).append("\n\n");
			sb.append("FILESIZE=").append(collect_database_size).append("\n\n");
			sb.append("ROWCOUNT=").append(lineCounter).append("\n\n");
			sb.append("CREATEDATETIME=").append(create_date).append(" ").append(create_time).append("\n\n");
			if (FileFormat.DingChang.getCode().equals(is_fixed_extract)) {
				sb.append("IS_FIXED_LENGTH=").append("YES").append("\n\n");
			} else {
				sb.append("IS_FIXED_LENGTH=").append("NO").append("\n\n");
			}
			sb.append("SEPARATOR=").append(fixed_separator).append("\n\n");
			sb.append("SQL=").append(sqlQuery).append("\n\n");
			int RowLength = 0;
			List<String> cols_length = StringUtil.split(lengths.toString(), "^");
			for (String length : cols_length) {
				RowLength += Integer.parseInt(length);
			}
			sb.append("ROWLENGTH=").append(RowLength).append("\n\n");
			sb.append("COLUMNCOUNT=").append(cols_length.size()).append("\n\n");
			sb.append("COLUMNDESCRIPTION=").append("\n");
			for (int i = 0; i < cols_length.size(); i++) {
				List<String> columns = StringUtil.split(allColumns.toString(), "^");
				List<String> types = StringUtil.split(allType.toString(), "^");
				if (StringUtil.isEmpty(fixed_separator)) {
					int start = 0;
					int end;
					if (i > 0) {
						for (int j = 0; j < i; j++) {
							start += Integer.parseInt(cols_length.get(j));
						}
					}
					start = start + 1;
					end = start + Integer.parseInt(cols_length.get(i)) - 1;
					sb.append(i + 1).append("$$").append(columns.get(i)).append("$$").append(types.get(i)).
							append("$$").append("(").append(start).append(",").append(end).append(")").append("\n");
				} else {
					sb.append(i + 1).append("$$").append(columns.get(i)).append("$$").append(types.get(i)).append("\n");
				}
			}
			bufferOutputWriter.write(sb.toString() + "\n");
			bufferOutputWriter.flush();
		} catch (Exception e) {
			logger.error("写信号文件失败", e);
		} finally {
			try {
				if (bufferOutputWriter != null)
					bufferOutputWriter.close();
				if (outputFileWriter != null)
					outputFileWriter.close();
			} catch (IOException e) {
				logger.error("关闭流失败", e);
			}
		}
	}

	public static String parseJsonDictionary(String dd_data, String tableName, String allColumns
			, String allType, List<Data_extraction_def> ext_defList, String unload_type, String primaryKeyInfo
			, String insertColumnInfo, String updateColumnInfo, String deleteColumnInfo, String hbase_name) {
		JSONArray jsonArray = new JSONArray();
		if (!StringUtil.isEmpty(dd_data)) {
			jsonArray = JSONArray.parseArray(dd_data);
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.getString("table_name").equals(tableName)) {
				jsonArray.remove(jsonObject);
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("table_name", tableName);
		jsonObject.put("table_ch_name", tableName);
		jsonObject.put("unload_type", unload_type);

		jsonObject.put("insertColumnInfo", insertColumnInfo);
		jsonObject.put("updateColumnInfo", updateColumnInfo);
		jsonObject.put("deleteColumnInfo", deleteColumnInfo);
		JSONArray storageArray = new JSONArray();
		for (Data_extraction_def data_extraction_def : ext_defList) {
			JSONObject object = new JSONObject();
			object.put("is_header", data_extraction_def.getIs_header());
			object.put("dbfile_format", data_extraction_def.getDbfile_format());
			object.put("database_code", data_extraction_def.getDatabase_code());
			if (StringUtil.isEmpty(data_extraction_def.getFile_suffix())) {
				data_extraction_def.setFile_suffix("dat");
			}
			///home/hyshf/xccccccccccc/#{date}/#{table}/#{文件格式}/.*
			object.put("plane_url", data_extraction_def.getPlane_url() + File.separator + "#{date}" +
					File.separator + "#{table}" + File.separator + "#{file_format}" + File.separator
					+ hbase_name + ".*." + data_extraction_def.getFile_suffix());
			if (StringUtil.isEmpty(data_extraction_def.getRow_separator())) {
				object.put("row_separator", "");
			} else {
				object.put("row_separator", StringUtil.string2Unicode(data_extraction_def.getRow_separator()));
			}
			if (StringUtil.isEmpty(data_extraction_def.getDatabase_separatorr())) {
				object.put("database_separatorr", "");
			} else {
				object.put("database_separatorr", StringUtil.string2Unicode(data_extraction_def.getDatabase_separatorr()));
			}
			storageArray.add(object);
		}
		jsonObject.put("storage", storageArray);
		List<String> columnList = StringUtil.split(allColumns, Constant.METAINFOSPLIT);
		List<String> typeList = StringUtil.split(allType, Constant.METAINFOSPLIT);
		List<String> primaryKeyList = StringUtil.split(primaryKeyInfo, Constant.METAINFOSPLIT);
		List<JSONObject> array = new ArrayList<>();
		for (int i = 0; i < columnList.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("column_type", typeList.get(i));
			object.put("column_remark", "");
			object.put("column_ch_name", columnList.get(i));
			object.put("column_name", columnList.get(i));
			object.put("is_primary_key", primaryKeyList.get(i));
			object.put("is_get", IsFlag.Shi.getCode());
			object.put("is_alive", IsFlag.Shi.getCode());
			object.put("is_new", IsFlag.Fou.getCode());
			array.add(object);
		}
		jsonObject.put("columns", array);
		jsonArray.add(jsonObject);
		return jsonArray.toJSONString();
	}
}
