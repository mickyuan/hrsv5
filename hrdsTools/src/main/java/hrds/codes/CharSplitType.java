package hrds.codes;
/**Created by automatic  */
/**代码类型名：字符拆分方式  */
public enum CharSplitType {
	/**偏移量<PianYiLiang>  */
	PianYiLiang("1","偏移量","142"),
	/**自定符号<ZhiDingFuHao>  */
	ZhiDingFuHao("2","自定符号","142");

	private final String code;
	private final String value;
	private final String catCode;

	CharSplitType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
