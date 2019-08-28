package hrds.codes;
/**Created by automatic  */
/**代码类型名：具体算法  */
public enum SpecificAlgorithm {
	/**逻辑回归<LuoJiHuiGui>  */
	LuoJiHuiGui("01","逻辑回归","60"),
	/**朴素贝叶斯<PuShuBeiYeSi>  */
	PuShuBeiYeSi("02","朴素贝叶斯","60"),
	/**SVM<SVM>  */
	SVM("03","SVM","60"),
	/**线性回归<XianXingHuiGui>  */
	XianXingHuiGui("04","线性回归","60");

	private final String code;
	private final String value;
	private final String catCode;

	SpecificAlgorithm(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
