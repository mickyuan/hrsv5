package hrds.codes;
/**Created by automatic  */
/**代码类型名：算法类型  */
public enum AlgorithmType {
	/**分类<FenLei>  */
	FenLei("1","分类","71","算法类型"),
	/**回归<HuiGui>  */
	HuiGui("2","回归","71","算法类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AlgorithmType(String code,String value,String catCode,String catValue){
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
		for (AlgorithmType typeCode : AlgorithmType.values()) {
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
	public static AlgorithmType getCodeObj(String code) {
		for (AlgorithmType typeCode : AlgorithmType.values()) {
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
		return AlgorithmType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return AlgorithmType.values()[0].getCatCode();
	}
}
