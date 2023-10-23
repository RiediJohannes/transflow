package at.fhv.transflow.simulation;

public class SystemError extends Throwable {
    private final int code;
    private final String message;

    public SystemError(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public SystemError(ErrorCode errorCode, String customMessage) {
        this.code = errorCode.getCode();
        this.message = customMessage;
    }


    public int getSystemCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}