package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据质量执行方式  */
public enum DqcExecMode {
	/**手工<ShouGong>  */
	ShouGong("MAN","手工","140"),
	/**自动<ZiDong>  */
	ZiDong("AUTO","自动","140");

	private final String code;
	private final String value;
	private final String catCode;

	DqcExecMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
