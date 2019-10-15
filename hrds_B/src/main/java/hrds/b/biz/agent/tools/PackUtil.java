package hrds.b.biz.agent.tools;

import fd.ng.core.annotation.Class;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ZipUtils;

import java.util.HashMap;
import java.util.Map;

@Class(desc = "海云应用管理端向Agent发送消息的报文打包工具类", author = "WangZhengcheng")
public class PackUtil {

	@Method(desc = "对传入的json数据进行处理，根据长度判断是否需要进行压缩加密", logicStep = "" +
			"1、将jsonData转换为byte数组，并获取该数组的长度，对获取到的长度做特殊处理" +
			"2、判断处理byte数组的长度，如果长度达到300，就调用工具类对数据进行压缩" +
			"3、如果长度未达到300，就不需要压缩" +
			"4、返回经过处理后的数据")
	@Param(name = "jsonData", desc = "JSON格式的字符串，内容是根据colSetId去数据库中获取的数据库设置相关信息", range = "" +
			"如果是根据表名模糊查询，则FuzzyQueryTableName的值为表名字符串(如果是多个中间用|分隔)" +
			"如果是获取所有表信息，则FuzzyQueryTableName的值为NOTHING")
	@Return(desc = "经过处理后的信息", range = "不为空")
	public static String packMsg(String jsonData) {
		try {
			//1、将jsonData转换为byte数组，并获取该数组的长度，对获取到的长度做特殊处理
			int valueSize = jsonData.getBytes().length;
			int afterValueSize = valueSize + 26;
			StringBuilder sbData = new StringBuilder();
			sbData.append(appendCharToLength(String.valueOf(afterValueSize), '0', 7, true));
			//2、判断处理byte数组的长度，如果长度达到300，就调用工具类对数据进行压缩
			//3、如果长度未达到300，就不需要压缩
			if (valueSize >= 300) {
				sbData.append("Compressed");
				sbData.append(ZipUtils.gzip(jsonData));
			} else {
				sbData.append("Uncompressed");
				sbData.append(jsonData);
			}
			//4、返回经过处理后的数据
			return sbData.toString();
		} catch (Exception var7) {
			throw new AppSystemException("SENDERR:数据打包失败!", var7);
		}
	}

	@Method(desc = "对字节数组的长度补0，补齐7位", logicStep = "" +
			"1、获取要补齐的0的个数" +
			"2、构造补齐字符串")
	@Param(name = "src", desc = "待补齐的原字符串", range = "不限")
	@Param(name = "addChar", desc = "补齐使用的字符", range = "不为空")
	@Param(name = "length", desc = "补齐后的字符串长度", range = "不为空")
	@Param(name = "atHead", desc = "进行前补齐还是后补齐", range = "true为前补齐，false为后补齐")
	@Return(desc = "补齐后的待发送数据", range = "不为空")
	private static String appendCharToLength(String src, char addChar, int length, boolean atHead) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (src == null) {
			src = "";
		}
		//1、获取要补齐的0的个数
		int toAdd = length - src.getBytes(CodecUtil.UTF8_STRING).length;//FIXME 要用CodecUtil里面的UTF变量！已修复
		//2、构造补齐字符串
		for(int i = 0; i < toAdd; ++i) {
			sb.append(addChar);
		}
		//3、根据atHead判断是进行前补齐还是后补齐并返回
		return atHead ? sb + src : src + sb;
	}

	@Method(desc = "对传入的json数据进行处理，获取http响应的各个部分的数据，封装成Map<String,String>返回", logicStep = "" +
			"1、获取数据，获得前26个字符，并进行特殊处理" +
			"2、判断响应数据是否被压缩，如果被压缩，要进行解压解密" +
			"3、将以上部分封装到Map集合中并返回")
	@Param(name = "data", desc = "Agent端响应的数据", range = "不为空")
	@Return(desc = "经过处理后的响应数据", range = "四对Entry，key分别为bit_head, head_msg, iscompress, msg(数据部分)")
	public static Map<String, String> unpackMsg(String data) {
		Map<String, String> map = new HashMap<>();
		String fileNameHead = data.substring(0, 15);
		//FIXME 要判断buf的长度，否则，越界了怎么办！已修复，直接对fileNameHead使用substring截取
		// 另外，插入是为了下面的split，那么直接用substring截取不行了吗？
		// TODO 或者，再构造这个串的时候，加上分隔符号，是不是更好？
		map.put("bit_head", fileNameHead.substring(0, 7));
		map.put("head_msg", fileNameHead.substring(7, 11));
		String iscompress = fileNameHead.substring(11, 15);
		map.put("iscompress", iscompress);
		String msg = data.substring(26);
		if ("Compressed".equals(iscompress)) {
			map.put("msg", StringUtil.isBlank(msg) ? "" : ZipUtils.gunzip(msg));
		} else if("Uncompressed".equals(iscompress)){
			map.put("msg", StringUtil.isBlank(msg) ? "" : msg);
		} else{
			throw new BusinessException("iscompress参数取值不合法，该参数只能是Compressed/Uncompressed");
		}
		return map;
	}
}
