package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.JobIoUtil;
import hrds.agent.job.biz.utils.ParquetUtil;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * JdbcToParquetFileWriter
 * date: 2019/12/6 17:20
 * author: zxz
 */
public class JdbcToParquetFileWriter extends AbstractFileWriter {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcToParquetFileWriter.class);

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
		ParquetWriter<Group> parquetWriter = null;
		long lineCounter = pageNum * pageRow;
		long counter = 0;
		int index = 0;
		long fileSize;
		GroupFactory factory;
		try {
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			//清洗配置
			final DataCleanInterface allClean = CleanFactory.getInstance().getObjectClean("clean_database");
			String[] colName = StringUtils.splitByWholeSeparatorPreserveAllTokens(tableBean.getAllColumns(),
					CollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			//字符合并
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allClean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			StringBuilder sb_ = new StringBuilder();//用来写临时数据

			String currValue;
			int numberOfColumns = colName.length;
			int[] typeArray = tableBean.getTypeArray();
			MessageType parquetSchema = ParquetUtil.getSchema(tableBean.getColumnMetaInfo(),
					tableBean.getColTypeMetaInfo());
			factory = new SimpleGroupFactory(parquetSchema);
			String fileName = midName + hbase_name + pageNum + index + ".part";
			parquetWriter = ParquetUtil.getParquetWriter(parquetSchema, fileName);
			fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
			List<String> type = StringUtil.split(tableBean.getAllType(), CollectTableHandleParse.STRSPLIT);
			while (resultSet.next()) {
				lineCounter++;
				counter++;
				midStringOther.delete(0, midStringOther.length());
				//每一行获取一个group对象
				Group group = factory.newGroup();
				for (int i = 1; i <= numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					midStringOther.append(getOneColumnValue(avroWriter, lineCounter, resultSet,
							typeArray[i - 1], sb_, i, hbase_name));
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, colName[i - 1].toUpperCase(), group, type.get(i - 1),
							FileFormat.PARQUET.getCode(), null);
					// Write to output
					// Add DELIMITER if not last value
					if (i < numberOfColumns) {
						midStringOther.append(Constant.DATADELIMITER);
					}
					if (splitIng.isEmpty()) {
						ColumnTool.addData2Group(group, type.get(i - 1), colName[i - 1], currValue);
					}
				}
				//如果有列合并处理合并信息
				if (!mergeIng.isEmpty()) {
					String[] arrColString = StringUtils.split(midStringOther.toString(), Constant.DATADELIMITER);
					//字段合并
					allClean.merge(mergeIng, arrColString, colName, group, null, FileFormat.PARQUET.getCode());
				}
				group.append(Constant.SDATENAME, eltDate);
				//因为进数方式是表级别的，如果每张表选择了存储方式则不同目的地下的都是一样的，所以拼的字段加在卸数这里
				if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
					String md5 = toMD5(midStringOther.toString());
					group.append(Constant.EDATENAME, Constant.MAXDATE).append(Constant.MD5NAME, md5);
				}
				if (Constant.WriteMultipleFiles) {
					//获取文件大小和当前读到的内容大小
					long messageSize = group.toString().length();
					long singleFileSize = new File(fileName).length();
					if (singleFileSize + messageSize > Constant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						parquetWriter.close();
						index++;
						fileName = midName + hbase_name + pageNum + index + ".part";
						parquetWriter = ParquetUtil.getParquetWriter(parquetSchema, fileName);
						fileInfo.append(fileName).append(CollectTableHandleParse.STRSPLIT);
					}
				}
				parquetWriter.write(group);
			}
			//写meta数据开始
			fileSize = JobIoUtil.getFileSize(midName);
			ColumnTool.writeFileMeta(hbase_name, new File(midName), tableBean.getColumnMetaInfo(),
					lineCounter, tableBean.getColTypeMetaInfo(), tableBean.getColLengthInfo(), fileSize, "n");
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数Parquet文件失败" + e.getMessage());
		} finally {
			try {
				if (parquetWriter != null)
					parquetWriter.close();
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
