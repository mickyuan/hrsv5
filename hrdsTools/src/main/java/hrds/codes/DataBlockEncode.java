package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据块编码  */
public enum DataBlockEncode {
	/**NONE<NONE>  */
	NONE("1","NONE","132","数据块编码"),
	/**PREFIX<PREFIX>  */
	PREFIX("2","PREFIX","132","数据块编码"),
	/**DIFF<DIFF>  */
	DIFF("3","DIFF","132","数据块编码"),
	/**FAST_DIFF<FAST_DIFF>  */
	FAST_DIFF("4","FAST_DIFF","132","数据块编码"),
	/**PREFIX_TREE<PREFIX_TREE>  */
	PREFIX_TREE("5","PREFIX_TREE","132","数据块编码");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DataBlockEncode(String code,String value,String catCode,String catValue){
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
		for (DataBlockEncode typeCode : DataBlockEncode.values()) {
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
	public static DataBlockEncode getCodeObj(String code) {
		for (DataBlockEncode typeCode : DataBlockEncode.values()) {
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
		return DataBlockEncode.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DataBlockEncode.values()[0].getCatCode();
	}
}
