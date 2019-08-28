package hrds.codes;
/**Created by automatic  */
/**代码类型名：算法评判标准  */
public enum AlgorithmCriteria {
	/**准确度<ZhunQueDu>  */
	ZhunQueDu("01","准确度","72"),
	/**R^2<RDePingFang>  */
	RDePingFang("02","R^2","72"),
	/**平均根误差<PingJunGenWuCha>  */
	PingJunGenWuCha("03","平均根误差","72"),
	/**平均绝对误差<PingJunJueDuiWuCha>  */
	PingJunJueDuiWuCha("04","平均绝对误差","72");

	private final String code;
	private final String value;
	private final String catCode;

	AlgorithmCriteria(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
