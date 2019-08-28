package hrds.codes;
/**Created by automatic  */
/**代码类型名：压缩格式  */
public enum ReduceType {
	/**tar<TAR>  */
	TAR("1","tar","120"),
	/**gz<GZ>  */
	GZ("2","gz","120"),
	/**zip<ZIP>  */
	ZIP("3","zip","120"),
	/**none<NONE>  */
	NONE("4","none","120");

	private final String code;
	private final String value;
	private final String catCode;

	ReduceType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
