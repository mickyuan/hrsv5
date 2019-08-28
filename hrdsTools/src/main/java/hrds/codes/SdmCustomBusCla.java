package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理自定义业务类类型  */
public enum SdmCustomBusCla {
	/**None<NONE>  */
	NONE("0","None","141"),
	/**Java<Java>  */
	Java("1","Java","141"),
	/**JavaScript<JavaScript>  */
	JavaScript("2","JavaScript","141");

	private final String code;
	private final String value;
	private final String catCode;

	SdmCustomBusCla(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
