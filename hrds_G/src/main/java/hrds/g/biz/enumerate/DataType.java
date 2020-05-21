package hrds.g.biz.enumerate;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;

@DocClass(desc = "输出的数据类型", author = "dhw", createdate = "2020/4/2 15:21")
public enum DataType {

	/**
	 * json
	 */
	json("json", "json"),

	/**
	 * csv
	 */
	csv("csv", "csv");

	private final String code;

	private final String value;

	DataType(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static String ofValueByCode(String code) {
		for (DataType dataType : DataType.values()) {
			if (dataType.getCode().equals(code)) {
				return dataType.value;
			}
		}
		return null;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static DataType ofEnumByCode(String code) {
		for (DataType dataType : DataType.values()) {
			if (dataType.getCode().equals(code)) {
				return dataType;
			}
		}
		return null;
	}

	/**
	 * 判断是否异步状态所指定值
	 *
	 * @param dataType 输出数据类型
	 * @return
	 */
	public static boolean isDataType(String dataType) {
		if (StringUtil.isNotBlank(dataType) && (DataType.json == DataType.ofEnumByCode(dataType)
				|| DataType.csv == DataType.ofEnumByCode(dataType))) {
			return true;
		} else {
			return false;
		}
	}

}
