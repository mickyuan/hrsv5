package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习数据转换方式  */
public enum DataTransferMethod {
	/**对数运算<log>  */
	log("011","对数运算","90"),
	/**指数运算<exp>  */
	exp("012","指数运算","90"),
	/**自然对数运算<ln>  */
	ln("013","自然对数运算","90"),
	/**幂运算<power>  */
	power("014","幂运算","90"),
	/**平方根运算<sqrt>  */
	sqrt("015","平方根运算","90"),
	/**转为数值型<number>  */
	number("021","转为数值型","90"),
	/**转为字符型<string>  */
	string("022","转为字符型","90"),
	/**日期比较<datadiff>  */
	datadiff("031","日期比较","90"),
	/**获取年<year>  */
	year("041","获取年","90"),
	/**获取月<month>  */
	month("042","获取月","90"),
	/**获取周<week>  */
	week("043","获取周","90"),
	/**去左空格<ltrim>  */
	ltrim("051","去左空格","90"),
	/**去右空格<rtrim>  */
	rtrim("052","去右空格","90"),
	/**获取长度<length>  */
	length("053","获取长度","90"),
	/**转成小写<lower>  */
	lower("054","转成小写","90"),
	/**转成大写<upercase>  */
	upercase("055","转成大写","90");

	private final String code;
	private final String value;
	private final String catCode;

	DataTransferMethod(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
