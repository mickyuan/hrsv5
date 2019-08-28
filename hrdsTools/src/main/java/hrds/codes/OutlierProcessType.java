package hrds.codes;
/**Created by automatic  */
/**代码类型名：异常值处理处理类型  */
public enum OutlierProcessType {
	/**平均值<PingJunZhi>  */
	PingJunZhi("1","平均值","76"),
	/**中位值<ZhongWeiZhi>  */
	ZhongWeiZhi("2","中位值","76"),
	/**自定义<ZiDingYi>  */
	ZiDingYi("3","自定义","76");

	private final String code;
	private final String value;
	private final String catCode;

	OutlierProcessType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
