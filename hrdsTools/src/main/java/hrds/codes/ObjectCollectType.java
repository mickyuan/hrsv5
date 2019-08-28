package hrds.codes;
/**Created by automatic  */
/**代码类型名：对象采集方式  */
public enum ObjectCollectType {
	/**行采集<HangCaiJi>  */
	HangCaiJi("1","行采集","123"),
	/**对象采集<DuiXiangCaiJi>  */
	DuiXiangCaiJi("2","对象采集","123");

	private final String code;
	private final String value;
	private final String catCode;

	ObjectCollectType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
