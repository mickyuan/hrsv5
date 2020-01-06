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
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.JobIoUtil;
import hrds.agent.job.biz.utils.WriterFile;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.lang3.StringUtils;
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
	public String writeFiles(ResultSet resultSet, CollectTableBean collectTableBean, long pageNum,
	                         long pageRow, TableBean tableBean) {
		String eltDate = collectTableBean.getEtlDate();
		StringBuilder fileInfo = new StringBuilder(1024);
		String hbase_name = collectTableBean.getHbase_name();
		String midName = Constant.JDBCUNLOADFOLDER + collectTableBean.getDatabase_id() + File.separator
				+ collectTableBean.getTable_id() + File.separator;
		midName = FileNameUtils.normalize(midName, true);
		DataFileWriter<Object> avroWriter = null;
		CsvListWriter writer;
		long lineCounter = pageNum * pageRow;
		long counter = 0;
		long fileSize;
		int index = 0;
		WriterFile writerFile = null;
		try {
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//卸数文件名为hbase_name加线程唯一标识加此线程创建文件下标
			String fileName = midName + hbase_name + pageNum + index + ".part";
			fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
			writerFile = new WriterFile(fileName);
			writer = writerFile.getCsvWriter();
			//清洗配置
			final DataCleanInterface allclean = CleanFactory.getInstance().getObjectClean("clean_database");
			String[] colName = StringUtils.splitByWholeSeparatorPreserveAllTokens(tableBean.getAllColumns(),
					CollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			//字符合并
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allclean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			List<Object> sb = new ArrayList<>(StringUtils.splitByWholeSeparatorPreserveAllTokens(tableBean.getColLengthInfo()
					, CollectTableHandleParse.STRSPLIT).length);//用来写一行数据
			StringBuilder sb_ = new StringBuilder();//用来写临时数据
			List<String> typeList = StringUtil.split(tableBean.getAllType(),
					CollectTableHandleParse.STRSPLIT);
			log.info("type : " + typeList.size() + "  colName " + colName.length);
			String currValue;
			int numberOfColumns = colName.length;
			int[] typeArray = tableBean.getTypeArray();
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
					midStringOther.append(getOneColumnValue(avroWriter, lineCounter, resultSet, typeArray[i - 1], sb_, i, hbase_name));
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, colName[i - 1].toUpperCase(), null,
							typeList.get(i - 1), FileFormat.CSV.getCode(), sb);
					// Add DELIMITER if not last value
					if (i < numberOfColumns) {
						midStringOther.append(Constant.DATADELIMITER);
					}
					if (splitIng.get(colName[i - 1].toUpperCase()) == null
							|| splitIng.get(colName[i - 1].toUpperCase()).size() == 0) {
						sb.add(currValue);
					}
				}
				//如果有列合并处理合并信息
				if (!mergeIng.isEmpty()) {
					String[] arrColString = StringUtils.split(midStringOther.toString(),
							Constant.DATADELIMITER);
					allclean.merge(mergeIng, arrColString, colName, null, sb,
							FileFormat.CSV.getCode());
				}
				sb.add(eltDate);
				//因为进数方式是表级别的，如果每张表选择了存储方式则不同目的地下的都是一样的，所以拼的字段加在卸数这里
				if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
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
						fileName = midName + hbase_name + pageNum + index + ".part";
						writerFile = new WriterFile(fileName);
						writer = writerFile.getCsvWriter();
						fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
					}
				}
				writer.write(sb);
				if (lineCounter % 50000 == 0) {
					writer.flush();
				}
				sb.clear();
			}
			writer.flush();
			//写meta数据开始  TODO 这里这个meta信息应该是只有数据抽取才需要写，5.0版本的meta信息直接通过tableBean传递
			fileSize = JobIoUtil.getFileSize(midName);
			ColumnTool.writeFileMeta(hbase_name, new File(midName), tableBean.getColumnMetaInfo(),
					lineCounter, tableBean.getColTypeMetaInfo(), tableBean.getColLengthInfo(), fileSize, "n");
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数Csv文件失败" + e.getMessage());
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
		fileInfo.append(counter).append(CollectTableHandleParse.STRSPLIT).append(fileSize);
		//返回卸数一个或者多个文件名全路径和总的文件行数和文件大小
		return fileInfo.toString();
	}

}
