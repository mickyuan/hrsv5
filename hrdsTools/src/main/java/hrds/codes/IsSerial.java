package hrds.codes;
/**Created by automatic  */
/**代码类型名：串行并行  */
public enum IsSerial {
	/**串行<chuanxing>  */
	chuanxing("1","串行","17"),
	/**并行<BingXing>  */
	BingXing("2","并行","17");

	private final String code;
	private final String value;
	private final String catCode;

	IsSerial(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
