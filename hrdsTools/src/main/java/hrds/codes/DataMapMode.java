package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据映射方式  */
public enum DataMapMode {
	/**hive<Hive>  */
	Hive("1","hive","119"),
	/**spark<Spark>  */
	Spark("2","spark","119"),
	/**hbase<Hbase>  */
	Hbase("3","hbase","119"),
	/**elk<ELK>  */
	ELK("4","elk","119");

	private final String code;
	private final String value;
	private final String catCode;

	DataMapMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
