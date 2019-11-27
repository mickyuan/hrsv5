package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "海云应用管理端向Agent发送消息的报文打包工具类", author = "WangZhengcheng")
public class PackUtil {

	@Method(desc = "对传入的json数据进行处理，根据长度判断是否需要进行压缩加密", logicStep = "" +
			"1、将jsonData转换为byte数组，并获取该数组的长度" +
			"2、判断处理byte数组的长度，如果长度达到300，就调用工具类对数据进行压缩" +
			"3、如果长度未达到300，就不需要压缩" +
			"4、返回经过处理后的数据")
	@Param(name = "jsonData", desc = "待压缩的数据", range = "JSON格式的字符串")
	@Return(desc = "经过处理后的信息", range = "不为空")
	public static String packMsg(String jsonData) {
		try {
			//1、将jsonData转换为byte数组，并获取该数组的长度
			int valueSize = jsonData.getBytes().length;
			StringBuilder sbData = new StringBuilder();
			//2、判断处理byte数组的长度，如果长度达到300，就调用工具类对数据进行压缩
			//3、如果长度未达到300，就不需要压缩
			if (valueSize >= 300) {
				sbData.append(IsFlag.Shi.getCode());
				sbData.append(ZipUtils.gzip(jsonData));
			} else {
				sbData.append(IsFlag.Fou.getCode());
				sbData.append(jsonData);
			}
			//4、返回经过处理后的数据
			return sbData.toString();
		} catch (Exception var7) {
			throw new AppSystemException("SENDERR:数据打包失败!", var7);
		}
	}

	@Method(desc = "对传入的json数据进行处理，获取http响应的各个部分的数据，封装成Map<String,String>返回", logicStep = "" +
			"1、获取数据，获得前26个字符，并进行特殊处理" +
			"2、判断响应数据是否被压缩，如果被压缩，要进行解压解密" +
			"3、将以上部分封装到Map集合中并返回")
	@Param(name = "data", desc = "Agent端响应的数据", range = "不为空")
	@Return(desc = "经过处理后的响应数据", range = "四对Entry，key分别为iscompress, msg(数据部分)")
	public static Map<String, String> unpackMsg(String data) {
		String compressed = data.substring(0, 1);
		IsFlag compressedFlag = IsFlag.ofEnumByCode(compressed);
		Map<String, String> map = new HashMap<>();
		map.put("isCompress", compressed);
		String msg = data.substring(1);
		if (compressedFlag == IsFlag.Shi) {
			map.put("msg", StringUtil.isBlank(msg) ? "" : ZipUtils.gunzip(msg));
		}else{
			map.put("msg", StringUtil.isBlank(msg) ? "" : msg);
		}
		return map;
	}
}
