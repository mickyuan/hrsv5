package hrds.codes;
/**Created by automatic  */
/**代码类型名：DB文件格式  */
public enum FileFormat {
	/**定长<DingChang>  */
	DingChang("0","定长","47"),
	/**非定长<FeiDingChang>  */
	FeiDingChang("1","非定长","47"),
	/**CSV<CSV>  */
	CSV("2","CSV","47");

	private final String code;
	private final String value;
	private final String catCode;

	FileFormat(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
