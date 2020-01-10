package hrds.agent.job.biz.dataclean;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.core.dbstage.writer.JdbcToFixedFileWriter;
import hrds.agent.job.biz.utils.ColUtil;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.CharSplitType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.FillingType;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.parquet.example.data.Group;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>标 题: 海云数服务</p>
 * <p>描 述: 系统自带清洗规则</p>
 * <p>版 权: Copyright (c) 2016</p>
 * <p>公 司: 上海泓智信息科技有限公司</p>
 * <p>创建时间: 2016年9月5日 上午11:42:07</p>
 * <p>@author mashimaro</p>
 * <p>@version 1.0</p>
 * <p>DataClean_Biz.java</p>
 * <p></p>
 */
public class DataClean_Biz implements DataCleanInterface {
	//打印日志
	private static final Log log = LogFactory.getLog(DataClean_Biz.class);
	private ColUtil cutil = new ColUtil();

	/**
	 * 字符替换
	 */
	public String replace(Map<String, Map<String, String>> deleSpecialSpace, String columnData, String columnName) {
		if (deleSpecialSpace.size() != 0) {
			Map<String, String> colMap = deleSpecialSpace.get(columnName);
			if (null != colMap && colMap.size() != 0) {
				for (String key : colMap.keySet()) {
					String str2 = colMap.get(key);
					columnData = StringUtils.replace(columnData, key, str2);
				}
			}
		}
		return columnData;
	}

