package hrds.agent.job.biz.utils;

import fd.ng.core.utils.StringUtil;

import java.util.StringTokenizer;

/**
 * @description: 操作字符串的工具类，从老项目中摘出来，根据idea的提示对逻辑不合理的地方稍作优化
 * @author: WangZhengcheng
 * @create: 2019-08-28 14:34
 **/
public class StringOperator {

	/**
	 * @Description: 解码unicode编码的字符串
	 * @Param: [unicode : unicode编码的字符串]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/29
	 */
	public static String unicode2String(String unicode) {
		StringBuilder string = new StringBuilder();
		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; ++i) {
			int data = Integer.parseInt(hex[i], 16);
			string.append((char) data);
		}

		return string.toString();
	}

	/**
	 * @Description: 对指定字符串使用指定分隔符进行切分得到字符串数组
	 * @Param: [str : 指定字符串]
	 * @Param: [delim : 指定分隔符]
	 * @return: java.lang.String[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/29
	 */
	public static String[] split(String str, String delim) {
		String[] strReturn = null;
		if (fd.ng.core.utils.StringUtil.isBlank(delim)) {
			strReturn = new String[]{str};
			return strReturn;
		} else {
			StringTokenizer st = new StringTokenizer(str, delim);
			int size = st.countTokens();
			if (size < 0) {
				return null;
			} else {
				strReturn = new String[size];
				for (int var5 = 0; st.hasMoreTokens(); strReturn[var5++] = st.nextToken()) {

				}
				return strReturn;
			}
		}
	}

	/**
	 * @Description: 在sOld中找到sPartten的位置并替换sReplace
	 * @Param: [sOld : 原字符串]
	 * @Param: [sPartten : 被替换的字符串]
	 * @Param: [sReplace : 用做替换的字符串]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/29
	 */
	public static String replace(String sOld, String sPartten, String sReplace) {
		if (sOld == null) {
			return null;
		} else if (sPartten == null) {
			return sOld;
		} else {
			if (sReplace == null) {
				sReplace = "";
			}
			StringBuilder sBuffer = new StringBuilder();
			int isOldLen = sOld.length();
			int isParttenLen = sPartten.length();

			int iHead;
			int iIndex;
			for (iHead = 0; (iIndex = sOld.indexOf(sPartten, iHead)) > -1; iHead = iIndex + isParttenLen) {
				sBuffer.append(sOld.substring(iHead, iIndex)).append(sReplace);
			}
			sBuffer.append(sOld.substring(iHead, isOldLen));
			return sBuffer.toString();
		}
	}

	/**
	 * @Description: 将对象转换为字符串
	 * @Param: [obj]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/29
	 */
	public static String getString(Object obj) {
		return null != obj && !StringUtil.isEmpty(obj.toString()) ? obj.toString() : "";
	}
}
