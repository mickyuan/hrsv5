package hrds.codes;
/**Created by automatic  */
/**代码类型名：通知等级  */
public enum NoticeLevel {
	/**所有<All>  */
	All("1","所有","15","通知等级"),
	/**失败<Succeed>  */
	Succeed("2","失败","15","通知等级"),
	/**成功<Failure>  */
	Failure("3","成功","15","通知等级");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	NoticeLevel(String code,String value,String catCode,String catValue){
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
		for (NoticeLevel typeCode : NoticeLevel.values()) {
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
	public static NoticeLevel getCodeObj(String code) {
		for (NoticeLevel typeCode : NoticeLevel.values()) {
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
		return NoticeLevel.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return NoticeLevel.values()[0].getCatCode();
	}
}
