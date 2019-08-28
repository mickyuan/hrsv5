package hrds.codes;
/**Created by automatic  */
/**代码类型名：缺失值处理类型  */
public enum MissValueProcessType {
	/**全部<QuanBu>  */
	QuanBu("1","全部","67"),
	/**K的个数<kDeGeShu>  */
	kDeGeShu("2","K的个数","67");

	private final String code;
	private final String value;
	private final String catCode;

	MissValueProcessType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
