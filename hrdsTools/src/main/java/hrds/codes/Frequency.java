package hrds.codes;
/**Created by automatic  */
/**代码类型名：频率  */
public enum Frequency {
	/**自定义<ZiDingYi>  */
	ZiDingYi("0","自定义","7"),
	/**天<Tian>  */
	Tian("1","天","7"),
	/**周<Zhou>  */
	Zhou("2","周","7"),
	/**月<Yue>  */
	Yue("3","月","7"),
	/**季度<JiDu>  */
	JiDu("4","季度","7");

	private final String code;
	private final String value;
	private final String catCode;

	Frequency(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
