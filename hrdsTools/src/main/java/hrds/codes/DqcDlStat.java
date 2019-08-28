package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据质量处理状态  */
public enum DqcDlStat {
	/**等待处理<DengDaiChuLi>  */
	DengDaiChuLi("w","等待处理","139"),
	/**已退回<YiTuiHui>  */
	YiTuiHui("b","已退回","139"),
	/**已忽略<YiHuLue>  */
	YiHuLue("i","已忽略","139"),
	/**已处理<YiChuLi>  */
	YiChuLi("d","已处理","139"),
	/**处理完结<ChuLiWanJie>  */
	ChuLiWanJie("oki","处理完结","139"),
	/**已忽略通过<YiHuLueTongGuo>  */
	YiHuLueTongGuo("okd","已忽略通过","139"),
	/**正常<ZhengChang>  */
	ZhengChang("zc","正常","139");

	private final String code;
	private final String value;
	private final String catCode;

	DqcDlStat(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
