package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：ETl作业有效标志  */
public enum Job_Effective_Flag {
	/**有效(Y)<YES>  */
	YES("Y","有效(Y)","24","ETl作业有效标志"),
	/**无效(N)<NO>  */
	NO("N","无效(N)","24","ETl作业有效标志"),
	/**空跑(V)<VIRTUAL>  */
	VIRTUAL("V","空跑(V)","24","ETl作业有效标志");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Job_Effective_Flag(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Job_Effective_Flag";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Job_Effective_Flag typeCode : Job_Effective_Flag.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static Job_Effective_Flag ofEnumByCode(String code) {
		for (Job_Effective_Flag typeCode : Job_Effective_Flag.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String ofCatValue(){
		return Job_Effective_Flag.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Job_Effective_Flag.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	@Override
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
