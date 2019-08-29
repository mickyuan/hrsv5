package hrds.codes;
/**Created by automatic  */
/**代码类型名：作业操作类型  */
public enum ProjectLevel {
	/**等待<DengDai>  */
	DengDai("6000","等待","43","作业操作类型"),
	/**工程运行<GongChengYunXing>  */
	GongChengYunXing("6001","工程运行","43","作业操作类型"),
	/**工程中止<GongChengZhongZhi>  */
	GongChengZhongZhi("6002","工程中止","43","作业操作类型"),
	/**工程暂停<GongChengZanTing>  */
	GongChengZanTing("6003","工程暂停","43","作业操作类型"),
	/**工程重跑<GongChengChongPao>  */
	GongChengChongPao("6004","工程重跑","43","作业操作类型"),
	/**工程完成<GongChengWanCheng>  */
	GongChengWanCheng("6005","工程完成","43","作业操作类型"),
	/**任务运行<RenWuYunXing>  */
	RenWuYunXing("7001","任务运行","43","作业操作类型"),
	/**任务中止<RenWuZhongZhi>  */
	RenWuZhongZhi("7002","任务中止","43","作业操作类型"),
	/**任务暂停<RenWuZanTing>  */
	RenWuZanTing("7003","任务暂停","43","作业操作类型"),
	/**任务重跑<RenWuChongPao>  */
	RenWuChongPao("7004","任务重跑","43","作业操作类型"),
	/**任务完成<RenWuWanCheng>  */
	RenWuWanCheng("7005","任务完成","43","作业操作类型"),
	/**作业中止<ZuoYeZhongZhi>  */
	ZuoYeZhongZhi("8001","作业中止","43","作业操作类型"),
	/**作业重跑<ZuoYeChongPao>  */
	ZuoYeChongPao("8002","作业重跑","43","作业操作类型"),
	/**作业跳过<ZuoYeTiaoGuo>  */
	ZuoYeTiaoGuo("8003","作业跳过","43","作业操作类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ProjectLevel(String code,String value,String catCode,String catValue){
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
		for (ProjectLevel typeCode : ProjectLevel.values()) {
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
	public static ProjectLevel getCodeObj(String code) {
		for (ProjectLevel typeCode : ProjectLevel.values()) {
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
		return ProjectLevel.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ProjectLevel.values()[0].getCatCode();
	}
}
