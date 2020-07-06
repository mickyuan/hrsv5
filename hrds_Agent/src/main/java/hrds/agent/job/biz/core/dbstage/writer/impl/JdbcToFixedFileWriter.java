package hrds.agent.job.biz.core.dbstage.writer.impl;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dbstage.writer.AbstractFileWriter;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.agent.job.biz.utils.WriterFile;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
	private static final Log log = LogFactory.getLog(JdbcToFixedFileWriter.class);

	public JdbcToFixedFileWriter(ResultSet resultSet, CollectTableBean collectTableBean, int pageNum,
	                             TableBean tableBean, Data_extraction_def data_extraction_def) {
		super(resultSet, collectTableBean, pageNum, tableBean, data_extraction_def);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String writeFiles() {
		//获取行分隔符
		String database_separatorr = data_extraction_def.getDatabase_separatorr();
		String eltDate = collectTableBean.getEtlDate();
		StringBuilder fileInfo = new StringBuilder(1024);
		String hbase_name = collectTableBean.getHbase_name();
		//数据抽取指定的目录
		String plane_url = data_extraction_def.getPlane_url();
		String midName = plane_url + File.separator + eltDate + File.separator + collectTableBean.getTable_name()
				+ File.separator + Constant.fileFormatMap.get(FileFormat.DingChang.getCode()) + File.separator;
		midName = FileNameUtils.normalize(midName, true);
		DataFileWriter<Object> avroWriter = null;
		BufferedWriter writer;
		long counter = 0;
		int index = 0;
		WriterFile writerFile = null;
		try {
			//定长情况下是数据抽取的文件编码
			String database_code = data_extraction_def.getDatabase_code();
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//卸数文件名为hbase_name加线程唯一标识加此线程创建文件下标
			String fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
			fileInfo.append(fileName).append(Constant.METAINFOSPLIT);
			writerFile = new WriterFile(fileName);
			writer = writerFile.getBufferedWriter(DataBaseCode.ofValueByCode(database_code));
			//获取所有字段的名称，包括列分割和列合并出来的字段名称 写表头
			writeHeader(writer, tableBean.getColumnMetaInfo());
			//清洗配置
			final DataCleanInterface allclean = CleanFactory.getInstance().getObjectClean("clean_database");
			//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
			List<String> selectColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allclean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			StringBuilder sb = new StringBuilder();//用来写一行数据
			StringBuilder sb_ = new StringBuilder();//用来写临时数据
			List<String> typeList = StringUtil.split(tableBean.getAllType(), Constant.METAINFOSPLIT);
			int numberOfColumns = selectColumnList.size();
			log.info("type : " + typeList.size() + "  colName " + numberOfColumns);
			String currValue;
			int[] typeArray = tableBean.getTypeArray();
			while (resultSet.next()) {
				// Count it
				counter++;
				//获取所有列的值用来生成MD5值
				midStringOther.delete(0, midStringOther.length());
				// Write columns
				for (int i = 0; i < numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					//定长的分隔符可能为空，为了列合并取值，这里MD5值默认拼接commons里面的常量
					midStringOther.append(getOneColumnValue(avroWriter, counter, pageNum, resultSet, typeArray[i]
							, sb_, selectColumnList.get(i), hbase_name, midName));
					// Add DELIMITER if not last value
					if (i < numberOfColumns - 1) {
						midStringOther.append(Constant.DATADELIMITER);
					}
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, selectColumnList.get(i).toUpperCase(), null,
							typeList.get(i), FileFormat.DingChang.getCode(), null,
							database_code, database_separatorr);
					if (splitIng.get(selectColumnList.get(i).toUpperCase()) == null
							|| splitIng.get(selectColumnList.get(i).toUpperCase()).size() == 0) {
						sb.append(columnToFixed(currValue, TypeTransLength.getLength(
								typeList.get(i)), database_code)).append(database_separatorr);
					} else {
						sb.append(currValue).append(database_separatorr);
					}

				}
				if (!mergeIng.isEmpty()) {
					List<String> arrColString = StringUtil.split(midStringOther.toString(),
							Constant.DATADELIMITER);
					String merge = allclean.merge(mergeIng, arrColString.toArray(new String[0]),
							selectColumnList.toArray(new String[0]), null, null,
							FileFormat.DingChang.getCode(), database_code, database_separatorr);
					sb.append(merge).append(database_separatorr);
				}
				sb.append(eltDate);
				//根据是否算MD5判断是否追加结束日期和MD5两个字段
				if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5())) {
					String md5 = toMD5(midStringOther.toString());
					sb.append(database_separatorr).append(Constant.MAXDATE).
							append(database_separatorr).append(md5);
				}
				//拼接操作时间、操作日期、操作人
				appendOperateInfo(sb, database_separatorr);
				sb.append(data_extraction_def.getRow_separator());
				if (JobConstant.WriteMultipleFiles) {
					long messageSize = sb.toString().length();
					long singleFileSize = new File(fileName).length();
					if (singleFileSize + messageSize > JobConstant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						writerFile.bufferedWriterClose();
						index++;
						fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
						writerFile = new WriterFile(fileName);
						writer = writerFile.getBufferedWriter(DataBaseCode.ofValueByCode(
								database_code));
						//获取所有字段的名称，包括列分割和列合并出来的字段名称 写表头
						writeHeader(writer, tableBean.getColumnMetaInfo());
						fileInfo.append(fileName).append(Constant.METAINFOSPLIT);
					}
				}
				writer.write(sb.toString());
				if (counter % JobConstant.BUFFER_ROW == 0) {
					log.info("正在写入文件，已写入" + counter + "行");
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
		fileInfo.append(counter);
		//返回卸数一个或者多个文件名全路径和总的文件行数
		return fileInfo.toString();
	}

	/**
	 * 根据页面传过来的参数，决定是否写表头
	 *
	 * @param writer         定长的写文件的输出流
	 * @param columnMetaInfo 所有字段的列
	 */
	private void writeHeader(BufferedWriter writer, String columnMetaInfo) throws Exception {
		if (IsFlag.Shi.getCode().equals(data_extraction_def.getIs_header())) {
			if (!StringUtil.isEmpty(data_extraction_def.getDatabase_separatorr())) {
				columnMetaInfo = StringUtil.replace(columnMetaInfo, Constant.METAINFOSPLIT,
						data_extraction_def.getDatabase_separatorr());
			} else {
				columnMetaInfo = StringUtil.replace(columnMetaInfo, Constant.METAINFOSPLIT, ",");
			}
			writer.write(columnMetaInfo);
			writer.write(data_extraction_def.getRow_separator());
		}
	}

	/**
	 * 添加操作日期、操作时间、操作人
	 */
	private void appendOperateInfo(StringBuilder sb, String database_separatorr) {
		if (JobConstant.ISADDOPERATEINFO) {
			sb.append(database_separatorr).append(operateDate).append(database_separatorr)
					.append(operateTime).append(database_separatorr).append(user_id);
		}
	}

}
