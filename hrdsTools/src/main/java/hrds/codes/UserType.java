package hrds.codes;
/**Created by automatic  */
/**代码类型名：用户类型  */
public enum UserType {
	/**系统管理员<XiTongGuanLiYuan>  */
	XiTongGuanLiYuan("00","系统管理员","1"),
	/**采集管理<CaijiGuanLiYuan>  */
	CaijiGuanLiYuan("01","采集管理","1"),
	/**数据采集<CaiJiYongHu>  */
	CaiJiYongHu("02","数据采集","1"),
	/**数据查询<YeWuYongHu>  */
	YeWuYongHu("03","数据查询","1"),
	/**作业调度<ZuoYeGuanLiYuan>  */
	ZuoYeGuanLiYuan("04","作业调度","1"),
	/**作业操作员<ZuoYeCaoZuoYuan>  */
	ZuoYeCaoZuoYuan("05","作业操作员","1"),
	/**数据可视化管理<ShuJuKSHGuanLiYuan>  */
	ShuJuKSHGuanLiYuan("06","数据可视化管理","1"),
	/**可视化数据源<ShuJuKSHSJY>  */
	ShuJuKSHSJY("07","可视化数据源","1"),
	/**数据可视化分析<ShuJuKSHBianJI>  */
	ShuJuKSHBianJI("08","数据可视化分析","1"),
	/**数据可视化查看<ShuJuKSHChaKan>  */
	ShuJuKSHChaKan("09","数据可视化查看","1"),
	/**监控管理<JianKongGuanLiYuan>  */
	JianKongGuanLiYuan("10","监控管理","1"),
	/**服务接口管理<RESTJieKouGuanLiYuan>  */
	RESTJieKouGuanLiYuan("11","服务接口管理","1"),
	/**服务接口用户<RESTYongHu>  */
	RESTYongHu("12","服务接口用户","1"),
	/**分词器管理<FenCiQiGuanLiYuan>  */
	FenCiQiGuanLiYuan("13","分词器管理","1"),
	/**数据集市<JiShiGuanLiYuan>  */
	JiShiGuanLiYuan("14","数据集市","1"),
	/**数据加工<JiShiJiaGongGuanLiYuan>  */
	JiShiJiaGongGuanLiYuan("15","数据加工","1"),
	/**机器学习工作台<JiQiXueXiGuanLiYuan>  */
	JiQiXueXiGuanLiYuan("16","机器学习工作台","1"),
	/**机器学习业务<JiQiXueXiYongHu>  */
	JiQiXueXiYongHu("17","机器学习业务","1"),
	/**流数据管理<LiuShuJuGuanLiYuan>  */
	LiuShuJuGuanLiYuan("18","流数据管理","1"),
	/**流数据生产<LiuShuJuShengChanYongHu>  */
	LiuShuJuShengChanYongHu("19","流数据生产","1"),
	/**数据库配置(永洪)<ShuJuKuPeiZhi>  */
	ShuJuKuPeiZhi("20","数据库配置(永洪)","1"),
	/**报表创建(永洪)<BaoBiaoChuanJian>  */
	BaoBiaoChuanJian("21","报表创建(永洪)","1"),
	/**报表查看(永洪)<BaoBiaoChaKan>  */
	BaoBiaoChaKan("22","报表查看(永洪)","1"),
	/**流数据消费<LiuShuJuXiaoFeiYongHu>  */
	LiuShuJuXiaoFeiYongHu("23","流数据消费","1"),
	/**数据管控<ShuJuGuanKongGuanLiYuan>  */
	ShuJuGuanKongGuanLiYuan("24","数据管控","1"),
	/**自主分析管理<ZiZhuFenXiGuanLi>  */
	ZiZhuFenXiGuanLi("25","自主分析管理","1"),
	/**自主分析操作<ZiZhuFenXiCaoZuo>  */
	ZiZhuFenXiCaoZuo("26","自主分析操作","1");

	private final String code;
	private final String value;
	private final String catCode;

	UserType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
