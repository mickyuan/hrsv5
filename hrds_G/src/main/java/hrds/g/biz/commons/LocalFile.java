package hrds.g.biz.commons;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.PropertyParaValue;
import hrds.g.biz.enumerate.DataType;
import hrds.g.biz.enumerate.OutType;
import hrds.g.biz.enumerate.StateType;
import hrds.g.biz.serviceuser.common.InterfaceCommon;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

public class LocalFile {

	private static final Logger logger = LogManager.getLogger();
	private static final Type type = new TypeReference<List<Map<String, String>>>() {
	}.getType();

	@Method(desc = "判断是不是数据文件.如果是数据文件则写成对应的文件",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.判断dataType,outType是否合法" +
					"3.判断输出数据类型以及输出数据形式处理数据" +
					"4.输出数据形式为stream,输出数据类型为json，直接返回数据" +
					"5.输出数据形式为stream,输出数据类型为csv,处理为csv数据返回" +
					"6.创建文件(文件名称为UUID.dataType)" +
					"7.写文件" +
					"8.如果文件写成功则保存此次记录")
	@Param(name = "feedback", desc = "根据rowkey，表名称、数据版本号获取hbase表信息", range = "无限制")
	@Param(name = "dataType", desc = "输出数据类型", range = "json/csv")
	@Param(name = "outType", desc = "输出数据形式", range = "stream/file")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> writeFile(DatabaseWrapper db, Map<String, Object> feedback,
	                                            String dataType, String outType, Long user_id) {
		try {
			// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
			// 2.判断dataType,outType是否合法
			if (!DataType.isDataType(dataType)) {
				return StateType.getResponseInfo(StateType.DATA_TYPE_ERROR);
			}
			if (!OutType.isOutType(outType)) {
				return StateType.getResponseInfo(StateType.OUT_TYPE_ERROR);
			}
			// 3.判断输出数据类型以及输出数据形式处理数据
			if (OutType.STREAM == OutType.ofEnumByCode(outType)) {
				// 4.输出数据形式为stream,输出数据类型为json，直接返回数据
				if (DataType.json == DataType.ofEnumByCode(dataType)) {
					feedback.put("dataType", dataType);
					feedback.put("outType", OutType.STREAM.getValue());
				} else {
					// 5.输出数据形式为stream,输出数据类型为csv,处理为csv数据返回
					csvData(feedback);
				}
				return feedback;
			} else {
				String uuid = UUID.randomUUID().toString();
				// 6.创建文件(文件名称为UUID.dataType)
				File filePath = createFile(uuid, dataType);
				// 7.写文件
				boolean isWriteSuccess = writeDataFile(filePath, feedback, dataType);
				// 8.如果文件写成功则保存此次记录
				if (isWriteSuccess) {
					if (InterfaceCommon.saveFileInfo(db, user_id, uuid, dataType, outType,
							CommonVariables.RESTFILEPATH) == 1) {
						JSONObject obj = new JSONObject();
						obj.put("uuid", uuid);
						obj.put("dataType", dataType);
						obj.put("outType", OutType.FILE.getValue());
						feedback.put("message", obj);
					} else {
						feedback = StateType.getResponseInfo(StateType.EXCEPTION.getCode(),
								"保存接口文件信息失败");
					}
				} else {
					logger.info("数据输出形式为 : " + outType + "   删除文件");
					FileUtil.deleteDirectory(filePath);
				}
				return feedback;
			}
		} catch (IOException e) {
			logger.error(e);
			return StateType.getResponseInfo(StateType.EXCEPTION);
		}
	}

