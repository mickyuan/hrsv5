package hrds.codes;
/**Created by automatic  */
/**代码类型名：可视化源对象  */
public enum AutoSourceObject {
	/**自主数据数据集<ZiZhuShuJuShuJuJi>  */
	ZiZhuShuJuShuJuJi("01","自主数据数据集","158"),
	/**系统级数据集<XiTongJiShuJuJi>  */
	XiTongJiShuJuJi("02","系统级数据集","158"),
	/**数据组件数据集<ShuJuZuJianShuJuJi>  */
	ShuJuZuJianShuJuJi("03","数据组件数据集","158");

	private final String code;
	private final String value;
	private final String catCode;

	AutoSourceObject(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
