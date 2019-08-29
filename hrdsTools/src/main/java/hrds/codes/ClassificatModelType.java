package hrds.codes;
/**Created by automatic  */
/**代码类型名：分类模型类型  */
public enum ClassificatModelType {
	/**支持向量机-SVC<ZhiChiXiangLiangJiSVC>  */
	ZhiChiXiangLiangJiSVC("01","支持向量机-SVC","53","分类模型类型"),
	/**神经网络-前馈<ShenJingWangLuoQianKui>  */
	ShenJingWangLuoQianKui("02","神经网络-前馈","53","分类模型类型"),
	/**逻辑回归<LuoJiHuiGui>  */
	LuoJiHuiGui("03","逻辑回归","53","分类模型类型"),
	/**朴素贝叶斯<PuSuBeiYeSi>  */
	PuSuBeiYeSi("04","朴素贝叶斯","53","分类模型类型"),
	/**集成投票分类器<JiChengTouPiaoFenLeiQi>  */
	JiChengTouPiaoFenLeiQi("05","集成投票分类器","53","分类模型类型"),
	/**堆叠分类器<DuiDieFenLeiQi>  */
	DuiDieFenLeiQi("06","堆叠分类器","53","分类模型类型"),
	/**神经网络<ShenJingWangLuo>  */
	ShenJingWangLuo("07","神经网络","53","分类模型类型"),
	/**随机森林分类<SuiJiSenLinFenLei>  */
	SuiJiSenLinFenLei("08","随机森林分类","53","分类模型类型"),
	/**分类决策树<FenLeiJueCeShu>  */
	FenLeiJueCeShu("09","分类决策树","53","分类模型类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ClassificatModelType(String code,String value,String catCode,String catValue){
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
		for (ClassificatModelType typeCode : ClassificatModelType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static ClassificatModelType getCodeObj(String code) {
		for (ClassificatModelType typeCode : ClassificatModelType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return ClassificatModelType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ClassificatModelType.values()[0].getCatCode();
	}
}
