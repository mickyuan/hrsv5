package hrds.codes;
/**Created by automatic  */
/**代码类型名：独立样本类型  */
public enum IndependentSampleStyle {
	/**配对样本t检验<PeiDuiYangBenTJianYan>  */
	PeiDuiYangBenTJianYan("1","配对样本t检验","50","独立样本类型"),
	/**等方差双样本检验<DengFangChaShuangYangBenJianYan>  */
	DengFangChaShuangYangBenJianYan("2","等方差双样本检验","50","独立样本类型"),
	/**异方差双样本检验<YiFangChaShuangYangBenJianYan>  */
	YiFangChaShuangYangBenJianYan("3","异方差双样本检验","50","独立样本类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	IndependentSampleStyle(String code,String value,String catCode,String catValue){
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
		for (IndependentSampleStyle typeCode : IndependentSampleStyle.values()) {
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
	public static IndependentSampleStyle getCodeObj(String code) {
		for (IndependentSampleStyle typeCode : IndependentSampleStyle.values()) {
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
		return IndependentSampleStyle.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return IndependentSampleStyle.values()[0].getCatCode();
	}
}
