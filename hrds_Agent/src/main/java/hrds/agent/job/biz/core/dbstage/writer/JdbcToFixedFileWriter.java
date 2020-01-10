package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.agent.job.biz.utils.JobIoUtil;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.agent.job.biz.utils.WriterFile;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * JdbcToFixedFileWriter
 * date: 2019/12/6 17:21
 * author: zxz
 */
public class JdbcToFixedFileWriter extends AbstractFileWriter {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcToNonFixedFileWriter.class);

	@SuppressWarnings("unchecked")
	@Override
	public String writeFiles(ResultSet resultSet, CollectTableBean collectTableBean, long pageNum,
	                         long pageRow, TableBean tableBean) {
		//获取行分隔符
		String database_separatorr = collectTableBean.getDatabase_separatorr();
		String eltDate = collectTableBean.getEtlDate();
		StringBuilder fileInfo = new StringBuilder(1024);
		String hbase_name = collectTableBean.getHbase_name();
		String midName = Constant.JDBCUNLOADFOLDER + collectTableBean.getDatabase_id() + File.separator
				+ collectTableBean.getTable_id() + File.separator;
		midName = FileNameUtils.normalize(midName, true);
		DataFileWriter<Object> avroWriter = null;
		BufferedWriter writer;
		long lineCounter = pageNum * pageRow;
		long counter = 0;
		int index = 0;
		WriterFile writerFile = null;
		try {
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//卸数文件名为hbase_name加线程唯一标识加此线程创建文件下标
			String fileName = midName + hbase_name + pageNum + index + ".part";
			fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
			writerFile = new WriterFile(fileName);
			writer = writerFile.getBufferedWriter();
			//清洗配置
			final DataCleanInterface allclean = CleanFactory.getInstance().getObjectClean("clean_database");
			//获取所有字段的名称，包括列分割和列合并出来的字段名称
			List<String> allColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
			//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
			List<String> selectColumnList = StringUtil.split(tableBean.getAllColumns(), CollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allclean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			StringBuilder sb = new StringBuilder();//用来写一行数据
			StringBuilder sb_ = new StringBuilder();//用来写临时数据
			List<String> typeList = StringUtil.split(tableBean.getAllType(), CollectTableHandleParse.STRSPLIT);
			int numberOfColumns = selectColumnList.size();
			log.info("type : " + typeList.size() + "  colName " + numberOfColumns);
			String currValue;
			int[] typeArray = tableBean.getTypeArray();
			//定长情况下是数据抽取的文件编码
			String database_code = collectTableBean.getDatabase_code();
			while (resultSet.next()) {
				// Count it
				lineCounter++;
				counter++;
				//获取所有列的值用来生成MD5值
				midStringOther.delete(0, midStringOther.length());
				// Write columns
				for (int i = 1; i <= numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					midStringOther.append(getOneColumnValue(avroWriter, lineCounter, resultSet, typeArray[i - 1]
							, sb_, i, hbase_name)).append(database_separatorr);
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, selectColumnList.get(i - 1).toUpperCase(), null,
							typeList.get(i - 1), FileFormat.DingChang.getCode(), null,
							database_code, database_separatorr);
					if (splitIng.get(selectColumnList.get(i - 1).toUpperCase()) == null
							|| splitIng.get(selectColumnList.get(i - 1).toUpperCase()).size() == 0) {
						//TODO 下面这一行可以优化，TypeTransLength.getLength(typeList.get(i - 1))提出到循环外面
						sb.append(columnToFixed(currValue, TypeTransLength.getLength(
								typeList.get(i - 1)), database_code)).append(database_separatorr);
					} else {
						sb.append(currValue).append(database_separatorr);
					}

				}
				//TODO 定长目前不支持列合并，原因同上
				if (!mergeIng.isEmpty()) {
					List<String> arrColString = StringUtil.split(midStringOther.toString(),
							database_separatorr);
					String merge = allclean.merge(mergeIng, arrColString.toArray(new String[0]),
							allColumnList.toArray(new String[0]), null, null,
							FileFormat.DingChang.getCode(), database_code, database_separatorr);
					sb.append(merge).append(database_separatorr);
				}
				sb.append(eltDate);
				if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
					String md5 = toMD5(midStringOther.toString());
					sb.append(database_separatorr).append(Constant.MAXDATE).
							append(database_separatorr).append(md5);
				}
				sb.append('\n');
				if (JobConstant.WriteMultipleFiles) {
					long messageSize = sb.toString().length();
					long singleFileSize = new File(fileName).length();
					if (singleFileSize + messageSize > JobConstant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						writerFile.bufferedWriterClose();
						index++;
						fileName = midName + hbase_name + pageNum + index + ".part";
						writerFile = new WriterFile(fileName);
						writer = writerFile.getBufferedWriter();
						fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
					}
				}
				writer.write(sb.toString());
				if (lineCounter % 50000 == 0) {
					writer.flush();
				}
				sb.delete(0, sb.length());
			}
			writer.flush();
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数定长文件失败", e);
		} finally {
			try {
				if (writerFile != null)
					writerFile.bufferedWriterClose();
				if (avroWriter != null)
					avroWriter.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		fileInfo.append(counter).append(CollectTableHandleParse.STRSPLIT).append(JobIoUtil.getFileSize(midName));
		//返回卸数一个或者多个文件名全路径和总的文件行数和文件大小
		return fileInfo.toString();
	}

	/**
	 * 字段变为定长
	 */
	public static String columnToFixed(String columnValue, int length, String database_code) {
		StringBuilder sb;
		try {
			byte[] bytes = columnValue.getBytes(DataBaseCode.ofValueByCode(database_code));
			int columnValueLength = bytes.length;
			sb = new StringBuilder();
			sb.append(columnValue);
			if (length > columnValueLength) {
				for (int j = 0; j < length - columnValueLength; j++) {
					sb.append(" ");
				}
			} else {
				throw new AppSystemException("定长指定的长度小于源数据长度");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new AppSystemException("定长文件卸数编码读取错误", e);
		}
	}

//	/**
//	 * 获取字段的长度
//	 */
//	private int getColumnLength(ResultSetMetaData rsMetaData, int i) throws SQLException {
//		int length = rsMetaData.getPrecision(i);
//		String columnType = rsMetaData.getColumnTypeName(i).toUpperCase();
//		if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC")) {
//			length = length + 2;
//		}
//		JSONObject jsonTypeObj;
//		if (length <= 0) {
//			//TODO 待讨论，这个需要从哪里取值
//			String jsonType = PropertyParaUtil.getString("fixedType", "");
//			jsonTypeObj = JSON.parseObject(jsonType);
//			String lengthValue = jsonTypeObj.getString(columnType);
//			try {
//				length = Integer.parseInt(lengthValue);
//			} catch (NumberFormatException e) {
//				log.error("读取类型数据库类型错误 " + columnType, e);
//			}
//		}
//		return length;
//	}
}
