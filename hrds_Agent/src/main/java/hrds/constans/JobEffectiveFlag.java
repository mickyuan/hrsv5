package hrds.constans;

/**
 * @ClassName: JobEffectiveFlag
 * @Description: 代码项：ETL作业有效标识
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 15:42
 * @Since: JDK 1.8
 **/
public enum JobEffectiveFlag {
	YES("Y", "有效"), NO("N", "无效"), VIRTUAL("V", "空跑");

	private String code;
	private String message;

	JobEffectiveFlag(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {

		return code;
	}

	public String getMessage() {

		return message;
	}

	@Override
	public String toString() {

		return String.format("{'code' : %s, 'message' : %s}", code, message);
	}
}
