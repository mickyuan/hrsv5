package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：机器学习项目当前操作状态  */
public enum MLOperationStatus {
	/**未操作<WeiCaoZuo>  */
	WeiCaoZuo("0","未操作","57","机器学习项目当前操作状态"),
	/**数据源聚焦<ShuJuYuanJuJi>  */
	ShuJuYuanJuJi("1","数据源聚焦","57","机器学习项目当前操作状态"),
	/**数据探索<ShuJuTanSuo>  */
	ShuJuTanSuo("2","数据探索","57","机器学习项目当前操作状态"),
	/**数据预处理<ShuJuYuChuLi>  */
	ShuJuYuChuLi("3","数据预处理","57","机器学习项目当前操作状态"),
	/**假设检验<JiaSheJianYan>  */
	JiaSheJianYan("4","假设检验","57","机器学习项目当前操作状态"),
	/**特征加工<TeZhengJiaGong>  */
	TeZhengJiaGong("5","特征加工","57","机器学习项目当前操作状态"),
	/**数据拆分<ShuJuChaiFen>  */
	ShuJuChaiFen("6","数据拆分","57","机器学习项目当前操作状态"),
	/**模型选择<MoXingXuanZhe>  */
	MoXingXuanZhe("7","模型选择","57","机器学习项目当前操作状态"),
	/**模型评估<MoXingPingGu>  */
	MoXingPingGu("8","模型评估","57","机器学习项目当前操作状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	MLOperationStatus(String code,String value,String catCode,String catValue){
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
		for (MLOperationStatus typeCode : MLOperationStatus.values()) {
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
	public static MLOperationStatus getCodeObj(String code) {
		for (MLOperationStatus typeCode : MLOperationStatus.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return MLOperationStatus.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return MLOperationStatus.values()[0].getCatCode();
	}
}
