package hrds.codes;
/**Created by automatic  */
/**代码类型名：hdfs文件类型  */
public enum HdfsFileType {
	/**csv<Csv>  */
	Csv("1","csv","130"),
	/**parquet<Parquet>  */
	Parquet("2","parquet","130"),
	/**avro<Avro>  */
	Avro("3","avro","130"),
	/**orcfile<OrcFile>  */
	OrcFile("4","orcfile","130"),
	/**sequencefile<SequenceFile>  */
	SequenceFile("5","sequencefile","130"),
	/**其他<Other>  */
	Other("6","其他","130");

	private final String code;
	private final String value;
	private final String catCode;

	HdfsFileType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
