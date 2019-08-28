package hrds.codes;
/**Created by automatic  */
/**代码类型名：文件夹监听类别  */
public enum DirWatch {
	/**增加<ZengJia>  */
	ZengJia("0","增加","40"),
	/**删除<ShanChu>  */
	ShanChu("1","删除","40"),
	/**更新<GengXin>  */
	GengXin("2","更新","40");

	private final String code;
	private final String value;
	private final String catCode;

	DirWatch(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
