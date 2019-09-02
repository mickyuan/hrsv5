package hrds.agent.job.biz.constant;

public enum CharSetConstant implements EnumConstantInterface {
    NONE(0, "none"), GBK(1, "gbk"),
    GB2312(2, "gb2312"), UTF8(3, "utf-8");

    private final int code;
    private final String message;

    CharSetConstant(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return message;
    }
}
