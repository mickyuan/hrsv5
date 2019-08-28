package hrds.codes;
/**Created by automatic  */
/**代码类型名：算法类型  */
public enum AlgorithmType {
	/**分类<FenLei>  */
	FenLei("1","分类","71"),
	/**回归<HuiGui>  */
	HuiGui("2","回归","71");

	private final String code;
	private final String value;
	private final String catCode;

	AlgorithmType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
