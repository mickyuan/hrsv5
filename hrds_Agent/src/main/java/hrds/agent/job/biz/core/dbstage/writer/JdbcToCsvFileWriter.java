package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.agent.job.biz.utils.JobIoUtil;
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
import org.supercsv.io.CsvListWriter;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JdbcToCsvFileWriter
 * date: 2019/12/6 17:19
 * author: zxz
 */
public class JdbcToCsvFileWriter extends AbstractFileWriter {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcToCsvFileWriter.class);

	@SuppressWarnings("unchecked")
	@Override
	public String writeFiles(ResultSet resultSet, CollectTableBean collectTableBean, int pageNum,
	                         TableBean tableBean, Data_extraction_def data_extraction_def) {
		String eltDate = collectTableBean.getEtlDate();
		StringBuilder fileInfo = new StringBuilder(1024);
		String hbase_name = collectTableBean.getHbase_name();
		//数据抽取指定的目录
		String plane_url = data_extraction_def.getPlane_url();
		String midName = plane_url + File.separator + eltDate + File.separator + collectTableBean.getTable_name()
				+ File.separator + FileFormat.CSV.getValue() + File.separator;
		midName = FileNameUtils.normalize(midName, true);
		DataFileWriter<Object> avroWriter = null;
		CsvListWriter writer;
		long counter = 0;
		int index = 0;
		WriterFile writerFile = null;
		try {
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//卸数文件名为hbase_name加线程唯一标识加此线程创建文件下标
			String fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
			String dataDelimiter = data_extraction_def.getDatabase_separatorr();
			fileInfo.append(fileName).append(JdbcCollectTableHandleParse.STRSPLIT);
			writerFile = new WriterFile(fileName);
			writer = writerFile.getCsvWriter(DataBaseCode.ofValueByCode(data_extraction_def.getDatabase_code()));
			//清洗配置
			final DataCleanInterface allclean = CleanFactory.getInstance().getObjectClean("clean_database");
			//获取所有字段的名称，包括列分割和列合并出来的字段名称
			List<String> allColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
			//写表头
			if (IsFlag.Shi.getCode().equals(data_extraction_def.getIs_header())) {
				writer.write(allColumnList);
			}
			//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
			List<String> selectColumnList = StringUtil.split(tableBean.getAllColumns(), JdbcCollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			//字符合并
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allclean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			List<Object> sb = new ArrayList<>(StringUtil.split(tableBean.getColumnMetaInfo()
					, JdbcCollectTableHandleParse.STRSPLIT).size());//用来写一行数据
			StringBuilder sb_ = new StringBuilder();//用来写临时数据
			List<String> typeList = StringUtil.split(tableBean.getAllType(),
					JdbcCollectTableHandleParse.STRSPLIT);
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
				for (int i = 1; i <= numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					midStringOther.append(getOneColumnValue(avroWriter, counter, pageNum, resultSet, typeArray[i - 1],
							sb_, i, hbase_name));
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, selectColumnList.get(i - 1).toUpperCase(), null,
							typeList.get(i - 1), FileFormat.CSV.getCode(), sb,
							data_extraction_def.getDatabase_code(), dataDelimiter);
					// Add DELIMITER if not last value
					if (i < numberOfColumns) {
						midStringOther.append(JobConstant.DATADELIMITER);
					}
					if (splitIng.get(selectColumnList.get(i - 1).toUpperCase()) == null
							|| splitIng.get(selectColumnList.get(i - 1).toUpperCase()).size() == 0) {
						sb.add(currValue);
					}
				}
				//如果有列合并处理合并信息
				if (!mergeIng.isEmpty()) {
					List<String> arrColString = StringUtil.split(midStringOther.toString(),
							JobConstant.DATADELIMITER);
					allclean.merge(mergeIng, arrColString.toArray(new String[0]), allColumnList.toArray
									(new String[0]), null, sb,
							FileFormat.CSV.getCode(), data_extraction_def.getDatabase_code(), dataDelimiter);
				}
				sb.add(eltDate);
				//根据是否算MD5判断是否追加结束日期和MD5两个字段
				if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5())) {
					String md5 = toMD5(midStringOther.toString());
					sb.add(Constant.MAXDATE);
					sb.add(md5);
				}
				if (JobConstant.WriteMultipleFiles) {
					long messageSize = sb.toString().length();
					long singleFileSize = new File(fileName).length();
					if (singleFileSize + messageSize > JobConstant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						writerFile.csvClose();
						index++;
						fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
						writerFile = new WriterFile(fileName);
						writer = writerFile.getCsvWriter(DataBaseCode.ofValueByCode(
								data_extraction_def.getDatabase_code()));
						//写表头
						if (IsFlag.Shi.getCode().equals(data_extraction_def.getIs_header())) {
							writer.write(allColumnList);
						}
						fileInfo.append(fileName).append(JdbcCollectTableHandleParse.STRSPLIT);
					}
				}
				writer.write(sb);
				if (counter % 50000 == 0) {
					writer.flush();
				}
				sb.clear();
			}
			writer.flush();
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数Csv文件失败", e);
		} finally {
			try {
				if (writerFile != null)
					writerFile.csvClose();
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

}