	/**
	 * 字符补齐
	 */
	public String filling(Map<String, String> strFilling, String columnData, String columnname) {
		if (strFilling.size() != 0) {
			String str = strFilling.get(columnname);
			if (!StringUtil.isEmpty(str)) {
				String[] fi = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, CollectTableHandleParse.STRSPLIT);
				if (fi.length == 3) {
					int file_length = 0;
					try {
						file_length = Integer.parseInt(fi[0]);//长度
					} catch (Exception e) {
						log.error("字符补齐长度解析错误", e);
					}
					String filling_type = fi[1];//长度
					String character_filling = fi[2];//长度
					if (FillingType.QianBuQi.getCode().equals(filling_type)) {// 前对齐
						columnData = StringUtils.leftPad(columnData, file_length, character_filling);
					} else if (FillingType.HouBuQi.getCode().equals(filling_type)) {// 后补齐
						columnData = StringUtils.rightPad(columnData, file_length, character_filling);
					}
				}
			}
		}
		return columnData;
	}

	/**
	 * 日期转换
	 */
	public String dateing(Map<String, String> dateing, String columnData, String columnName) {
		if (dateing.size() != 0) {
			String formatStr = dateing.get(columnName);
			if (!StringUtil.isEmpty(formatStr)) {
				if (!StringUtil.isEmpty(columnData)) {
					try {
						String[] split = StringUtils.splitByWholeSeparatorPreserveAllTokens(formatStr,
								CollectTableHandleParse.STRSPLIT);
						//新的装换方式
						SimpleDateFormat newformat = new SimpleDateFormat(split[0]);
						//解出原来的时间
						SimpleDateFormat oldformat = new SimpleDateFormat(split[1]);
						Date parse = oldformat.parse(columnData);
						//格式化成自定义
						columnData = newformat.format(parse);
					} catch (Exception e) {
						//日志太多影响阅读
						//Debug.exception(logger, e.getMessage(),e);
					}
				}
			}
		}
		return columnData;
	}

	/**
	 * spliting  拆分信息
	 * columnData  原始数据
	 * columnname 列名
	 * group 写Parquet文件
	 * type 字段类型
	 * fileType 写文件类型
	 * list 写orc文件
	 * 字符拆分
	 */
	public String split(Map<String, Map<String, Column_split>> spliting, String columnData, String columnName, Group group, String type,
	                    String fileType, List<Object> list, String database_code, String database_separatorr) {
		if (spliting.get(columnName) != null && spliting.get(columnName).size() > 0) {
			StringBuilder sb = new StringBuilder(4096);
			//TODO 保留原字段...这里是卸数的格式，定长的还没加，先待定
			if (FileFormat.CSV.getCode().equals(fileType)) {
				list.add(columnData);
				sb.append(columnData);
			} else if (FileFormat.SEQUENCEFILE.getCode().equals(fileType)) {
				sb.append(columnData).append(Constant.DATADELIMITER);
			} else if (FileFormat.ORC.getCode().equals(fileType)) {
				list.add(columnData);
				sb.append(columnData);
			} else if (FileFormat.PARQUET.getCode().equals(fileType)) {
				if (group != null) {
					cutil.addData2Group(group, type, columnName, columnData);
				}
				sb.append(columnData);
			} else if (FileFormat.FeiDingChang.getCode().equals(fileType)) {
				sb.append(columnData).append(Constant.DATADELIMITER);
			} else if (FileFormat.DingChang.getCode().equals(fileType)) {
				int length = TypeTransLength.getLength(type);
				String fixedData = JdbcToFixedFileWriter.columnToFixed(columnData, length, database_code);
				sb.append(fixedData).append(database_separatorr);
//				log.error("定长文件，是调用这个类吗？这里要补充");
			} else {
				throw new AppSystemException("不支持的文件格式");
			}
			Map<String, Column_split> colMap = spliting.get(columnName);
			Column_split cp;
			//记录最后一个要拆分的字段类型
			if (null != colMap && colMap.size() != 0) {
				for (String colName : colMap.keySet()) {
					cp = colMap.get(colName);
					if (StringUtil.isEmpty(columnData)) {
						//csv给所有非数字类型前后加上""
						if (FileFormat.CSV.getCode().equals(fileType)) {
//							sb.append(Constant.DATADELIMITER);
							list.add("");
							//SEQUENCEFILE使用的是 '\001'作为分隔符
						} else if (FileFormat.SEQUENCEFILE.getCode().equals(fileType)) {
							sb.append(Constant.DATADELIMITER);
							//ORC
						} else if (FileFormat.ORC.getCode().equals(fileType)) {
							list.add("");
						} else if (FileFormat.PARQUET.getCode().equals(fileType)) {
							if (group != null) {
								cutil.addData2Group(group, cp.getCol_type(), colName.toUpperCase(), "");
							}
						} else if (FileFormat.FeiDingChang.getCode().equals(fileType)) {
							sb.append(Constant.DATADELIMITER);
						} else if (FileFormat.DingChang.getCode().equals(fileType)) {
							int length = TypeTransLength.getLength(cp.getCol_type());
							String fixedData = JdbcToFixedFileWriter.columnToFixed(columnData,
									length, database_code);
							sb.append(fixedData).append(database_separatorr);
//							log.error("定长文件，是调用这个类吗？这里要补充");
						} else {
							throw new AppSystemException("不支持的文件格式");
						}
					} else {
						try {
							//获取拆分的类型
							String substr;
							if (CharSplitType.PianYiLiang.getCode().equals(cp.getSplit_type())) {
								String col_offset = cp.getCol_offset();
								String[] split = col_offset.split(",");
								int start = Integer.parseInt(split[0]);
								int end = Integer.parseInt(split[1]);
								substr = columnData.substring(start, end);
							} else if (CharSplitType.ZhiDingFuHao.getCode().equals(cp.getSplit_type())) {
								int num = cp.getSeq() == null ? 0 : Integer.parseInt(cp.getSeq().toString());
								List<String> splitInNull = StringUtil.split(columnData,
										cp.getSplit_sep());
								substr = splitInNull.get(num);
							} else {
								throw new AppSystemException("不支持的字符拆分方式");
							}
							//csv给所有非数字类型前后加上""
							if (FileFormat.CSV.getCode().equals(fileType)) {
								//TODO 这里值包含分隔符是否需要拼"
//								sb.append(substr).append(Constant.DATADELIMITER);
								list.add(substr);
								//SEQUENCEFILE使用的是 '\001'作为分隔符
							} else if (FileFormat.SEQUENCEFILE.getCode().equals(fileType)) {
								sb.append(substr).append(Constant.DATADELIMITER);
								//ORC
							} else if (FileFormat.ORC.getCode().equals(fileType)) {
								list.add(substr);
							} else if (FileFormat.PARQUET.getCode().equals(fileType)) {
								if (group != null) {
									cutil.addData2Group(group, cp.getCol_type(), colName.toUpperCase(), substr);
								}
							} else if (FileFormat.FeiDingChang.getCode().equals(fileType)) {
								sb.append(substr).append(Constant.DATADELIMITER);
							} else if (FileFormat.DingChang.getCode().equals(fileType)) {
								int length = TypeTransLength.getLength(cp.getCol_type());
								String fixedData = JdbcToFixedFileWriter.columnToFixed(substr,
										length, database_code);
								sb.append(fixedData).append(database_separatorr);
//								log.error("定长文件，是调用这个类吗？这里要补充");
							} else {
								throw new AppSystemException("不支持的文件格式");
							}
						} catch (Exception e) {
							throw new AppSystemException("请检查" + colName
									+ "字段定义的字符拆分的方式" + e.getMessage());
						}
					}
				}
			}
			//删除逗号或者Constant.DATADELIMITER
			if (FileFormat.DingChang.getCode().equals(fileType)) {
				//定长且有分隔符，则删除最后的分隔符，否则不处理
				if (database_separatorr.length() > 0) {
					sb.delete(sb.length() - database_separatorr.length(), sb.length());
				}
			} else {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		} else {
			return columnData;
		}
	}

	/**
	 * 码值转换
	 */
	public String codeTrans(Map<String, String> coding, String columnData, String columnName) {
		if (coding.size() != 0) {
			JSONObject obj = JSONObject.parseObject(coding.get(columnName));
			if (obj != null && !obj.isEmpty()) {
				for (String key : obj.keySet()) {
					if (columnData.equalsIgnoreCase(key)) {
						columnData = obj.getString(key) == null ? columnData : obj.getString(key);
					}
				}
			}
		}
		return columnData;
	}

	/**
	 * mergeing 要合并的信息
	 * arrColString 所有做过其他清洗的字段数据
	 * columns 列名
	 * group 写Parquet文件时
	 */
	public String merge(Map<String, String> mergeing, String[] arrColString, String[] columns, Group group,
	                    List<Object> list, String fileType, String database_code, String database_separatorr) {

		StringBuilder return_sb = new StringBuilder(4096);
		if (mergeing.size() != 0) {
			for (String key : mergeing.keySet()) {
				StringBuilder sb = new StringBuilder();
				int[] index = findColIndex(columns, mergeing.get(key));
				for (int i : index) {
					sb.append(arrColString[i]);
				}
				if (FileFormat.PARQUET.getCode().equals(fileType)) {
					List<String> split = StringUtil.split(key, CollectTableHandleParse.STRSPLIT);
					//TODO 这里默认用varchar 待讨论
					cutil.addData2Group(group, split.get(1).toUpperCase(), split.get(0).toUpperCase(), sb.toString());
				} else if (FileFormat.ORC.getCode().equals(fileType)) {
					List<String> split = StringUtil.split(key, CollectTableHandleParse.STRSPLIT);
					cutil.addData2Inspector(list, split.get(1).toUpperCase(), sb.toString());
				} else if (FileFormat.CSV.getCode().equals(fileType)) {
					list.add(sb.toString());
				} else if (FileFormat.SEQUENCEFILE.getCode().equals(fileType)) {
					return_sb.append(sb.toString()).append(Constant.DATADELIMITER);
				} else if (FileFormat.DingChang.getCode().equals(fileType)) {
					return_sb.append(sb.toString()).append(Constant.DATADELIMITER);
				} else if (FileFormat.FeiDingChang.getCode().equals(fileType)) {
					List<String> split = StringUtil.split(key, CollectTableHandleParse.STRSPLIT);
					int length = TypeTransLength.getLength(split.get(1));
					String fixedStr = JdbcToFixedFileWriter.columnToFixed(sb.toString(), length, database_code);
					return_sb.append(fixedStr).append(database_separatorr);
//					log.error("定长文件，是调用这个类吗？这里要补充");
				} else {
					throw new AppSystemException("不支持的文件格式");
				}
			}
			//删除逗号或者Constant.DATADELIMITER
			if (FileFormat.DingChang.getCode().equals(fileType)) {
				//定长且有分隔符，则删除最后的分隔符，否则不处理
				if (database_separatorr.length() > 0) {
					return_sb.delete(return_sb.length() - database_separatorr.length(), return_sb.length());
				}
			} else {
				return_sb.deleteCharAt(return_sb.length() - 1);
			}
		}
		return return_sb.toString();
	}

	public String trim(Map<String, String> Triming, String columnData, String columnName) {
		if (!Triming.isEmpty()) {
			String colMap = Triming.get(columnName);
			if (!StringUtil.isEmpty(colMap)) {
				if (!StringUtil.isEmpty(columnData)) {
					columnData = columnData.trim();
				} else {
					columnData = "";
				}
			}
		}
		return columnData;
	}

	/**
	 *
	 */
	private int[] findColIndex(String[] column, String str) {

		String[] split = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, ",");
		int[] index = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			for (int j = 0; j < column.length; j++) {
				if (split[i].equalsIgnoreCase(column[j])) {
					index[i] = j;
				}
			}
		}
		return index;
	}
}
