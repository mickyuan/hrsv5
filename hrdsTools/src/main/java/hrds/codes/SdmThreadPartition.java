package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理线程与分区的关系  */
public enum SdmThreadPartition {
	/**一对一<YiDuiYi>  */
	YiDuiYi("1","一对一","105"),
	/**一对多<YiDuiDuo>  */
	YiDuiDuo("2","一对多","105");

	private final String code;
	private final String value;
	private final String catCode;

	SdmThreadPartition(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
