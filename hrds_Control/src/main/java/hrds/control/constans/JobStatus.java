package hrds.control.constans;

/**
 * @ClassName: JobStatus
 * @Description: 代码项：ETL作业状态
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 16:07
 * @Since: JDK 1.8
 **/
public enum JobStatus {
	DONE("D", "完成"), ERROR("E", "错误"), PENDING("P", "挂起"),
	RUNNING("R", "运行"), STOP("S", "停止"), WAITING("W", "等待");

	private String code;
	private String message;

	JobStatus(String code, String message) {
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
