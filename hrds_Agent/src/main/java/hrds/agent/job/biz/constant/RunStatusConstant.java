package hrds.agent.job.biz.constant;

import hrds.commons.exception.AppSystemException;

/**
 * 状态只有三种，开始，成功，失败，失败定义各种失败类型的枚举
 */
public enum RunStatusConstant implements EnumConstantInterface {
	WAITING(1, "waiting"), RUNNING(2, "running"), COMPLETE(3, "compele"),
	SUCCEED(4, "succeed"), FAILED(0, "failed"), TERMINATED(-1, "terminated"),
	PAUSE(5, "pause");

	private final int code;
	private final String message;

	RunStatusConstant(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * 根据指定的代码值转换成中文名字
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static String ofMessageByCode(int code) {
		for (RunStatusConstant typeCode : RunStatusConstant.values()) {
			if (typeCode.getCode() == code) {
				return typeCode.message;
			}
		}
		throw new AppSystemException("根据" + code + "没有找到对应的代码项");
	}

	@Override
	public String toString() {
		return message;
	}
}
