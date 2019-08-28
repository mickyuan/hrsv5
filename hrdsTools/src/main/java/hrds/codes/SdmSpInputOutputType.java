package hrds.codes;
/**Created by automatic  */
/**代码类型名：StreamingPro输入输出的类型  */
public enum SdmSpInputOutputType {
	/**文本文件<WENBENWENJIAN>  */
	WENBENWENJIAN("1","文本文件","151"),
	/**数据库表<SHUJUKUBIAO>  */
	SHUJUKUBIAO("2","数据库表","151"),
	/**消费主题<XIAOFEIZHUTI>  */
	XIAOFEIZHUTI("3","消费主题","151"),
	/**内部表<NEIBUBIAO>  */
	NEIBUBIAO("4","内部表","151");

	private final String code;
	private final String value;
	private final String catCode;

	SdmSpInputOutputType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
