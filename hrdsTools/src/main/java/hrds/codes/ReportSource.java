package hrds.codes;
/**Created by automatic  */
/**代码类型名：报表来源  */
public enum ReportSource {
	/**报表组件<BaoBiaoZuJian>  */
	BaoBiaoZuJian("01","报表组件","86"),
	/**机器学习<JiQiXueXi>  */
	JiQiXueXi("02","机器学习","86");

	private final String code;
	private final String value;
	private final String catCode;

	ReportSource(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
