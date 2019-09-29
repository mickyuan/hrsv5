package hrds.b.biz.agent.tools;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ZipUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 海云应用管理端向Agent发送消息的报文打包工具类
 * @Author: wangz
 * @CreateTime: 2019-09-26-12:00
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/
public class PackUtil {

	/**
	 * 对传入的json数据进行处理，根据长度判断是否需要进行压缩加密
	 *
	 * 1、将jsonData转换为byte数组，并获取该数组的长度，对获取到的长度做特殊处理
	 * 2、判断处理byte数组的长度，如果长度达到300，就调用工具类对数据进行压缩
	 * 3、如果长度未达到300，就不需要压缩
	 * 4、返回经过处理后的数据
	 *
	 * @Param: jsonData String
	 *         含义：JSON格式的字符串，内容是根据colSetId去数据库中获取的数据库设置相关信息
	 *         取值范围：如果是根据表名模糊查询，则FuzzyQueryTableName的值为表名字符串(如果是多个中间用|分隔)，
	 *         如果是获取所有表信息，则FuzzyQueryTableName的值为NOTHING
	 * @Param: String
	 *         含义：经过处理后的信息
	 *         取值范围：不为空
	 * */
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

	/**
	 * 对字节数组的长度补0，补齐7位
	 *
	 * 1、获取要补齐的0的个数
	 * 2、构造补齐字符串
	 * 3、根据atHead判断是进行前补齐还是后补齐并返回
	 *
	 * @Param: src String
	 *         含义：待补齐的原字符串
	 *         取值范围：不限
	 * @Param: addChar char
	 *         含义：补齐使用的字符
	 *         取值范围：不为空
	 * @Param: length int
	 *         含义：补齐后的字符串长度
	 *         取值范围：不为空
	 * @Param: atHead boolean
	 *         含义：进行前补齐还是后补齐
	 *         取值范围：true为前补齐，false为后补齐
	 * */
	private static String appendCharToLength(String src, char addChar, int length, boolean atHead) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (src == null) {
			src = "";
		}
		//1、获取要补齐的0的个数
		int toAdd = length - src.getBytes("UTF-8").length;
		//2、构造补齐字符串
		for(int i = 0; i < toAdd; ++i) {
			sb.append(addChar);
		}
		//3、根据atHead判断是进行前补齐还是后补齐并返回
		return atHead ? sb + src : src + sb;
	}

	/**
	 * 对传入的json数据进行处理，获取http响应的各个部分的数据，封装成Map<String,String>返回
	 *
	 * 1、获取数据，获得前26个字符，并进行特殊处理
	 * 2、判断响应数据是否被压缩，如果被压缩，要进行解压解密
	 * 3、将以上部分封装到Map集合中并返回
	 *
	 * @Param: jsonData String
	 *         含义：Agent端响应的数据
	 *         取值范围：不为空
	 * @Param: Map<String, String>
	 *         含义：经过处理后的响应数据
	 *         取值范围：四对Entry，key分别为bit_head, head_msg, iscompress, msg(数据部分)
	 * */
	public static Map<String, String> unpackMsg(String data) {
		Map<String, String> map = new HashMap<>();
		String fileNameHead = data.substring(0, 26);
		StringBuilder buf = new StringBuilder(fileNameHead);
		String fileName = buf.insert(25, '_').insert(15, '_').insert(11, '_').insert(7, "_").toString();
		List<String> split = StringUtil.split(fileName, "_");
		map.put("bit_head", split.get(0));
		map.put("head_msg", split.get(1));
		String iscompress = split.get(2);
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
