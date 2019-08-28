package hrds.codes;
/**Created by automatic  */
/**代码类型名：异常值处理处理方式  */
public enum OutlierProcessMode {
	/**删除<ShanChu>  */
	ShanChu("1","删除","75"),
	/**替换<TiHuan>  */
	TiHuan("2","替换","75");

	private final String code;
	private final String value;
	private final String catCode;

	OutlierProcessMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
