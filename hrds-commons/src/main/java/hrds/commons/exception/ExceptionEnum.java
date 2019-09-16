package hrds.commons.exception;

/**
 * 定制本项目中各种通用的异常信息。
 * code 必须是大于1000的任意整数！
 */
public enum ExceptionEnum implements ExceptionMessage {
	//TODO 以下 5 个枚举值仅仅为示例。请根据项目具体情况进行修改！
	DATA_NOT_EXIST(1100, "无法找到匹配的数据"),
	DATA_ADD_ERROR(1200, "新增数据失败"),
	DATA_UPDATE_ERROR(1300, "更新数据失败"),
	DATA_DELETE_ERROR(1400, "删除数据失败"),
	AGENT_DOWN_ERROR(1500, "Agent部署信息保存失败"),
	USER_NOT_EMPTY(9995, "用户名不能为空"),
	USER_PWD_EMPTY(9996, "密码不能为空"),
	USER_NOT_EXISTS(9997, "用户不存在"),
	PASSWORD_ERROR(9998, "密码错误");

	private final int code;
	private final String message;

	ExceptionEnum(final int code, final String message) {

		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode() {

		return code;
	}

	@Override
	public String getMessage() {

		return message;
	}
}
