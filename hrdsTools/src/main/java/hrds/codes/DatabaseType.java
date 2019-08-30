package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据库类型  */
public enum DatabaseType {
	/**MYSQL<MYSQL>  */
	MYSQL("01","MYSQL","6","数据库类型"),
	/**Oracle9i及一下<Oracle9i>  */
	Oracle9i("02","Oracle9i及一下","6","数据库类型"),
	/**Oracle10g及以上<Oracle10g>  */
	Oracle10g("03","Oracle10g及以上","6","数据库类型"),
	/**SQLSERVER2000<SqlServer2000>  */
	SqlServer2000("04","SQLSERVER2000","6","数据库类型"),
	/**SQLSERVER2005<SqlServer2005>  */
	SqlServer2005("05","SQLSERVER2005","6","数据库类型"),
	/**DB2<DB2>  */
	DB2("06","DB2","6","数据库类型"),
	/**SybaseASE12.5及以上<SybaseASE125>  */
	SybaseASE125("07","SybaseASE12.5及以上","6","数据库类型"),
	/**Informatic<Informatic>  */
	Informatic("08","Informatic","6","数据库类型"),
	/**H2<H2>  */
	H2("09","H2","6","数据库类型"),
	/**ApacheDerby<ApacheDerby>  */
	ApacheDerby("10","ApacheDerby","6","数据库类型"),
	/**Postgresql<Postgresql>  */
	Postgresql("11","Postgresql","6","数据库类型"),
	/**GBase<GBase>  */
	GBase("12","GBase","6","数据库类型"),
	/**TeraData<TeraData>  */
	TeraData("13","TeraData","6","数据库类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DatabaseType(String code,String value,String catCode,String catValue){
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
		for (DatabaseType typeCode : DatabaseType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static DatabaseType getCodeObj(String code) {
		for (DatabaseType typeCode : DatabaseType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据code没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return DatabaseType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DatabaseType.values()[0].getCatCode();
	}
}
