package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：数据质量处理状态  */
public enum DqcDlStat {
	/**等待处理<DengDaiChuLi>  */
	DengDaiChuLi("w","等待处理","139","数据质量处理状态"),
	/**已退回<YiTuiHui>  */
	YiTuiHui("b","已退回","139","数据质量处理状态"),
	/**已忽略<YiHuLue>  */
	YiHuLue("i","已忽略","139","数据质量处理状态"),
	/**已处理<YiChuLi>  */
	YiChuLi("d","已处理","139","数据质量处理状态"),
	/**处理完结<ChuLiWanJie>  */
	ChuLiWanJie("oki","处理完结","139","数据质量处理状态"),
	/**已忽略通过<YiHuLueTongGuo>  */
	YiHuLueTongGuo("okd","已忽略通过","139","数据质量处理状态"),
	/**正常<ZhengChang>  */
	ZhengChang("zc","正常","139","数据质量处理状态");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DqcDlStat(String code,String value,String catCode,String catValue){
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
		for (DqcDlStat typeCode : DqcDlStat.values()) {
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
	public static DqcDlStat getCodeObj(String code) {
		for (DqcDlStat typeCode : DqcDlStat.values()) {
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
		return DqcDlStat.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DqcDlStat.values()[0].getCatCode();
	}
}
