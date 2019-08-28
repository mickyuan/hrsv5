package hrds.codes;
/**Created by automatic  */
/**代码类型名：估计回归方法  */
public enum RegressionMethodEstimation {
	/**岭回归<LingHuiGui>  */
	LingHuiGui("1","岭回归","54"),
	/**Lasso回归<LassoHuiGui>  */
	LassoHuiGui("2","Lasso回归","54"),
	/**包含截距项<BaoHanJieJuXiang>  */
	BaoHanJieJuXiang("3","包含截距项","54");

	private final String code;
	private final String value;
	private final String catCode;

	RegressionMethodEstimation(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
