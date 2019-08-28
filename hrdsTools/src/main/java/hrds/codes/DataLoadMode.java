package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据加载方式  */
public enum DataLoadMode {
	/**SQL执行<SqlZhiXing>  */
	SqlZhiXing("1","SQL执行","69"),
	/**调用其它程序<DiaoYongQiTaChengXu>  */
	DiaoYongQiTaChengXu("2","调用其它程序","69");

	private final String code;
	private final String value;
	private final String catCode;

	DataLoadMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
