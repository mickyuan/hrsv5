package hrds.codes;
/**Created by automatic  */
/**代码类型名：自主取数取数状态  */
public enum AutoFetchStatus {
	/**编辑<BianJi>  */
	BianJi("01","编辑","157","自主取数取数状态"),
	/**完成<WanCheng>  */
	WanCheng("04","完成","157","自主取数取数状态"),
	/**注销<ZhuXiao>  */
	ZhuXiao("05","注销","157","自主取数取数状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AutoFetchStatus(String code,String value,String catCode,String catValue){
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
		for (AutoFetchStatus typeCode : AutoFetchStatus.values()) {
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
	public static AutoFetchStatus getCodeObj(String code) {
		for (AutoFetchStatus typeCode : AutoFetchStatus.values()) {
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
		return AutoFetchStatus.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return AutoFetchStatus.values()[0].getCatCode();
	}
}
