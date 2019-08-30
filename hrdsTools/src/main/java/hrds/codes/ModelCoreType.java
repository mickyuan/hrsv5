package hrds.codes;
/**Created by automatic  */
/**代码类型名：模型核心类型  */
public enum ModelCoreType {
	/**线性核<XianXingHe>  */
	XianXingHe("1","线性核","63","模型核心类型"),
	/**多项式核<DuoXiangShiHe>  */
	DuoXiangShiHe("2","多项式核","63","模型核心类型"),
	/**径向基核<JiangXiangJiHe>  */
	JiangXiangJiHe("3","径向基核","63","模型核心类型"),
	/**sigmoid<sigmoid>  */
	sigmoid("4","sigmoid","63","模型核心类型"),
	/**预计算<YuJiSuan>  */
	YuJiSuan("5","预计算","63","模型核心类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ModelCoreType(String code,String value,String catCode,String catValue){
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
		for (ModelCoreType typeCode : ModelCoreType.values()) {
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
	public static ModelCoreType getCodeObj(String code) {
		for (ModelCoreType typeCode : ModelCoreType.values()) {
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
		return ModelCoreType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ModelCoreType.values()[0].getCatCode();
	}
}
