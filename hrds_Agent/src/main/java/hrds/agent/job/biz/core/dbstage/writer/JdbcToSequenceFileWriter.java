package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.JobIoUtil;
import hrds.agent.job.biz.utils.WriterFile;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;

/**
 * JdbcToSequenceFileWriter
 * date: 2019/12/6 17:21
 * author: zxz
 */
public class JdbcToSequenceFileWriter extends AbstractFileWriter {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcToSequenceFileWriter.class);

	@SuppressWarnings("unchecked")
	@Override
	public String writeFiles(ResultSet resultSet, CollectTableBean collectTableBean, long pageNum,
	                         long pageRow, TableBean tableBean) {
		String eltDate = collectTableBean.getEltDate();
		StringBuilder fileInfo = new StringBuilder(1024);
		String hbase_name = collectTableBean.getHbase_name();
		String midName = Constant.JDBCUNLOADFOLDER + collectTableBean.getDatabase_id() + File.separator
				+ collectTableBean.getTable_id() + File.separator;
		midName = FileNameUtils.normalize(midName, true);
		DataFileWriter<Object> avroWriter = null;
		long lineCounter = pageNum * pageRow;
		long counter = 0;
		int index = 0;
		long fileSize;
		WriterFile writerFile = null;
		Writer writer;
		Text value;
		try {
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//卸数文件名为hbase_name加线程唯一标识加此线程创建文件下标
			String fileName = midName + hbase_name + pageNum + index + ".part";
			fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
			writerFile = new WriterFile(fileName);
			writer = writerFile.getSequenceWrite();
			//清洗配置
			final DataCleanInterface allclean = CleanFactory.getInstance().getObjectClean("clean_database");
			String[] colName = StringUtils.splitByWholeSeparatorPreserveAllTokens(tableBean.getAllColumns(),
					CollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			Clean cl = new Clean(parseJson, allclean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			StringBuilder sb = new StringBuilder();//用来写一行数据
			StringBuilder sb_ = new StringBuilder();//用来写临时数据

			String currValue;
			int numberOfColumns = colName.length;
			int[] typeArray = tableBean.getTypeArray();

			String[] type = StringUtils.split(tableBean.getAllType(), CollectTableHandleParse.STRSPLIT);
			while (resultSet.next()) {
				// Count it
				lineCounter++;
				counter++;
				//获取所有列的值用来生成MD5值
				midStringOther.delete(0, midStringOther.length());
				// Write columns
				value = new Text();
				for (int i = 1; i <= numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					midStringOther.append(getOneColumnValue(avroWriter, lineCounter, resultSet,
							typeArray[i - 1], sb_, i, hbase_name));
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, colName[i - 1].toUpperCase(), null, type[i - 1],
							FileFormat.SEQUENCEFILE.getCode(), null);
					sb.append(currValue);
					if (i < numberOfColumns) {
						sb.append(Constant.DATADELIMITER);
						midStringOther.append(Constant.DATADELIMITER);
					}
				}
				//如果有列合并处理合并信息
				if (!mergeIng.isEmpty()) {
					String[] arrColString = StringUtils.split(midStringOther.toString(), Constant.DATADELIMITER);
					String mer = allclean.merge(mergeIng, arrColString, colName, null, null,
							FileFormat.SEQUENCEFILE.getCode());
					//字段合并
					midStringOther.append(mer).append(Constant.DATADELIMITER);
					sb.append(mer).append(Constant.DATADELIMITER);
				}
				sb.append(eltDate);
				if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
					String md5 = toMD5(midStringOther.toString());
					sb.append(Constant.DATADELIMITER).append(Constant.MAXDATE).
							append(Constant.DATADELIMITER).append(md5);
				}
				if (Constant.WriteMultipleFiles) {
					//获取文件大小和当前读到的内容大小
					long messageSize = sb.toString().length();
					long singleFileSize = new File(midName + index + ".part").length();
					if (singleFileSize + messageSize > Constant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						writerFile.sequenceClose();
						index++;
						fileName = midName + hbase_name + pageNum + index + ".part";
						writerFile = new WriterFile(fileName);
						writer = writerFile.getSequenceWrite();
						fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
					}
				}
				value.set(sb.toString());
				writer.append(NullWritable.get(), value);
				if (lineCounter % 50000 == 0) {
					log.info(hbase_name + "文件已写入一次,目前写到" + lineCounter + "行");
					writer.hflush();
				}
				sb.delete(0, sb.length());
			}
			writer.hflush();
			//写meta数据开始
			fileSize = JobIoUtil.getFileSize(midName);
			ColumnTool.writeFileMeta(hbase_name, new File(midName), tableBean.getColumnMetaInfo(),
					lineCounter, tableBean.getColTypeMetaInfo(), tableBean.getColLengthInfo(), fileSize, "n");
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数Sequence文件失败" + e.getMessage());
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
