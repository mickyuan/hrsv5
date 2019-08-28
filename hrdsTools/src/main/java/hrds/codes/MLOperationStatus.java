package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习项目当前操作状态  */
public enum MLOperationStatus {
	/**未操作<WeiCaoZuo>  */
	WeiCaoZuo("0","未操作","57"),
	/**数据源聚焦<ShuJuYuanJuJi>  */
	ShuJuYuanJuJi("1","数据源聚焦","57"),
	/**数据探索<ShuJuTanSuo>  */
	ShuJuTanSuo("2","数据探索","57"),
	/**数据预处理<ShuJuYuChuLi>  */
	ShuJuYuChuLi("3","数据预处理","57"),
	/**假设检验<JiaSheJianYan>  */
	JiaSheJianYan("4","假设检验","57"),
	/**特征加工<TeZhengJiaGong>  */
	TeZhengJiaGong("5","特征加工","57"),
	/**数据拆分<ShuJuChaiFen>  */
	ShuJuChaiFen("6","数据拆分","57"),
	/**模型选择<MoXingXuanZhe>  */
	MoXingXuanZhe("7","模型选择","57"),
	/**模型评估<MoXingPingGu>  */
	MoXingPingGu("8","模型评估","57");

	private final String code;
	private final String value;
	private final String catCode;

	MLOperationStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
