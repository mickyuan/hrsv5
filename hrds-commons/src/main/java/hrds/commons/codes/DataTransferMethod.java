package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：机器学习数据转换方式  */
public enum DataTransferMethod {
	/**对数运算<log>  */
	log("011","对数运算","90","机器学习数据转换方式"),
	/**指数运算<exp>  */
	exp("012","指数运算","90","机器学习数据转换方式"),
	/**自然对数运算<ln>  */
	ln("013","自然对数运算","90","机器学习数据转换方式"),
	/**幂运算<power>  */
	power("014","幂运算","90","机器学习数据转换方式"),
	/**平方根运算<sqrt>  */
	sqrt("015","平方根运算","90","机器学习数据转换方式"),
	/**转为数值型<number>  */
	number("021","转为数值型","90","机器学习数据转换方式"),
	/**转为字符型<string>  */
	string("022","转为字符型","90","机器学习数据转换方式"),
	/**日期比较<datadiff>  */
	datadiff("031","日期比较","90","机器学习数据转换方式"),
	/**获取年<year>  */
	year("041","获取年","90","机器学习数据转换方式"),
	/**获取月<month>  */
	month("042","获取月","90","机器学习数据转换方式"),
	/**获取周<week>  */
	week("043","获取周","90","机器学习数据转换方式"),
	/**去左空格<ltrim>  */
	ltrim("051","去左空格","90","机器学习数据转换方式"),
	/**去右空格<rtrim>  */
	rtrim("052","去右空格","90","机器学习数据转换方式"),
	/**获取长度<length>  */
	length("053","获取长度","90","机器学习数据转换方式"),
	/**转成小写<lower>  */
	lower("054","转成小写","90","机器学习数据转换方式"),
	/**转成大写<upercase>  */
	upercase("055","转成大写","90","机器学习数据转换方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DataTransferMethod(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (DataTransferMethod typeCode : DataTransferMethod.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static DataTransferMethod getCodeObj(String code) {
		for (DataTransferMethod typeCode : DataTransferMethod.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return DataTransferMethod.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DataTransferMethod.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
