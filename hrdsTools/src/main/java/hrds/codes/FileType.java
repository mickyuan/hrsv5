package hrds.codes;
/**Created by automatic  */
/**代码类型名：资源管理器文件类型  */
public enum FileType {
	/**全部文件<All>  */
	All("1001","全部文件","22","资源管理器文件类型"),
	/**图片<TuPian>  */
	TuPian("1002","图片","22","资源管理器文件类型"),
	/**文档<WenDang>  */
	WenDang("1003","文档","22","资源管理器文件类型"),
	/**PDF文件<PDFFile>  */
	PDFFile("1013","PDF文件","22","资源管理器文件类型"),
	/**office文件<OfficeFile>  */
	OfficeFile("1023","office文件","22","资源管理器文件类型"),
	/**文本文件<WenBenFile>  */
	WenBenFile("1033","文本文件","22","资源管理器文件类型"),
	/**压缩文件<YaSuoFile>  */
	YaSuoFile("1043","压缩文件","22","资源管理器文件类型"),
	/**日志文件<RiZhiFile>  */
	RiZhiFile("1053","日志文件","22","资源管理器文件类型"),
	/**表数据文件<biaoShuJuFile>  */
	biaoShuJuFile("1063","表数据文件","22","资源管理器文件类型"),
	/**视频<ShiPin>  */
	ShiPin("1004","视频","22","资源管理器文件类型"),
	/**音频<YinPin>  */
	YinPin("1005","音频","22","资源管理器文件类型"),
	/**其它<Other>  */
	Other("1006","其它","22","资源管理器文件类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	FileType(String code,String value,String catCode,String catValue){
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
		for (FileType typeCode : FileType.values()) {
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
	public static FileType getCodeObj(String code) {
		for (FileType typeCode : FileType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据code没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return FileType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return FileType.values()[0].getCatCode();
	}
}
