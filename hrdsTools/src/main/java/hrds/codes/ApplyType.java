package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据申请类型  */
public enum ApplyType {
	/**查看<ChaKan>  */
	ChaKan("1","查看","18"),
	/**下载<XiaZai>  */
	XiaZai("2","下载","18"),
	/**发布<FaBu>  */
	FaBu("3","发布","18"),
	/**重命名<ChongMingMing>  */
	ChongMingMing("4","重命名","18");

	private final String code;
	private final String value;
	private final String catCode;

	ApplyType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
