package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：机器学习数据运行状态  */
public enum DataTableRunState {
	/**未开始<WeiKaiShi>  */
	WeiKaiShi("0","未开始","56","机器学习数据运行状态"),
	/**组建中<ZuJianZhong>  */
	ZuJianZhong("1","组建中","56","机器学习数据运行状态"),
	/**组建完成<ZuJianWanCheng>  */
	ZuJianWanCheng("2","组建完成","56","机器学习数据运行状态"),
	/**组建失败<ZuJianShiBai>  */
	ZuJianShiBai("3","组建失败","56","机器学习数据运行状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DataTableRunState(String code,String value,String catCode,String catValue){
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
		for (DataTableRunState typeCode : DataTableRunState.values()) {
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
	public static DataTableRunState getCodeObj(String code) {
		for (DataTableRunState typeCode : DataTableRunState.values()) {
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
		return DataTableRunState.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DataTableRunState.values()[0].getCatCode();
	}
}
