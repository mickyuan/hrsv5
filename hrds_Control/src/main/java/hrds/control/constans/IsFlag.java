package hrds.control.constans;

public enum IsFlag implements EnumConstantInterface {
    NO(0, "no"),YES(1, "yes");

    private final int code;
    private final String message;

    IsFlag(int code, String message){

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
