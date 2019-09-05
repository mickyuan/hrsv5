package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：自主取数数据展现形式  */
public enum AutoDataRetrievalForm {
	/**文本框<WenBenKuang>  */
	WenBenKuang("01","文本框","156","自主取数数据展现形式"),
	/**下拉选择<XiaLaXuanZe>  */
	XiaLaXuanZe("02","下拉选择","156","自主取数数据展现形式"),
	/**下拉多选<XiaLaDuoXuan>  */
	XiaLaDuoXuan("03","下拉多选","156","自主取数数据展现形式"),
	/**单选按钮<DanXuanAnNiu>  */
	DanXuanAnNiu("04","单选按钮","156","自主取数数据展现形式"),
	/**复选按钮<FuXuanAnNiu>  */
	FuXuanAnNiu("05","复选按钮","156","自主取数数据展现形式"),
	/**日期选择<RiQiXuanZe>  */
	RiQiXuanZe("06","日期选择","156","自主取数数据展现形式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AutoDataRetrievalForm(String code,String value,String catCode,String catValue){
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
		for (AutoDataRetrievalForm typeCode : AutoDataRetrievalForm.values()) {
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
	public static AutoDataRetrievalForm getCodeObj(String code) {
		for (AutoDataRetrievalForm typeCode : AutoDataRetrievalForm.values()) {
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
		return AutoDataRetrievalForm.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return AutoDataRetrievalForm.values()[0].getCatCode();
	}
}
