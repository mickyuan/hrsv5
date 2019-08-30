package hrds.codes;
/**Created by automatic  */
/**代码类型名：异常值处理标识条件  */
public enum OutlierProcessCondition {
	/**百分比<BaiFenBi>  */
	BaiFenBi("1","百分比","74","异常值处理标识条件"),
	/**固定数量<GuDingShuLiang>  */
	GuDingShuLiang("2","固定数量","74","异常值处理标识条件"),
	/**分界值<FenJieZhi>  */
	FenJieZhi("3","分界值","74","异常值处理标识条件"),
	/**标准差<BiaoZhunCha>  */
	BiaoZhunCha("4","标准差","74","异常值处理标识条件");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	OutlierProcessCondition(String code,String value,String catCode,String catValue){
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
		for (OutlierProcessCondition typeCode : OutlierProcessCondition.values()) {
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
	public static OutlierProcessCondition getCodeObj(String code) {
		for (OutlierProcessCondition typeCode : OutlierProcessCondition.values()) {
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
		return OutlierProcessCondition.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return OutlierProcessCondition.values()[0].getCatCode();
	}
}
