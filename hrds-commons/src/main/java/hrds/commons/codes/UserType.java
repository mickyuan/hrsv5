package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：用户类型  */
public enum UserType {
	/**系统管理员<XiTongGuanLiYuan>  */
	XiTongGuanLiYuan("00","系统管理员","17","用户类型"),
	/**采集管理<CaijiGuanLiYuan>  */
	CaijiGuanLiYuan("01","采集管理","17","用户类型"),
	/**数据采集<CaiJiYongHu>  */
	CaiJiYongHu("02","数据采集","17","用户类型"),
	/**数据查询<YeWuYongHu>  */
	YeWuYongHu("03","数据查询","17","用户类型"),
	/**作业调度<ZuoYeGuanLiYuan>  */
	ZuoYeGuanLiYuan("04","作业调度","17","用户类型"),
	/**作业操作员<ZuoYeCaoZuoYuan>  */
	ZuoYeCaoZuoYuan("05","作业操作员","17","用户类型"),
	/**数据可视化管理<ShuJuKSHGuanLiYuan>  */
	ShuJuKSHGuanLiYuan("06","数据可视化管理","17","用户类型"),
	/**可视化数据源<ShuJuKSHSJY>  */
	ShuJuKSHSJY("07","可视化数据源","17","用户类型"),
	/**数据可视化分析<ShuJuKSHBianJI>  */
	ShuJuKSHBianJI("08","数据可视化分析","17","用户类型"),
	/**数据可视化查看<ShuJuKSHChaKan>  */
	ShuJuKSHChaKan("09","数据可视化查看","17","用户类型"),
	/**监控管理<JianKongGuanLiYuan>  */
	JianKongGuanLiYuan("10","监控管理","17","用户类型"),
	/**服务接口管理<RESTJieKouGuanLiYuan>  */
	RESTJieKouGuanLiYuan("11","服务接口管理","17","用户类型"),
	/**服务接口用户<RESTYongHu>  */
	RESTYongHu("12","服务接口用户","17","用户类型"),
	/**分词器管理<FenCiQiGuanLiYuan>  */
	FenCiQiGuanLiYuan("13","分词器管理","17","用户类型"),
	/**数据集市<JiShiGuanLiYuan>  */
	JiShiGuanLiYuan("14","数据集市","17","用户类型"),
	/**数据加工<JiShiJiaGongGuanLiYuan>  */
	JiShiJiaGongGuanLiYuan("15","数据加工","17","用户类型"),
	/**机器学习工作台<JiQiXueXiGuanLiYuan>  */
	JiQiXueXiGuanLiYuan("16","机器学习工作台","17","用户类型"),
	/**机器学习业务<JiQiXueXiYongHu>  */
	JiQiXueXiYongHu("17","机器学习业务","17","用户类型"),
	/**流数据管理<LiuShuJuGuanLiYuan>  */
	LiuShuJuGuanLiYuan("18","流数据管理","17","用户类型"),
	/**流数据生产<LiuShuJuShengChanYongHu>  */
	LiuShuJuShengChanYongHu("19","流数据生产","17","用户类型"),
	/**数据库配置(永洪)<ShuJuKuPeiZhi>  */
	ShuJuKuPeiZhi("20","数据库配置(永洪)","17","用户类型"),
	/**报表创建(永洪)<BaoBiaoChuanJian>  */
	BaoBiaoChuanJian("21","报表创建(永洪)","17","用户类型"),
	/**报表查看(永洪)<BaoBiaoChaKan>  */
	BaoBiaoChaKan("22","报表查看(永洪)","17","用户类型"),
	/**流数据消费<LiuShuJuXiaoFeiYongHu>  */
	LiuShuJuXiaoFeiYongHu("23","流数据消费","17","用户类型"),
	/**数据管控<ShuJuGuanKongGuanLiYuan>  */
	ShuJuGuanKongGuanLiYuan("24","数据管控","17","用户类型"),
	/**自主分析管理<ZiZhuFenXiGuanLi>  */
	ZiZhuFenXiGuanLi("25","自主分析管理","17","用户类型"),
	/**资源管理<ZiYuanGuanLi>  */
	ZiYuanGuanLi("27","资源管理","17","用户类型"),
	/**自主分析操作<ZiZhuFenXiCaoZuo>  */
	ZiZhuFenXiCaoZuo("26","自主分析操作","17","用户类型"),
	/**数据对标操作<ShuJuDuiBiaoCaoZuo>  */
	ShuJuDuiBiaoCaoZuo("37","数据对标操作","17","用户类型"),
	/**数据对标管理<ShuJuDuiBiaoGuanLi>  */
	ShuJuDuiBiaoGuanLi("55","数据对标管理","17","用户类型"),
	/**用户管理<YongHuGuanLi>  */
	YongHuGuanLi("99","用户管理","17","用户类型"),
	/**部门管理<BuMenGuanLi>  */
	BuMenGuanLi("98","部门管理","17","用户类型"),
	/**系统参数管理<XiTongCanShuGuanLi>  */
	XiTongCanShuGuanLi("97","系统参数管理","17","用户类型"),
	/**数据整理<ShuJuZhengLi>  */
	ShuJuZhengLi("96","数据整理","17","用户类型"),
	/**数据存储层定义<ShuJunCunChuCengDingYi>  */
	ShuJunCunChuCengDingYi("95","数据存储层定义","17","用户类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	UserType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "UserType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (UserType typeCode : UserType.values()) {
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
	public static UserType ofEnumByCode(String code) {
		for (UserType typeCode : UserType.values()) {
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
	public static String ofCatValue(){
		return UserType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return UserType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	@Override
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
