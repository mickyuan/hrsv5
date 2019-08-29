package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理线程与分区的关系  */
public enum SdmThreadPartition {
	/**一对一<YiDuiYi>  */
	YiDuiYi("1","一对一","105","流数据管理线程与分区的关系"),
	/**一对多<YiDuiDuo>  */
	YiDuiDuo("2","一对多","105","流数据管理线程与分区的关系");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SdmThreadPartition(String code,String value,String catCode,String catValue){
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
		for (SdmThreadPartition typeCode : SdmThreadPartition.values()) {
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
	public static SdmThreadPartition getCodeObj(String code) {
		for (SdmThreadPartition typeCode : SdmThreadPartition.values()) {
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
		return SdmThreadPartition.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SdmThreadPartition.values()[0].getCatCode();
	}
}
