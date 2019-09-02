package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：估计回归方法  */
public enum RegressionMethodEstimation {
	/**岭回归<LingHuiGui>  */
	LingHuiGui("1","岭回归","54","估计回归方法"),
	/**Lasso回归<LassoHuiGui>  */
	LassoHuiGui("2","Lasso回归","54","估计回归方法"),
	/**包含截距项<BaoHanJieJuXiang>  */
	BaoHanJieJuXiang("3","包含截距项","54","估计回归方法");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	RegressionMethodEstimation(String code,String value,String catCode,String catValue){
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
		for (RegressionMethodEstimation typeCode : RegressionMethodEstimation.values()) {
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
	public static RegressionMethodEstimation getCodeObj(String code) {
		for (RegressionMethodEstimation typeCode : RegressionMethodEstimation.values()) {
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
		return RegressionMethodEstimation.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return RegressionMethodEstimation.values()[0].getCatCode();
	}
}
