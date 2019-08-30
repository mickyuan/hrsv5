package hrds.codes;
/**Created by automatic  */
/**代码类型名：作业程序类型  */
public enum ProType {
	/**c<C>  */
	C("01","c","12","作业程序类型"),
	/**c++<CAdd>  */
	CAdd("02","c++","12","作业程序类型"),
	/**JAVA<Java>  */
	Java("03","JAVA","12","作业程序类型"),
	/**C#<CStudio>  */
	CStudio("04","C#","12","作业程序类型"),
	/**shell<Shell>  */
	Shell("05","shell","12","作业程序类型"),
	/**perl<Perl>  */
	Perl("06","perl","12","作业程序类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ProType(String code,String value,String catCode,String catValue){
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
		for (ProType typeCode : ProType.values()) {
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
	public static ProType getCodeObj(String code) {
		for (ProType typeCode : ProType.values()) {
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
		return ProType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ProType.values()[0].getCatCode();
	}
}
