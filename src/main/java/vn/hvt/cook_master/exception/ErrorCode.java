package vn.hvt.cook_master.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


public enum ErrorCode {
    UNAUTHORIZED_EXCEPTION(999, "Unauthorized" , HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(0001, "Invalid message key" , HttpStatus.BAD_REQUEST),
    INVALID_DATA(1003, "Invalid data" , HttpStatus.BAD_REQUEST),
    PERMISSION_EXISTED(1001, "Permission existed" , HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed" , HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001, "User not existed" , HttpStatus.NOT_FOUND),
    FIELD_INVALID(1002, " must be between {min} and {max} characters" , HttpStatus.BAD_REQUEST),
    NOT_BLANK(1004, "Field cannot be empty" , HttpStatus.BAD_REQUEST),
    UNAUTHENTICATION(1005," UnAuthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006," You do not have permission" , HttpStatus.FORBIDDEN),
    DOB_INVALID(1008, "you age must be at least {min}" , HttpStatus.BAD_REQUEST),
    ENUM_INVALID(1009,"{name} must be any of {enumClass}" , HttpStatus.BAD_REQUEST),

    ;



    private  int code;
    private  String message;
    private HttpStatusCode httpStatusCode;


    ErrorCode(int code, String message , HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
