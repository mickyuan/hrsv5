package hrds.codes;
/**Created by automatic  */
/**代码类型名：用户状态  */
public enum UserState {
	/**正常<ZhengChang>  */
	ZhengChang("1","正常","3"),
	/**禁用<JinYong>  */
	JinYong("2","禁用","3"),
	/**删除<ShanChu>  */
	ShanChu("3","删除","3"),
	/**正在使用<ZhengZaiShiYong>  */
	ZhengZaiShiYong("4","正在使用","3");

	private final String code;
	private final String value;
	private final String catCode;

	UserState(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
