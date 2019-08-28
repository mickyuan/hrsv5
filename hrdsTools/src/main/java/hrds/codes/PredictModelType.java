package hrds.codes;
/**Created by automatic  */
/**代码类型名：预测模型类型  */
public enum PredictModelType {
	/**线性模型<XianXingMoXing>  */
	XianXingMoXing("01","线性模型","79"),
	/**支持向量机-svr<ZhiChiXiangLiangJiSVR>  */
	ZhiChiXiangLiangJiSVR("02","支持向量机-svr","79"),
	/**时间序列<ShiJianXuLie>  */
	ShiJianXuLie("03","时间序列","79"),
	/**随机森林回归<SuiJiSenLinHuiGui>  */
	SuiJiSenLinHuiGui("04","随机森林回归","79"),
	/**堆叠回归<DuiDieHuiGui>  */
	DuiDieHuiGui("05","堆叠回归","79"),
	/**梯度增强回归树<TiDuZengQiangHuiGuiShu>  */
	TiDuZengQiangHuiGuiShu("06","梯度增强回归树","79");

	private final String code;
	private final String value;
	private final String catCode;

	PredictModelType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
