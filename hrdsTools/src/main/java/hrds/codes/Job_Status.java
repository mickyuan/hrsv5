package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL作业状态  */
public enum Job_Status {
	/**完成<DONE>  */
	DONE("D","完成","109","ETL作业状态"),
	/**错误<ERROR>  */
	ERROR("E","错误","109","ETL作业状态"),
	/**挂起<PENDING>  */
	PENDING("P","挂起","109","ETL作业状态"),
	/**运行<RUNNING>  */
	RUNNING("R","运行","109","ETL作业状态"),
	/**停止<STOP>  */
	STOP("S","停止","109","ETL作业状态"),
	/**等待<WAITING>  */
	WAITING("W","等待","109","ETL作业状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Job_Status(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (Job_Status typeCode : Job_Status.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static Job_Status getCodeObj(String code) {
		for (Job_Status typeCode : Job_Status.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据code没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return Job_Status.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return Job_Status.values()[0].getCatCode();
	}
}
