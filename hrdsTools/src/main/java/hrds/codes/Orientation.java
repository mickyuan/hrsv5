package hrds.codes;
/**Created by automatic  */
/**代码类型名：上下左右  */
public enum Orientation {
	/**上<top>  */
	top("0","上","34"),
	/**下<bottom>  */
	bottom("1","下","34"),
	/**左<left>  */
	left("2","左","34"),
	/**右<right>  */
	right("3","右","34");

	private final String code;
	private final String value;
	private final String catCode;

	Orientation(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
