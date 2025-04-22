package vn.hvt.cook_master.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


public enum ErrorCode {
    UNAUTHORIZED_EXCEPTION(999, "Unauthorized" , HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN(999, "Invalid token" , HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(0001, "Invalid message key" , HttpStatus.BAD_REQUEST),
    INVALID_DATA(1003, "Invalid data" , HttpStatus.BAD_REQUEST),
    PERMISSION_EXISTED(1001, "Permission existed" , HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(1001, "Permission not existed" , HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1001, "Role existed" , HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1001, "Role not existed" , HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed" , HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001, "User not existed" , HttpStatus.NOT_FOUND),
    INVALID_EMAIL(1001, "Invalid email" , HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1001, "Email existed" , HttpStatus.BAD_REQUEST),
    FIELD_INVALID(1002, " must be between {min} and {max} characters" , HttpStatus.BAD_REQUEST),
    NOT_BLANK(1004, "Field cannot be empty" , HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005," UnAuthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006," You do not have permission" , HttpStatus.FORBIDDEN),
    DOB_INVALID(1008, "you age must be at least {min}" , HttpStatus.BAD_REQUEST),
    ENUM_INVALID(1009,"{name} must be any of {enumClass}" , HttpStatus.BAD_REQUEST),
    POSITIVE(1007,"{name} must be positive" , HttpStatus.BAD_REQUEST),

    //IMAGE
    UPLOAD_IMAGE_FAILED(1010,"Failed to upload image" , HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(1010,"Image not found" ,HttpStatus.NOT_FOUND),
    DELETE_IMAGE_FAILED(1010,"Failed to delete image" ,HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(1011,"Failed to upload file" ,HttpStatus.BAD_REQUEST),
    FORBIDDEN(1403, "FORBIDDEN",HttpStatus.FORBIDDEN ),


    //USER
    USER_NOT_FOUND(1001, "User not found" , HttpStatus.NOT_FOUND),

    // RECIPE
    INVALID_FIELD(2002, "Invalid field" , HttpStatus.BAD_REQUEST),
    RECIPE_NOT_FOUND(2001, "Recipe not found" , HttpStatus.NOT_FOUND),
    FIELD_NULL_OR_EMPTY(2003, "Field cannot be null or empty" , HttpStatus.BAD_REQUEST),



    // INGREDIENT
    INGREDIENT_NOT_FOUND(2001, "Ingredient not found" , HttpStatus.NOT_FOUND),


    // RECIPE INGREDIENT
    RECIPE_INGREDIENT_NOT_FOUND(2001, "Recipe ingredient not found" , HttpStatus.NOT_FOUND),

    // RECIPE STEP
    STEP_NOT_FOUND(2001, "Step not found" , HttpStatus.NOT_FOUND),
    STEP_IMAGE_NOT_FOUND(2001, "Step image not found" , HttpStatus.NOT_FOUND);



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
