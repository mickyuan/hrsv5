package hrds.codes;
/**Created by automatic  */
/**代码类型名：自主取数取数状态  */
public enum AutoFetchStatus {
	/**编辑<BianJi>  */
	BianJi("01","编辑","157"),
	/**完成<WanCheng>  */
	WanCheng("04","完成","157"),
	/**注销<ZhuXiao>  */
	ZhuXiao("05","注销","157");

	private final String code;
	private final String value;
	private final String catCode;

	AutoFetchStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
