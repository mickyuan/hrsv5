package hrds.codes;
/**Created by automatic  */
/**代码类型名：初始化聚类中心方式  */
public enum InitializeClusterMode {
	/**打开数据集<DaKaiShuJuJi>  */
	DaKaiShuJuJi("1","打开数据集","49"),
	/**导入外部数据<DaoRuWaiBuShuJu>  */
	DaoRuWaiBuShuJu("2","导入外部数据","49");

	private final String code;
	private final String value;
	private final String catCode;

	InitializeClusterMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
