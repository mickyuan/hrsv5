package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL作业类型  */
public enum Pro_Type {
	/**SHELL<SHELL>  */
	SHELL("SHELL","SHELL","117","ETL作业类型"),
	/**PERL<PERL>  */
	PERL("PERL","PERL","117","ETL作业类型"),
	/**BAT<BAT>  */
	BAT("BAT","BAT","117","ETL作业类型"),
	/**JAVA<JAVA>  */
	JAVA("JAVA","JAVA","117","ETL作业类型"),
	/**PYTHON<PYTHON>  */
	PYTHON("PYTHON","PYTHON","117","ETL作业类型"),
	/**WF<WF>  */
	WF("WF","WF","117","ETL作业类型"),
	/**DBTRAN<DBTRAN>  */
	DBTRAN("DBTRAN","DBTRAN","117","ETL作业类型"),
	/**DBJOB<DBJOB>  */
	DBJOB("DBJOB","DBJOB","117","ETL作业类型"),
	/**Yarn<Yarn>  */
	Yarn("Yarn","Yarn","117","ETL作业类型"),
	/**Thrift<Thrift>  */
	Thrift("Thrift","Thrift","117","ETL作业类型");

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

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (Pro_Type typeCode : Pro_Type.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static Pro_Type getCodeObj(String code) {
		for (Pro_Type typeCode : Pro_Type.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return Pro_Type.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return Pro_Type.values()[0].getCatCode();
	}
}
