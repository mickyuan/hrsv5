package hrds.codes;
/**Created by automatic  */
/**代码类型名：分类模型类型  */
public enum ClassificatModelType {
	/**支持向量机-SVC<ZhiChiXiangLiangJiSVC>  */
	ZhiChiXiangLiangJiSVC("01","支持向量机-SVC","53"),
	/**神经网络-前馈<ShenJingWangLuoQianKui>  */
	ShenJingWangLuoQianKui("02","神经网络-前馈","53"),
	/**逻辑回归<LuoJiHuiGui>  */
	LuoJiHuiGui("03","逻辑回归","53"),
	/**朴素贝叶斯<PuSuBeiYeSi>  */
	PuSuBeiYeSi("04","朴素贝叶斯","53"),
	/**集成投票分类器<JiChengTouPiaoFenLeiQi>  */
	JiChengTouPiaoFenLeiQi("05","集成投票分类器","53"),
	/**堆叠分类器<DuiDieFenLeiQi>  */
	DuiDieFenLeiQi("06","堆叠分类器","53"),
	/**神经网络<ShenJingWangLuo>  */
	ShenJingWangLuo("07","神经网络","53"),
	/**随机森林分类<SuiJiSenLinFenLei>  */
	SuiJiSenLinFenLei("08","随机森林分类","53"),
	/**分类决策树<FenLeiJueCeShu>  */
	FenLeiJueCeShu("09","分类决策树","53");

	private final String code;
	private final String value;
	private final String catCode;

	ClassificatModelType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
