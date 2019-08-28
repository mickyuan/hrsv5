package hrds.codes;
/**Created by automatic  */
/**代码类型名：hive文件存储类型  */
public enum HiveStorageType {
	/**TEXTFILE<TEXTFILE>  */
	TEXTFILE("1","TEXTFILE","131"),
	/**SEQUENCEFILE<SEQUENCEFILE>  */
	SEQUENCEFILE("2","SEQUENCEFILE","131"),
	/**PARQUET<PARQUET>  */
	PARQUET("3","PARQUET","131"),
	/**CSV<CSV>  */
	CSV("4","CSV","131"),
	/**ORC<ORC>  */
	ORC("5","ORC","131");

	private final String code;
	private final String value;
	private final String catCode;

	HiveStorageType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
