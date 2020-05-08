package hrds.commons.enumtools;

import hrds.commons.exception.ExceptionMessage;

/**
 * ClassName: StageConstant <br/>
 * Function: 阶段枚举 <br/>
 * Reason: 用于标识采集作业的各个阶段
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public enum StageConstant {

	UNLOADDATA(1, "unloaddata"), UPLOAD(2, "upload"), DATALOADING(3, "dataloading"),
	CALINCREMENT(4, "calincrement"), DATAREGISTRATION(5, "dataregistration");

	private final int code;
	private final String desc;

	StageConstant(int code, String desc) {

		this.code = code;
		this.desc = desc;
	}

	public String getDesc() {

		return desc;
	}
	public int getCode(){
		return code;
	}

	@Override
	public String toString() {

		return "StageConstant{" +
						"code=" + code +
						", desc='" + desc + '\'' +
						'}';
	}
}
