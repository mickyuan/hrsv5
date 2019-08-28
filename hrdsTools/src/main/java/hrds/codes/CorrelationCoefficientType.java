package hrds.codes;
/**Created by automatic  */
/**代码类型名：相关性系数类型  */
public enum CorrelationCoefficientType {
	/**Pearson相关系数<PearsonXiangGuanXiShu>  */
	PearsonXiangGuanXiShu("1","Pearson相关系数","82"),
	/**Spearman相关系数<SpearmanXiangGuanXiShu>  */
	SpearmanXiangGuanXiShu("2","Spearman相关系数","82");

	private final String code;
	private final String value;
	private final String catCode;

	CorrelationCoefficientType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
