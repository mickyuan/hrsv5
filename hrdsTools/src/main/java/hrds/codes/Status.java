package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL状态  */
public enum Status {
	/**有效(T)<TRUE>  */
	TRUE("T","有效(T)","113"),
	/**失效(F)<FALSE>  */
	FALSE("F","失效(F)","113");

	private final String code;
	private final String value;
	private final String catCode;

	Status(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
