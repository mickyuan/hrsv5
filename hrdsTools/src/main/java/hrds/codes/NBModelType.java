package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习朴素贝叶斯类型  */
public enum NBModelType {
	/**多项式<duoxiangshi>  */
	duoxiangshi("1","多项式","103"),
	/**伯努利<nonuli>  */
	nonuli("2","伯努利","103");

	private final String code;
	private final String value;
	private final String catCode;

	NBModelType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
