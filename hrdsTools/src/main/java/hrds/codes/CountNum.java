package hrds.codes;
/**Created by automatic  */
/**代码类型名：记录总数  */
public enum CountNum {
	/**1万左右<YiWan>  */
	YiWan("10000","1万左右","37"),
	/**10万左右<ShiWan>  */
	ShiWan("100000","10万左右","37"),
	/**100万左右<BaiWan>  */
	BaiWan("1000000","100万左右","37"),
	/**1000万左右<Qianwan>  */
	Qianwan("10000000","1000万左右","37"),
	/**亿左右<Yi>  */
	Yi("100000000","亿左右","37"),
	/**亿以上<YiYiShang>  */
	YiYiShang("100000001","亿以上","37");

	private final String code;
	private final String value;
	private final String catCode;

	CountNum(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
