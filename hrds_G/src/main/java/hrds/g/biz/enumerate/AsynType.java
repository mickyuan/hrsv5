package hrds.g.biz.enumerate;

import fd.ng.core.utils.StringUtil;

public enum AsynType {

	/**
	 * 同步返回
	 */
	SYNCHRONIZE("0", "同步返回"),

	/**
	 * 异步回调
	 */
	ASYNCALLBACK("1", "异步回调"),
	/**
	 * 异步轮询
	 */
	ASYNPOLLING("2", "异步轮询");

	private final String code;

	private final String value;

	AsynType(String code, String value) {
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
		for (AsynType asynType : AsynType.values()) {
			if (asynType.getCode().equals(code)) {
				return asynType.value;
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
	public static AsynType ofEnumByCode(String code) {
		for (AsynType asynType : AsynType.values()) {
			if (asynType.getCode().equals(code)) {
				return asynType;
			}
		}
		return null;
	}

	/**
	 * 判断是否异步状态所指定值
	 *
	 * @param asynType
	 * @return
	 */
	public static boolean isAsynType(String asynType) {
		if (StringUtil.isNotBlank(asynType) && (AsynType.SYNCHRONIZE == AsynType.ofEnumByCode(asynType)
				|| AsynType.SYNCHRONIZE == AsynType.ofEnumByCode(asynType)
				|| AsynType.SYNCHRONIZE == AsynType.ofEnumByCode(asynType))) {
			return true;
		} else {
			return false;
		}
	}

}
