package hrds.codes;
/**Created by automatic  */
/**代码类型名：隐藏层单位类型  */
public enum HideLayerUnitType {
	/**自动计算<ZiDongJiSuan>  */
	ZiDongJiSuan("1","自动计算","77"),
	/**设定<SheDing>  */
	SheDing("2","设定","77");

	private final String code;
	private final String value;
	private final String catCode;

	HideLayerUnitType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
