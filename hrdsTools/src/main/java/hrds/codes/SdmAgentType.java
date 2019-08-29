package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理Agent类别  */
public enum SdmAgentType {
	/**文本流Agent<WenBenLiu>  */
	WenBenLiu("1","文本流Agent","98","流数据管理Agent类别"),
	/**Rest接收Agent<RestJieShou>  */
	RestJieShou("2","Rest接收Agent","98","流数据管理Agent类别");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SdmAgentType(String code,String value,String catCode,String catValue){
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
		for (SdmAgentType typeCode : SdmAgentType.values()) {
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
	public static SdmAgentType getCodeObj(String code) {
		for (SdmAgentType typeCode : SdmAgentType.values()) {
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
		return SdmAgentType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SdmAgentType.values()[0].getCatCode();
	}
}
