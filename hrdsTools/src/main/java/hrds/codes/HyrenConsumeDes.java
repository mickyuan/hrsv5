package hrds.codes;
/**Created by automatic  */
/**代码类型名：海云内部消费目的地  */
public enum HyrenConsumeDes {
	/**hbase<HBASE>  */
	HBASE("1","hbase","128"),
	/**mpp<MPP>  */
	MPP("2","mpp","128"),
	/**hbaseOnSolr<HBASEONSOLR>  */
	HBASEONSOLR("3","hbaseOnSolr","128"),
	/**hdfs<HDFS>  */
	HDFS("4","hdfs","128"),
	/**时序数据库<Druid>  */
	Druid("5","时序数据库","128"),
	/**sparkd<SPARKD>  */
	SPARKD("6","sparkd","128");

	private final String code;
	private final String value;
	private final String catCode;

	HyrenConsumeDes(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
