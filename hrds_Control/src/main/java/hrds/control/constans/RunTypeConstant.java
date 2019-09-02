package hrds.control.constans;

public enum RunTypeConstant implements EnumConstantInterface {
	RUN_NOW(1), RUN_ONTIME(2), RUN_FILE_SIGNAL(3), RUN_COMMAND_LINE(4);

	private final int code;

	RunTypeConstant(int code) {

		this.code = code;
	}

	public int getCode() {

		return code;
	}

	@Override
	public String toString() {

		return String.valueOf(code);
	}
}
