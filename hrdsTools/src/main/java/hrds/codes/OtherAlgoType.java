package hrds.codes;
/**Created by automatic  */
/**代码类型名：其它算法类型  */
public enum OtherAlgoType {
	/**PageRank<PageRank>  */
	PageRank("1","PageRank","118");

	private final String code;
	private final String value;
	private final String catCode;

	OtherAlgoType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
