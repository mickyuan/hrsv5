package hrds.codes;
/**Created by automatic  */
/**代码类型名：自主取数数据展现形式  */
public enum AutoDataRetrievalForm {
	/**文本框<WenBenKuang>  */
	WenBenKuang("01","文本框","156"),
	/**下拉选择<XiaLaXuanZe>  */
	XiaLaXuanZe("02","下拉选择","156"),
	/**下拉多选<XiaLaDuoXuan>  */
	XiaLaDuoXuan("03","下拉多选","156"),
	/**单选按钮<DanXuanAnNiu>  */
	DanXuanAnNiu("04","单选按钮","156"),
	/**复选按钮<FuXuanAnNiu>  */
	FuXuanAnNiu("05","复选按钮","156"),
	/**日期选择<RiQiXuanZe>  */
	RiQiXuanZe("06","日期选择","156");

	private final String code;
	private final String value;
	private final String catCode;

	AutoDataRetrievalForm(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
