package hrds.agent.job.biz.constant;

/**
 * ClassName: FileFormatConstant <br/>
 * Function: 数据文件类型枚举 <br/>
 * Reason: 用于标识数据文件类型
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public enum FileFormatConstant implements EnumConstantInterface {
    CSV(1, "csv"),PARQUET(2, "parquet"),AVRO(3, "avro"),
    ORCFILE(4, "orcfile"),SEQUENCEFILE(5, "sequencefile"),
    OTHER(6, "other");

    private final int code;
    private final String message;

    FileFormatConstant(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

}