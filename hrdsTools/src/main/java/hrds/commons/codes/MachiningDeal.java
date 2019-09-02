package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：加工层字段处理方式  */
public enum MachiningDeal {
	/**映射<YINGSHE>  */
	YINGSHE("1","映射","96","加工层字段处理方式"),
	/**规则mapping<GUIZEMAPPING>  */
	GUIZEMAPPING("2","规则mapping","96","加工层字段处理方式"),
	/**去空<QUKONG>  */
	QUKONG("3","去空","96","加工层字段处理方式"),
	/**映射并添加<YINGSHEBINGTIANJIA>  */
	YINGSHEBINGTIANJIA("4","映射并添加","96","加工层字段处理方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	MachiningDeal(String code,String value,String catCode,String catValue){
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
		for (MachiningDeal typeCode : MachiningDeal.values()) {
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
	public static MachiningDeal getCodeObj(String code) {
		for (MachiningDeal typeCode : MachiningDeal.values()) {
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
		return MachiningDeal.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return MachiningDeal.values()[0].getCatCode();
	}
}
