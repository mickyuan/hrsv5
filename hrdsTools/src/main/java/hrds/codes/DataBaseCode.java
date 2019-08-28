package hrds.codes;
/**Created by automatic  */
/**代码类型名：采集编码  */
public enum DataBaseCode {
	/**UTF-8<UTF_8>  */
	UTF_8("1","UTF-8","35"),
	/**GBK<GBK>  */
	GBK("2","GBK","35"),
	/**UTF-16<UTF_16>  */
	UTF_16("3","UTF-16","35"),
	/**GB2312<GB2312>  */
	GB2312("4","GB2312","35"),
	/**ISO-8859-1<ISO_8859_1>  */
	ISO_8859_1("5","ISO-8859-1","35");

	private final String code;
	private final String value;
	private final String catCode;

	DataBaseCode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
