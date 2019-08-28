package hrds.codes;
/**Created by automatic  */
/**代码类型名：发布状态  */
public enum PublishStatus {
	/**未发布<WeiFaBu>  */
	WeiFaBu("0","未发布","51"),
	/**已发布<YiFaBu>  */
	YiFaBu("1","已发布","51"),
	/**已下线<YiXiaXian>  */
	YiXiaXian("2","已下线","51"),
	/**已删除<YiShanChu>  */
	YiShanChu("3","已删除","51");

	private final String code;
	private final String value;
	private final String catCode;

	PublishStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
