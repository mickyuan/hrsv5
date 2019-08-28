package hrds.codes;
/**Created by automatic  */
/**代码类型名：模型核心类型  */
public enum ModelCoreType {
	/**线性核<XianXingHe>  */
	XianXingHe("1","线性核","63"),
	/**多项式核<DuoXiangShiHe>  */
	DuoXiangShiHe("2","多项式核","63"),
	/**径向基核<JiangXiangJiHe>  */
	JiangXiangJiHe("3","径向基核","63"),
	/**sigmoid<sigmoid>  */
	sigmoid("4","sigmoid","63"),
	/**预计算<YuJiSuan>  */
	YuJiSuan("5","预计算","63");

	private final String code;
	private final String value;
	private final String catCode;

	ModelCoreType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
