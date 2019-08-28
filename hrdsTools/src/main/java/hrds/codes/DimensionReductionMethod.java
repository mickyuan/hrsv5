package hrds.codes;
/**Created by automatic  */
/**代码类型名：降维分析方法  */
public enum DimensionReductionMethod {
	/**主成分分析<ZhuChengFenFenXi>  */
	ZhuChengFenFenXi("1","主成分分析","59"),
	/**因子分析<YinZiFenXi>  */
	YinZiFenXi("2","因子分析","59");

	private final String code;
	private final String value;
	private final String catCode;

	DimensionReductionMethod(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
