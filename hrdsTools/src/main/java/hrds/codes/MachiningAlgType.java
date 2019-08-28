package hrds.codes;
/**Created by automatic  */
/**代码类型名：加工层算法类型  */
public enum MachiningAlgType {
	/**F1<F1_DEISERT>  */
	F1_DEISERT("F1","F1","95"),
	/**F2<F2_UPSERT>  */
	F2_UPSERT("F2","F2","95"),
	/**F3<F3_HISTORYCHAIN>  */
	F3_HISTORYCHAIN("F3","F3","95"),
	/**F5<F5_FULLDATAHISTORYCHAIN>  */
	F5_FULLDATAHISTORYCHAIN("F5","F5","95"),
	/**I<I_APPEND>  */
	I_APPEND("I","I","95");

	private final String code;
	private final String value;
	private final String catCode;

	MachiningAlgType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
