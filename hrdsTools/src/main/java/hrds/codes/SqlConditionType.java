package hrds.codes;
/**Created by automatic  */
/**代码类型名：sql条件类型  */
public enum SqlConditionType {
	/**where条件<where>  */
	where("1","where条件","92"),
	/**分组条件<groupby>  */
	groupby("2","分组条件","92"),
	/**连接条件<join>  */
	join("3","连接条件","92");

	private final String code;
	private final String value;
	private final String catCode;

	SqlConditionType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
