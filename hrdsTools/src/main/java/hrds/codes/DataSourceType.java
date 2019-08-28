package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据源类型  */
public enum DataSourceType {
	/**贴源层_01<ISL>  */
	ISL("ISL","贴源层_01","45"),
	/**贴源层<DCL>  */
	DCL("DCL","贴源层","45"),
	/**加工层<DPL>  */
	DPL("DPL","加工层","45"),
	/**集市层<DML>  */
	DML("DML","集市层","45"),
	/**系统层<SFL>  */
	SFL("SFL","系统层","45"),
	/**AI模型层<AML>  */
	AML("AML","AI模型层","45"),
	/**管控层<DQC>  */
	DQC("DQC","管控层","45"),
	/**自定义层<UDL>  */
	UDL("UDL","自定义层","45");

	private final String code;
	private final String value;
	private final String catCode;

	DataSourceType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