	@Method(desc = "创建存放文件的路径", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.获取文件的路径" +
			"3.判断文件存放目录是否存在，不存在创建" +
			"4.判断文件是否存在，不存在创建" +
			"5.返回文件")
	@Param(name = "uuid", desc = "文件ID", range = "使用uuid生成")
	@Param(name = "dataType", desc = "数据输出类型", range = "json/csv")
	@Return(desc = "返回创建存放文件的路径", range = "无限制")
	public static File createFile(String uuid, String dataType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.获取文件的路径
		String filePath = CommonVariables.RESTFILEPATH + File.separator + uuid + '.' + dataType;
		File writeFile = null;
		try {
			File file = new File(CommonVariables.RESTFILEPATH);
			// 3.判断文件存放目录是否存在，不存在创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
			// 4.判断文件是否存在，不存在创建
			writeFile = new File(filePath);
			if (!writeFile.exists()) {
				writeFile.createNewFile();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		// 5.返回文件
		return writeFile;
	}

	@Method(desc = "写数据文件", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.解析获取hbase表信息" +
			"3.创建流对象" +
			"4.判断输出数据类型为csv还是json处理数据" +
			"5.关闭流" +
			"6.返回接口响应信息")
	@Param(name = "file", desc = "存放数据文件路径", range = "无限制")
	@Param(name = "feedback", desc = "hbase表信息", range = "无限制")
	@Param(name = "dataType", desc = "输出数据类型", range = "json/csv")
	@Return(desc = "返回写文件响应信息", range = "true代表成功，false代表失败")
	public static boolean writeDataFile(File file, Map<String, Object> feedback, String dataType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		BufferedWriter writer;
		try {
			// 2.解析获取hbase表信息
			List<Map<String, String>> listData = JsonUtil.toObject(feedback.get("message").toString(), type);
			// 3.创建流对象
			writer = new BufferedWriter(new FileWriter(file));
			// 列信息
			List<String> columnList = new ArrayList<>();
			int size = 0;
			// 4.判断输出数据类型为csv还是json处理数据
			if (DataType.csv == DataType.ofEnumByCode(dataType)) {
				for (Map<String, String> map : listData) {
					// map：key代表列名，value代表对应值
					StringBuilder builder = new StringBuilder();
					for (Entry<String, String> entry : map.entrySet()) {
						builder.append(entry.getValue()).append(',');
						String column = entry.getKey().toLowerCase();
						if (!columnList.contains(column)) {
							columnList.add(column);
						}
					}
					if (size == 0) {
						//只写一次CSV的列.
						writer.write(StringUtils.join(columnList, ','));//csv文件的第一行为列的信息
						writer.newLine();
					}
					String columnData = builder.deleteCharAt(builder.length() - 1).toString();
					writer.write(columnData);
					writer.newLine();
					if (size % 100000 == 0) {
						//10万条清理一次
						writer.flush();
					}
					size++;
				}
			} else {
				for (Map<String, String> map : listData) {
					JSONObject jsonDataObj = new JSONObject();
					for (Entry<String, String> entry : map.entrySet()) {
						jsonDataObj.put(entry.getKey().toLowerCase(), entry.getValue());
					}
					writer.write(jsonDataObj.toJSONString());
					writer.newLine();
					if (size % 100000 == 0) {
						//10万条清理一次
						writer.flush();
					}
					size++;
				}
			}
			writer.flush();
			// 5.关闭流
			writer.close();
			// 6.返回写文件响应信息
			return true;
		} catch (IOException e) {
			logger.error(e);
		}
		return false;
	}

	@Method(desc = "处理为流的csv数据", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.解析获取hbase表信息" +
			"3.遍历获取hbase表列值信息" +
			"4.封装数据为csv数据格式")
	@Param(name = "feedback", desc = "hbase表信息", range = "无限制")
	public static void csvData(Map<String, Object> feedback) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		try {
			// 2.解析获取hbase表信息
			List<Map<String, String>> listData = JsonUtil.toObject(feedback.get("message").toString(), type);
			//列信息
			List<String> columnList = new ArrayList<>();
			//列对应的值
			List<String> columnData = new ArrayList<>();
			// 3.遍历获取hbase表列值信息
			for (Map<String, String> map : listData) {
				StringBuilder builder = new StringBuilder();
				for (Entry<String, String> entry : map.entrySet()) {
					String column = entry.getKey().toLowerCase();
					builder.append(entry.getValue()).append(',');
					if (!columnList.contains(column)) {
						columnList.add(column);
					}
				}
				builder.deleteCharAt(builder.length() - 1);
				columnData.add(builder.toString());
			}
			// 4.封装数据为csv数据格式
			Map<String, Object> obj = new HashMap<>();
			obj.put("column", String.join(",", columnList));
			obj.put("data", columnData);
			feedback.put("message", obj);
		} catch (Exception e) {
			feedback.put("message", "流数据量过大...请使用文件");
		}
	}

}
