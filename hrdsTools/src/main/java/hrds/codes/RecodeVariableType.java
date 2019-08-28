package hrds.codes;
/**Created by automatic  */
/**代码类型名：重编码变量类型  */
public enum RecodeVariableType {
	/**分类变量<FenLeiBianLiang>  */
	FenLeiBianLiang("1","分类变量","80"),
	/**数值变量<ShuZhiBianLiang>  */
	ShuZhiBianLiang("2","数值变量","80");

	private final String code;
	private final String value;
	private final String catCode;

	RecodeVariableType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
