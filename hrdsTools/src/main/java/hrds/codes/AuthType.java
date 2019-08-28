package hrds.codes;
/**Created by automatic  */
/**代码类型名：权限类型  */
public enum AuthType {
	/**允许<YunXu>  */
	YunXu("1","允许","19"),
	/**不允许<BuYunXu>  */
	BuYunXu("2","不允许","19"),
	/**一次<YiCi>  */
	YiCi("3","一次","19"),
	/**申请<ShenQing>  */
	ShenQing("0","申请","19");

	private final String code;
	private final String value;
	private final String catCode;

	AuthType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
