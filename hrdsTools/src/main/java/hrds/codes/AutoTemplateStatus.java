package hrds.codes;
/**Created by automatic  */
/**代码类型名：自主取数模板状态  */
public enum AutoTemplateStatus {
	/**编辑<BianJi>  */
	BianJi("01","编辑","155"),
	/**发布<FaBu>  */
	FaBu("04","发布","155"),
	/**注销<ZhuXiao>  */
	ZhuXiao("05","注销","155");

	private final String code;
	private final String value;
	private final String catCode;

	AutoTemplateStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
