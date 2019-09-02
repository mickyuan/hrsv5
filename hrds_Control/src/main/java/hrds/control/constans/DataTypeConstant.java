package hrds.control.constans;

public enum DataTypeConstant implements EnumConstantInterface {
    //TODO 从0开始是因为海云是如此
    STRING(0, "string"),CHAR(1, "char"),BOOLEAN(3, "boolean"),
    INT(4, "int"),DECIMAL(5, "decimal"),FLOAT(6, "float"),
    DOUBLE(7, "double");

    private final int code;
    private final String message;

    DataTypeConstant(int code, String message) {
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
