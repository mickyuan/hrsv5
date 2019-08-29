package hrds.codes;
/**Created by automatic  */
/**代码类型名：报表发布状态  */
public enum RPublishStatus {
	/**未发布<WeiFaBu>  */
	WeiFaBu("1","未发布","101","报表发布状态"),
	/**已发布<YiFaBu>  */
	YiFaBu("2","已发布","101","报表发布状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	RPublishStatus(String code,String value,String catCode,String catValue){
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
		for (RPublishStatus typeCode : RPublishStatus.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static RPublishStatus getCodeObj(String code) {
		for (RPublishStatus typeCode : RPublishStatus.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return RPublishStatus.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return RPublishStatus.values()[0].getCatCode();
	}
}
