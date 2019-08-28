package hrds.codes;
/**Created by automatic  */
/**代码类型名：通知等级  */
public enum NoticeLevel {
	/**所有<All>  */
	All("1","所有","15"),
	/**失败<Succeed>  */
	Succeed("2","失败","15"),
	/**成功<Failure>  */
	Failure("3","成功","15");

	private final String code;
	private final String value;
	private final String catCode;

	NoticeLevel(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
