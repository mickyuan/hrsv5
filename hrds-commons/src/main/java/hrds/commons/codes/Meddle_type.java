package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：ETL干预类型  */
public enum Meddle_type {
	/**分组级续跑<GRP_RESUME>  */
	GRP_RESUME("GR","分组级续跑","29","ETL干预类型"),
	/**分组级暂停<GRP_PAUSE>  */
	GRP_PAUSE("GP","分组级暂停","29","ETL干预类型"),
	/**分组级重跑，从源头开始<GRP_ORIGINAL>  */
	GRP_ORIGINAL("GO","分组级重跑，从源头开始","29","ETL干预类型"),
	/**作业直接跑<JOB_TRIGGER>  */
	JOB_TRIGGER("JT","作业直接跑","29","ETL干预类型"),
	/**作业停止<JOB_STOP>  */
	JOB_STOP("JS","作业停止","29","ETL干预类型"),
	/**作业重跑<JOB_RERUN>  */
	JOB_RERUN("JR","作业重跑","29","ETL干预类型"),
	/**作业临时调整优先级<JOB_PRIORITY>  */
	JOB_PRIORITY("JP","作业临时调整优先级","29","ETL干预类型"),
	/**作业跳过<JOB_JUMP>  */
	JOB_JUMP("JJ","作业跳过","29","ETL干预类型"),
	/**系统日切<SYS_SHIFT>  */
	SYS_SHIFT("SF","系统日切","29","ETL干预类型"),
	/**系统停止<SYS_STOP>  */
	SYS_STOP("SS","系统停止","29","ETL干预类型"),
	/**系统级暂停<SYS_PAUSE>  */
	SYS_PAUSE("SP","系统级暂停","29","ETL干预类型"),
	/**系统级重跑，从源头开始<SYS_ORIGINAL>  */
	SYS_ORIGINAL("SO","系统级重跑，从源头开始","29","ETL干预类型"),
	/**系统级续跑<SYS_RESUME>  */
	SYS_RESUME("SR","系统级续跑","29","ETL干预类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Meddle_type(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Meddle_type";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Meddle_type typeCode : Meddle_type.values()) {
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
	public static Meddle_type ofEnumByCode(String code) {
		for (Meddle_type typeCode : Meddle_type.values()) {
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
		return Meddle_type.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Meddle_type.values()[0].getCatCode();
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
