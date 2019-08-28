package hrds.codes;
/**Created by automatic  */
/**代码类型名：作业程序类型  */
public enum ProType {
	/**c<C>  */
	C("01","c","12"),
	/**c++<CAdd>  */
	CAdd("02","c++","12"),
	/**JAVA<Java>  */
	Java("03","JAVA","12"),
	/**C#<CStudio>  */
	CStudio("04","C#","12"),
	/**shell<Shell>  */
	Shell("05","shell","12"),
	/**perl<Perl>  */
	Perl("06","perl","12");

	private final String code;
	private final String value;
	private final String catCode;

	ProType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
