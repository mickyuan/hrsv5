package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL干预状态  */
public enum Meddle_status {
	/**完成<DONE>  */
	DONE("D","完成","115"),
	/**异常<ERROR>  */
	ERROR("E","异常","115"),
	/**失效<FALSE>  */
	FALSE("F","失效","115"),
	/**有效<TRUE>  */
	TRUE("T","有效","115"),
	/**干预中<RUNNING>  */
	RUNNING("R","干预中","115");

	private final String code;
	private final String value;
	private final String catCode;

	Meddle_status(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
