package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据质量校验结果  */
public enum DqcVerifyResult {
	/**检查通过<ZhengChang>  */
	ZhengChang("0","检查通过","138"),
	/**数据异常<YiChang>  */
	YiChang("1","数据异常","138"),
	/**执行失败<ZhiXingShiBai>  */
	ZhiXingShiBai("2","执行失败","138");

	private final String code;
	private final String value;
	private final String catCode;

	DqcVerifyResult(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
