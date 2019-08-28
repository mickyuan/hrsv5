package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据库类型  */
public enum DatabaseType {
	/**MYSQL<MYSQL>  */
	MYSQL("01","MYSQL","6"),
	/**Oracle9i及一下<Oracle9i>  */
	Oracle9i("02","Oracle9i及一下","6"),
	/**Oracle10g及以上<Oracle10g>  */
	Oracle10g("03","Oracle10g及以上","6"),
	/**SQLSERVER2000<SqlServer2000>  */
	SqlServer2000("04","SQLSERVER2000","6"),
	/**SQLSERVER2005<SqlServer2005>  */
	SqlServer2005("05","SQLSERVER2005","6"),
	/**DB2<DB2>  */
	DB2("06","DB2","6"),
	/**SybaseASE12.5及以上<SybaseASE125>  */
	SybaseASE125("07","SybaseASE12.5及以上","6"),
	/**Informatic<Informatic>  */
	Informatic("08","Informatic","6"),
	/**H2<H2>  */
	H2("09","H2","6"),
	/**ApacheDerby<ApacheDerby>  */
	ApacheDerby("10","ApacheDerby","6"),
	/**Postgresql<Postgresql>  */
	Postgresql("11","Postgresql","6"),
	/**GBase<GBase>  */
	GBase("12","GBase","6"),
	/**TeraData<TeraData>  */
	TeraData("13","TeraData","6");

	private final String code;
	private final String value;
	private final String catCode;

	DatabaseType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
