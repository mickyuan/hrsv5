package hrds.agent.job.biz.constant;

public enum DataTypeConstant implements EnumConstantInterface {
	//TODO 从0开始是因为海云是如此
	STRING(0, "string"), CHAR(1, "char"), BOOLEAN(3, "boolean"),
	INT(4, "int"), DECIMAL(5, "decimal"), FLOAT(6, "float"),
	DOUBLE(7, "double"), INT8(8, "int8"), BIGINT(9, "bigint"),
	LONG(10, "long"),NUMERIC(11,"numeric"),BYTE(12,"byte"),
	TIMESTAMP(13,"timestamp"),DATE(14,"date");

	private final int code;
	private final String message;

	DataTypeConstant(int code, String message) {
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

	@Override
	public String toString() {
		return message;
	}
}
