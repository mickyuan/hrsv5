package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：发布状态  */
public enum PublishStatus {
	/**未发布<WeiFaBu>  */
	WeiFaBu("0","未发布","51","发布状态"),
	/**已发布<YiFaBu>  */
	YiFaBu("1","已发布","51","发布状态"),
	/**已下线<YiXiaXian>  */
	YiXiaXian("2","已下线","51","发布状态"),
	/**已删除<YiShanChu>  */
	YiShanChu("3","已删除","51","发布状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	PublishStatus(String code,String value,String catCode,String catValue){
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
		for (PublishStatus typeCode : PublishStatus.values()) {
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
	public static PublishStatus getCodeObj(String code) {
		for (PublishStatus typeCode : PublishStatus.values()) {
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
		return PublishStatus.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return PublishStatus.values()[0].getCatCode();
	}
}
