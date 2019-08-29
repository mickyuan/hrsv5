package hrds.codes;
/**Created by automatic  */
/**代码类型名：模型类型  */
public enum ModelType {
	/**分类模型<FenLeiMoXing>  */
	FenLeiMoXing("1","分类模型","91","模型类型"),
	/**回归模型<JuLeiMoXing>  */
	JuLeiMoXing("2","回归模型","91","模型类型"),
	/**预测模型<YuCeMoXing>  */
	YuCeMoXing("3","预测模型","91","模型类型"),
	/**其它算法<QiTaSuanFa>  */
	QiTaSuanFa("4","其它算法","91","模型类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ModelType(String code,String value,String catCode,String catValue){
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
		for (ModelType typeCode : ModelType.values()) {
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
	public static ModelType getCodeObj(String code) {
		for (ModelType typeCode : ModelType.values()) {
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
		return ModelType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ModelType.values()[0].getCatCode();
	}
}
