package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据质量规则级别  */
public enum EdRuleLevel {
	/**警告<JingGao>  */
	JingGao("0","警告","137"),
	/**严重<YanZhong>  */
	YanZhong("1","严重","137");

	private final String code;
	private final String value;
	private final String catCode;

	EdRuleLevel(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
