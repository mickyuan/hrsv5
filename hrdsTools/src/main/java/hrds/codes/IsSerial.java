package hrds.codes;
/**Created by automatic  */
/**代码类型名：串行并行  */
public enum IsSerial {
	/**串行<chuanxing>  */
	chuanxing("1","串行","17","串行并行"),
	/**并行<BingXing>  */
	BingXing("2","并行","17","串行并行");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	IsSerial(String code,String value,String catCode,String catValue){
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
		for (IsSerial typeCode : IsSerial.values()) {
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
	public static IsSerial getCodeObj(String code) {
		for (IsSerial typeCode : IsSerial.values()) {
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
		return IsSerial.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return IsSerial.values()[0].getCatCode();
	}
}
