package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL作业类型  */
public enum Pro_Type {
	/**SHELL<SHELL>  */
	SHELL("SHELL","SHELL","117"),
	/**PERL<PERL>  */
	PERL("PERL","PERL","117"),
	/**BAT<BAT>  */
	BAT("BAT","BAT","117"),
	/**JAVA<JAVA>  */
	JAVA("JAVA","JAVA","117"),
	/**PYTHON<PYTHON>  */
	PYTHON("PYTHON","PYTHON","117"),
	/**WF<WF>  */
	WF("WF","WF","117"),
	/**DBTRAN<DBTRAN>  */
	DBTRAN("DBTRAN","DBTRAN","117"),
	/**DBJOB<DBJOB>  */
	DBJOB("DBJOB","DBJOB","117"),
	/**Yarn<Yarn>  */
	Yarn("Yarn","Yarn","117"),
	/**Thrift<Thrift>  */
	Thrift("Thrift","Thrift","117");

	private final String code;
	private final String value;
	private final String catCode;

	Pro_Type(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
