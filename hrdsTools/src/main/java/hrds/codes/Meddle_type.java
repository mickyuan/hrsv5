package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL干预类型  */
public enum Meddle_type {
	/**分组级续跑<GRP_RESUME>  */
	GRP_RESUME("GR","分组级续跑","114"),
	/**分组级暂停<GRP_PAUSE>  */
	GRP_PAUSE("GP","分组级暂停","114"),
	/**分组级重跑，从源头开始<GRP_ORIGINAL>  */
	GRP_ORIGINAL("GO","分组级重跑，从源头开始","114"),
	/**作业直接跑<JOB_TRIGGER>  */
	JOB_TRIGGER("JT","作业直接跑","114"),
	/**作业停止<JOB_STOP>  */
	JOB_STOP("JS","作业停止","114"),
	/**作业重跑<JOB_RERUN>  */
	JOB_RERUN("JR","作业重跑","114"),
	/**作业临时调整优先级<JOB_PRIORITY>  */
	JOB_PRIORITY("JP","作业临时调整优先级","114"),
	/**作业跳过<JOB_JUMP>  */
	JOB_JUMP("JJ","作业跳过","114"),
	/**系统日切<SYS_SHIFT>  */
	SYS_SHIFT("SF","系统日切","114"),
	/**系统停止<SYS_STOP>  */
	SYS_STOP("SS","系统停止","114"),
	/**系统级暂停<SYS_PAUSE>  */
	SYS_PAUSE("SP","系统级暂停","114"),
	/**系统级重跑，从源头开始<SYS_ORIGINAL>  */
	SYS_ORIGINAL("SO","系统级重跑，从源头开始","114"),
	/**系统级续跑<SYS_RESUME>  */
	SYS_RESUME("SR","系统级续跑","114");

	private final String code;
	private final String value;
	private final String catCode;

	Meddle_type(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
