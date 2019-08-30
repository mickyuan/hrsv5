package hrds.codes;
/**Created by automatic  */
/**代码类型名：数值变量重编码处理方式  */
public enum NumericalVariableRecodeMode {
	/**标准化<BiaoZhunHua>  */
	BiaoZhunHua("1","标准化","70","数值变量重编码处理方式"),
	/**归一化<GuiYiHua>  */
	GuiYiHua("2","归一化","70","数值变量重编码处理方式"),
	/**离散化<LiSanHua>  */
	LiSanHua("3","离散化","70","数值变量重编码处理方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	NumericalVariableRecodeMode(String code,String value,String catCode,String catValue){
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
		for (NumericalVariableRecodeMode typeCode : NumericalVariableRecodeMode.values()) {
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
	public static NumericalVariableRecodeMode getCodeObj(String code) {
		for (NumericalVariableRecodeMode typeCode : NumericalVariableRecodeMode.values()) {
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
		return NumericalVariableRecodeMode.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return NumericalVariableRecodeMode.values()[0].getCatCode();
	}
}
