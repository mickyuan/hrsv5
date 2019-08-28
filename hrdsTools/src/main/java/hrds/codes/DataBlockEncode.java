package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据块编码  */
public enum DataBlockEncode {
	/**NONE<NONE>  */
	NONE("1","NONE","132"),
	/**PREFIX<PREFIX>  */
	PREFIX("2","PREFIX","132"),
	/**DIFF<DIFF>  */
	DIFF("3","DIFF","132"),
	/**FAST_DIFF<FAST_DIFF>  */
	FAST_DIFF("4","FAST_DIFF","132"),
	/**PREFIX_TREE<PREFIX_TREE>  */
	PREFIX_TREE("5","PREFIX_TREE","132");

	private final String code;
	private final String value;
	private final String catCode;

	DataBlockEncode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
