package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习数据运行状态  */
public enum DataTableRunState {
	/**未开始<WeiKaiShi>  */
	WeiKaiShi("0","未开始","56"),
	/**组建中<ZuJianZhong>  */
	ZuJianZhong("1","组建中","56"),
	/**组建完成<ZuJianWanCheng>  */
	ZuJianWanCheng("2","组建完成","56"),
	/**组建失败<ZuJianShiBai>  */
	ZuJianShiBai("3","组建失败","56");

	private final String code;
	private final String value;
	private final String catCode;

	DataTableRunState(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
