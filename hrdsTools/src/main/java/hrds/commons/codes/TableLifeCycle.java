package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：数据表生命周期  */
public enum TableLifeCycle {
	/**永久<YongJiu>  */
	YongJiu("1","永久","38","数据表生命周期"),
	/**临时<LinShi>  */
	LinShi("2","临时","38","数据表生命周期");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	TableLifeCycle(String code,String value,String catCode,String catValue){
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
		for (TableLifeCycle typeCode : TableLifeCycle.values()) {
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
	public static TableLifeCycle getCodeObj(String code) {
		for (TableLifeCycle typeCode : TableLifeCycle.values()) {
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
		return TableLifeCycle.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return TableLifeCycle.values()[0].getCatCode();
	}
}
