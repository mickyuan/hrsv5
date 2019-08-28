package hrds.codes;
/**Created by automatic  */
/**代码类型名：数值变量重编码处理方式  */
public enum NumericalVariableRecodeMode {
	/**标准化<BiaoZhunHua>  */
	BiaoZhunHua("1","标准化","70"),
	/**归一化<GuiYiHua>  */
	GuiYiHua("2","归一化","70"),
	/**离散化<LiSanHua>  */
	LiSanHua("3","离散化","70");

	private final String code;
	private final String value;
	private final String catCode;

	NumericalVariableRecodeMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
