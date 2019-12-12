package hrds.agent.job.biz.utils;

import hrds.commons.hadoop.readconfig.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class JobIoUtil {

	private static final String SOLRCOLUMN = "TABLE_NAME";

	public static void closeQuietly(String type, String file, boolean jdbc) {

		String finallyName = null;
		FileSystem fs = null;
		try {
			Configuration conf = ConfigReader.getConfiguration();
			conf.setBoolean("fs.hdfs.impl.disable.cache", true);
			//TODO 这里直接写本地可能效率问题，但是因为自定义目的进数方式不一样，这里就是本地
			conf.set("fs.defaultFS", "file:///");
			fs = FileSystem.get(conf);
			switch (type) {
				case "csv":
					finallyName = file + ".csv";
					break;
				case "orc":
					finallyName = file + ".orc";
					break;
				case "parquet":
					finallyName = file + ".parquet";
					break;
				case "sequencefile":
					finallyName = file + ".seq";
					break;
			}
			fs.delete(new Path(finallyName), true);
			fs.rename(new Path(file + ".part"), new Path(finallyName));
		} catch (Exception ioe) {
			try {
				if (fs != null) {
					fs.delete(new Path(file + ".part"), true);
				}
			} catch (IllegalArgumentException | IOException e) {

			}
		} finally {
			IOUtils.closeQuietly(fs);
		}
	}

	public static long getFileSize(String path) {
		FileSystem fs = null;
		long size = 0;
		Configuration conf = ConfigReader.getConfiguration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.set("fs.defaultFS", "file:///");
		try {
			fs = FileSystem.get(conf);
			FileStatus[] listStatus = fs.listStatus(new Path(path));
			for (FileStatus fileStatus : listStatus) {
				size += fileStatus.getLen();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fs);
		}
		return size;

	}

	public static void clearnDir(final String tableName, File parent) {
//		System.out.println("文件"+tableName);
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		} else if (parent != null) {
			// 防止重跑上一次的文件还在
			File[] listFiles = parent.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					// 获取当前文件的名字，通过表名字来判断文件是否存在，使用相等判断 会遇到当一张表写多个文件时 文件带有编号
					// 使用包含关系时 customer_address 包含customer 这样处理customer表会误删除 customer_address
//					String filename = pathname.getName();
//					return filename.contains(tableName);
					if (pathname.isDirectory()) {
						return false;
					}
					String filename = pathname.getName();
					String substr;
					if (filename.indexOf('.') > -1) {
						substr = filename.substring(0, filename.indexOf('.'));
					} else {
						if (filename.length() < tableName.length()) {
							return false;
						}
						substr = filename.substring(0, tableName.trim().length());
					}
					return substr.equalsIgnoreCase(tableName);
				}
			});
			if (null != listFiles) {
				for (File fi : listFiles) {
					FileUtils.deleteQuietly(fi);
				}
			}
		}
	}

	/**
	 * 判断文件是不是超过设定值大小
	 *
	 * @param realCsvFile
	 * @return
	 */
//	public static boolean isBigFile(File realCsvFile) {
//		return Constant.HAS_HADOOP_ENV && realCsvFile.length() > Constant.MRSIZE;
//	}

	/**
	 * 从xml获取当前表的信息，写到sb中
	 *
	 * @param all_columns
	 * @param all_colType
	 * @param columns
	 * @param colType
	 * @param lengths
	 * @param jb
	 * @param cList
	 * @param jsonmsg
	 * @param jsonsingle
	 * @param is_use
	 * @param everyColLen
	 * @param is_use_
	 * @throws Exception
	 */
//	@SuppressWarnings("unchecked")
//	public static void getColumnInfo(StringBuilder all_columns, StringBuilder all_colType, StringBuilder columns, StringBuilder colType,
//	                                 StringBuilder lengths, JobBean jb, List<String> cList, JSONObject jsonmsg, JSONObject jsonsingle, boolean[] is_use,
//	                                 int[] everyColLen, String[] is_use_) throws Exception {
//		int size = cList.size();
//		for (int i = 0; i < size; i++) {
//			String cName = cList.get(i);
//			String cNameList[] = StringUtil.splitInNull(cName, "^");
//			all_columns.append(cNameList[0]);
//			all_columns.append(',');
//			String string = cNameList[2];
//			everyColLen[i] = Integer.parseInt(string);
//			all_colType.append(cNameList[3]).append("|");
//			if (IsFlag.Shi.equals(jb.getColumns_use().getString(cNameList[0]))) {
//				lengths.append(cNameList[2]).append(",");
//				colType.append(cNameList[3]).append("|");
//				columns.append(cNameList[0]).append(',');
//				is_use[i] = true;
//				is_use_[i] = "y";
//			} else {
//				is_use[i] = false;
//				is_use_[i] = "n";
//			}
//		}
//		all_columns = all_columns.deleteCharAt(all_columns.length() - 1);
//		colType = colType.length() == 0 ? colType : colType.deleteCharAt(colType.length() - 1);
//		columns = columns.length() == 0 ? columns : columns.deleteCharAt(columns.length() - 1);
//		lengths = lengths.length() == 0 ? lengths : lengths.deleteCharAt(lengths.length() - 1);
//		Map<String, Object> parseJson = Clean_ParseJson.parseJson(jsonsingle, jsonmsg, all_columns.toString());
//		Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>) parseJson.get("splitIng");//字符替换
//		Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
//
//		//更新拆分和合并的列信息
//		String colmeta = ColumnMeta.updateColumn(mergeIng, splitIng, columns, colType, lengths);
//		columns.delete(0, columns.length()).append(colmeta);
//		if (jb.getIs_solr()) {
//			columns.append(",").append(SOLRCOLUMN);
//			colType.append('|').append("char(").append(SOLRCOLUMN.length()).append(")");
//		}
//		columns.append(",").append(Constant.SDATENAME);
//		colType.append('|').append("char(8)");
//		if (jb.getIs_md5()) {
//			columns.append(",").append(Constant.EDATENAME).append(",").append(Constant.MD5NAME);
//			colType.append("|").append("char(8)").append("|").append("char(32)");
//		}
//
//	}

}
