package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL调度类型  */
public enum Dispatch_Type {
	/**批前(B)<BEFORE>  */
	BEFORE("B","批前(B)","108"),
	/**依赖触发(D)<DEPENDENCE>  */
	DEPENDENCE("D","依赖触发(D)","108"),
	/**定时T+1触发(T)<TIMING>  */
	TIMING("T","定时T+1触发(T)","108"),
	/**定时T+0触发(Z)<ZTIMING>  */
	ZTIMING("Z","定时T+0触发(Z)","108"),
	/**批后(A)<AFTER>  */
	AFTER("A","批后(A)","108");

	private final String code;
	private final String value;
	private final String catCode;

	Dispatch_Type(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
