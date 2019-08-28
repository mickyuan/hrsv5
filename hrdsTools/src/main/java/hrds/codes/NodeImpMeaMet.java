package hrds.codes;
/**Created by automatic  */
/**代码类型名：节点不纯度衡量方法  */
public enum NodeImpMeaMet {
	/**GINI<GINI>  */
	GINI("1","GINI","102"),
	/**熵<Shang>  */
	Shang("2","熵","102");

	private final String code;
	private final String value;
	private final String catCode;

	NodeImpMeaMet(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
