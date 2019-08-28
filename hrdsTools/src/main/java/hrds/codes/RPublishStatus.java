package hrds.codes;
/**Created by automatic  */
/**代码类型名：报表发布状态  */
public enum RPublishStatus {
	/**未发布<WeiFaBu>  */
	WeiFaBu("1","未发布","101"),
	/**已发布<YiFaBu>  */
	YiFaBu("2","已发布","101");

	private final String code;
	private final String value;
	private final String catCode;

	RPublishStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
