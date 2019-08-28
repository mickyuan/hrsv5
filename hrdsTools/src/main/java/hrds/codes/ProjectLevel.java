package hrds.codes;
/**Created by automatic  */
/**代码类型名：作业操作类型  */
public enum ProjectLevel {
	/**等待<DengDai>  */
	DengDai("6000","等待","43"),
	/**工程运行<GongChengYunXing>  */
	GongChengYunXing("6001","工程运行","43"),
	/**工程中止<GongChengZhongZhi>  */
	GongChengZhongZhi("6002","工程中止","43"),
	/**工程暂停<GongChengZanTing>  */
	GongChengZanTing("6003","工程暂停","43"),
	/**工程重跑<GongChengChongPao>  */
	GongChengChongPao("6004","工程重跑","43"),
	/**工程完成<GongChengWanCheng>  */
	GongChengWanCheng("6005","工程完成","43"),
	/**任务运行<RenWuYunXing>  */
	RenWuYunXing("7001","任务运行","43"),
	/**任务中止<RenWuZhongZhi>  */
	RenWuZhongZhi("7002","任务中止","43"),
	/**任务暂停<RenWuZanTing>  */
	RenWuZanTing("7003","任务暂停","43"),
	/**任务重跑<RenWuChongPao>  */
	RenWuChongPao("7004","任务重跑","43"),
	/**任务完成<RenWuWanCheng>  */
	RenWuWanCheng("7005","任务完成","43"),
	/**作业中止<ZuoYeZhongZhi>  */
	ZuoYeZhongZhi("8001","作业中止","43"),
	/**作业重跑<ZuoYeChongPao>  */
	ZuoYeChongPao("8002","作业重跑","43"),
	/**作业跳过<ZuoYeTiaoGuo>  */
	ZuoYeTiaoGuo("8003","作业跳过","43");

	private final String code;
	private final String value;
	private final String catCode;

	ProjectLevel(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
