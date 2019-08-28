package hrds.codes;
/**Created by automatic  */
/**代码类型名：报表条件过滤方式  */
public enum ConditionalFilter {
	/**文本<WenBen>  */
	WenBen("1","文本","93"),
	/**单选下拉<DanXuanXiaLa>  */
	DanXuanXiaLa("2","单选下拉","93"),
	/**多选下拉<DuoXuanXiaLa>  */
	DuoXuanXiaLa("3","多选下拉","93");

	private final String code;
	private final String value;
	private final String catCode;

	ConditionalFilter(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
