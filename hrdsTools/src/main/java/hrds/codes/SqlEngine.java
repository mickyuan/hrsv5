package hrds.codes;
/**Created by automatic  */
/**代码类型名：sql执行引擎  */
public enum SqlEngine {
	/**JDBC<JDBC>  */
	JDBC("1","JDBC","46"),
	/**SPARK<SPARK>  */
	SPARK("2","SPARK","46"),
	/**默认<MOREN>  */
	MOREN("3","默认","46");

	private final String code;
	private final String value;
	private final String catCode;

	SqlEngine(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
