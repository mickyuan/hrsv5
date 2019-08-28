package hrds.codes;
/**Created by automatic  */
/**代码类型名：异常值处理标识条件  */
public enum OutlierProcessCondition {
	/**百分比<BaiFenBi>  */
	BaiFenBi("1","百分比","74"),
	/**固定数量<GuDingShuLiang>  */
	GuDingShuLiang("2","固定数量","74"),
	/**分界值<FenJieZhi>  */
	FenJieZhi("3","分界值","74"),
	/**标准差<BiaoZhunCha>  */
	BiaoZhunCha("4","标准差","74");

	private final String code;
	private final String value;
	private final String catCode;

	OutlierProcessCondition(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
