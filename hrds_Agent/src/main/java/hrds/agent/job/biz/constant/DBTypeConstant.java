package hrds.agent.job.biz.constant;

/**
 * ClassName: DBTypeConstant <br/>
 * Function: 数据库类型枚举 <br/>
 * Reason: 数据库直连采集使用,内容是按照数据库E-R设计构建的
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public enum DBTypeConstant implements EnumConstantInterface {
	MYSQL(1, "MySQL"), ORACLE9IFOLLOW(2, "Oracle9ifollowing"), ORACLE10GABOV(3, "Oracle10gabove"),
	SQLSERVRER2000(4, "SQLServrer2000"), SQLSERVRER2005(5, "SQLServrer2005"), DB2(6, "DB2"),
	SYBASE(7, "SybaseASE12.5above"), INFORMATIC(8, "Informatic"), H2(9, "H2"),
	APACHEDERBY(10, "ApacheDerby"), POSTGRESQL(11, "PostgreSQL"), GBASE(12, "Gbase"),
	TERADATA(13, "TeraData");

	private final int code;
	private final String desc;

	DBTypeConstant(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	@Override
	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "DBTypeConstant{" +
				"code='" + code + '\'' +
				", desc='" + desc + '\'' +
				'}';
	}
}
