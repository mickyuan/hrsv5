package hrds.codes;
/**Created by automatic  */
/**代码类型名：降维分析方法  */
public enum DimensionReductionMethod {
	/**主成分分析<ZhuChengFenFenXi>  */
	ZhuChengFenFenXi("1","主成分分析","59","降维分析方法"),
	/**因子分析<YinZiFenXi>  */
	YinZiFenXi("2","因子分析","59","降维分析方法");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DimensionReductionMethod(String code,String value,String catCode,String catValue){
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
		for (DimensionReductionMethod typeCode : DimensionReductionMethod.values()) {
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
	public static DimensionReductionMethod getCodeObj(String code) {
		for (DimensionReductionMethod typeCode : DimensionReductionMethod.values()) {
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
		return DimensionReductionMethod.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DimensionReductionMethod.values()[0].getCatCode();
	}
}
