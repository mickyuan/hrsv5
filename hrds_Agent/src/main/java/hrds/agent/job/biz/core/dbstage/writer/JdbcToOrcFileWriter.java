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
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.WriterFile;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.avro.file.DataFileWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.RecordWriter;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JdbcToOrcFileWriter
 * date: 2019/12/6 17:19
 * author: zxz
 */
public class JdbcToOrcFileWriter extends AbstractFileWriter {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcToOrcFileWriter.class);
	private OrcSerde serde = new OrcSerde();

	@SuppressWarnings("unchecked")
	@Override
	public String writeFiles(ResultSet resultSet, CollectTableBean collectTableBean, int pageNum,
	                         TableBean tableBean, Data_extraction_def data_extraction_def) {
		String eltDate = collectTableBean.getEtlDate();
		//数据抽取指定的目录
		String plane_url = data_extraction_def.getPlane_url();
		String midName = plane_url + File.separator + eltDate + File.separator + collectTableBean.getTable_name()
				+ File.separator + FileFormat.ORC.getValue() + File.separator;
		String hbase_name = collectTableBean.getHbase_name();
		midName = FileNameUtils.normalize(midName, true);
		String dataDelimiter = data_extraction_def.getDatabase_separatorr();
		DataFileWriter<Object> avroWriter = null;
		long counter = 0;
		int index = 0;
		WriterFile writerFile = null;
		RecordWriter<NullWritable, Writable> writer;
		StringBuilder fileInfo = new StringBuilder(1024);
		try {
			String fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
			fileInfo.append(fileName).append(JdbcCollectTableHandleParse.STRSPLIT);
			writerFile = new WriterFile(fileName);
			avroWriter = getAvroWriter(tableBean.getTypeArray(), hbase_name, midName, pageNum);
			writer = writerFile.getOrcWrite();
			/* Get result set metadata */
			//清洗配置
			final DataCleanInterface allClean = CleanFactory.getInstance().getObjectClean("clean_database");
			//获取所有字段的名称，包括列分割和列合并出来的字段名称
			List<String> allColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
			//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
			List<String> selectColumnList = StringUtil.split(tableBean.getAllColumns(), JdbcCollectTableHandleParse.STRSPLIT);
			Map<String, Object> parseJson = tableBean.getParseJson();
			//字符合并
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");
			//字符拆分
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>)
					parseJson.get("splitIng");
			Clean cl = new Clean(parseJson, allClean);
			StringBuilder midStringOther = new StringBuilder(1024 * 1024);//获取所有列的值用来生成MD5值
			StringBuilder sb_ = new StringBuilder();//用来写临时数据

			List<String> typeList = StringUtil.split(tableBean.getAllType(),
					JdbcCollectTableHandleParse.STRSPLIT);
			String currValue;
			int numberOfColumns = selectColumnList.size();
			int[] typeArray = tableBean.getTypeArray();

			StructObjectInspector inspector = ColumnTool.schemaInfo(tableBean.getColumnMetaInfo(),
					tableBean.getColTypeMetaInfo());//Orc文件的列头信息
			List<Object> lineData;
			while (resultSet.next()) {
				// Count it
				counter++;
				//获取所有列的值用来生成MD5值
				midStringOther.delete(0, midStringOther.length());
				// Write columns

				lineData = new ArrayList<>();
				for (int i = 0; i < numberOfColumns; i++) {
					//获取原始值来计算 MD5
					sb_.delete(0, sb_.length());
					midStringOther.append(getOneColumnValue(avroWriter, counter, pageNum, resultSet,
							typeArray[i], sb_, selectColumnList.get(i), hbase_name));
					if (i < numberOfColumns-1) {
						midStringOther.append(JobConstant.DATADELIMITER);
					}
					//清洗操作
					currValue = sb_.toString();
					currValue = cl.cleanColumn(currValue, selectColumnList.get(i).toUpperCase(), null,
							typeList.get(i), FileFormat.ORC.getCode(), lineData,
							data_extraction_def.getDatabase_code(), dataDelimiter);
					if (splitIng.get(selectColumnList.get(i).toUpperCase()) == null
							|| splitIng.get(selectColumnList.get(i).toUpperCase()).size() == 0) {
						ColumnTool.addData2Inspector(lineData, typeList.get(i), currValue);
					}
				}
				//如果有列合并处理合并信息
				if (!mergeIng.isEmpty()) {
					List<String> arrColString = StringUtil.split(midStringOther.toString(),
							JobConstant.DATADELIMITER);
					//字段合并
					allClean.merge(mergeIng, arrColString.toArray(new String[0]), allColumnList.toArray(new String[0]),
							null, lineData, FileFormat.ORC.getCode(),
							data_extraction_def.getDatabase_code(), dataDelimiter);
				}
				lineData.add(eltDate);
				//根据是否算MD5判断是否追加结束日期和MD5两个字段
				if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5())) {
					String md5 = toMD5(midStringOther.toString());
					lineData.add(Constant.MAXDATE);
					lineData.add(md5);
				}
				if (JobConstant.WriteMultipleFiles) {
					//获取文件大小和当前读到的内容大小
					long messageSize = lineData.toString().length();
					long singleFileSize = new File(fileName).length();
					if (singleFileSize + messageSize > JobConstant.FILE_BLOCKSIZE) {
						//当文件满足阈值时 ，然后关闭当前流，并创建新的数据流
						writerFile.close();
						index++;
						fileName = midName + hbase_name + pageNum + index + "." + data_extraction_def.getFile_suffix();
						writerFile = new WriterFile(fileName);
						writer = writerFile.getOrcWrite();
						fileInfo.append(fileName).append(JdbcCollectTableHandleParse.STRSPLIT);
					}
				}
				writer.write(NullWritable.get(), serde.serialize(lineData, inspector));
			}
		} catch (Exception e) {
			log.error("卸数失败", e);
			throw new AppSystemException("数据库采集卸数Orc文件失败" + e.getMessage());
		} finally {
			try {
				if (writerFile != null)
					writerFile.orcClose();
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
