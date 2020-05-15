package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：ETL作业类型  */
public enum Pro_Type {
	/**SHELL<SHELL>  */
	SHELL("SHELL","SHELL","20","ETL作业类型"),
	/**PERL<PERL>  */
	PERL("PERL","PERL","20","ETL作业类型"),
	/**BAT<BAT>  */
	BAT("BAT","BAT","20","ETL作业类型"),
	/**JAVA<JAVA>  */
	JAVA("JAVA","JAVA","20","ETL作业类型"),
	/**PYTHON<PYTHON>  */
	PYTHON("PYTHON","PYTHON","20","ETL作业类型"),
	/**WF<WF>  */
	WF("WF","WF","20","ETL作业类型"),
	/**DBTRAN<DBTRAN>  */
	DBTRAN("DBTRAN","DBTRAN","20","ETL作业类型"),
	/**DBJOB<DBJOB>  */
	DBJOB("DBJOB","DBJOB","20","ETL作业类型"),
	/**Yarn<Yarn>  */
	Yarn("Yarn","Yarn","20","ETL作业类型"),
	/**Thrift<Thrift>  */
	Thrift("Thrift","Thrift","20","ETL作业类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Pro_Type(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Pro_Type";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Pro_Type typeCode : Pro_Type.values()) {
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
	public static Pro_Type ofEnumByCode(String code) {
		for (Pro_Type typeCode : Pro_Type.values()) {
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
	public static String ofCatValue(){
		return Pro_Type.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Pro_Type.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	@Override
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
