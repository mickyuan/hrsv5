package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：具体算法  */
public enum SpecificAlgorithm {
	/**逻辑回归<LuoJiHuiGui>  */
	LuoJiHuiGui("01","逻辑回归","60","具体算法"),
	/**朴素贝叶斯<PuShuBeiYeSi>  */
	PuShuBeiYeSi("02","朴素贝叶斯","60","具体算法"),
	/**SVM<SVM>  */
	SVM("03","SVM","60","具体算法"),
	/**线性回归<XianXingHuiGui>  */
	XianXingHuiGui("04","线性回归","60","具体算法");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SpecificAlgorithm(String code,String value,String catCode,String catValue){
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
		for (SpecificAlgorithm typeCode : SpecificAlgorithm.values()) {
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
	public static SpecificAlgorithm getCodeObj(String code) {
		for (SpecificAlgorithm typeCode : SpecificAlgorithm.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return SpecificAlgorithm.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SpecificAlgorithm.values()[0].getCatCode();
	}
}
