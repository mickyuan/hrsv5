package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：用户状态  */
public enum UserState {
	/**正常<ZhengChang>  */
	ZhengChang("1","正常","3","用户状态"),
	/**禁用<JinYong>  */
	JinYong("2","禁用","3","用户状态"),
	/**删除<ShanChu>  */
	ShanChu("3","删除","3","用户状态"),
	/**正在使用<ZhengZaiShiYong>  */
	ZhengZaiShiYong("4","正在使用","3","用户状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	UserState(String code,String value,String catCode,String catValue){
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
		for (UserState typeCode : UserState.values()) {
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
	public static UserState getCodeObj(String code) {
		for (UserState typeCode : UserState.values()) {
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
		return UserState.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return UserState.values()[0].getCatCode();
	}
}
