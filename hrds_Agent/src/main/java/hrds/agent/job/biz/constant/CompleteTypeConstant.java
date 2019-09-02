package hrds.agent.job.biz.constant;

/**
 * @description: 字段清洗补齐类型枚举类
 * @author: WangZhengcheng
 * @create: 2019-08-30 14:23
 **/
public enum CompleteTypeConstant implements EnumConstantInterface{

    BEFORE(1, "before"), AFTER(2, "after");
    
    private final int code;
    private final String message;

    CompleteTypeConstant(int code, String message) {
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
        return "CompleteTypeConstant{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
