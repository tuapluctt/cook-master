package vn.hvt.cook_master.exception;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.hvt.cook_master.dto.response.ApiResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String ENUMCLASS_ATTRIBUTE = "enumClass";


    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception e) {
        log.error("Handling runtime exception", e);
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .code(ErrorCode.UNAUTHORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNAUTHORIZED_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse response = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAppException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse response = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Map<String,String>>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();


        e.getBindingResult()
                .getFieldErrors()
                .forEach((error) -> {

                    var constraintViolation  = error.unwrap(ConstraintViolation.class);
                    var attributes = constraintViolation.getConstraintDescriptor().getAttributes();

                    // In ra các attribute để kiểm tra
                    log.info( "attributes: " + attributes.toString());

                    String fieldName = error.getField();
                    String errorKey = error.getDefaultMessage();

                    log.info("errorKey: " + errorKey);

                    // Lấy ra mã lỗi từ errorKey vidu errorKey: ENUM_INVALID
                    ErrorCode errorCode = ErrorCode.valueOf(errorKey);

                    String message = errorCode.getMessage();

                    // Thay thế các placeholder trong message   {min}, {max}, {name}, {enumClass}
                    message = mapAttribute(message, attributes  , error);


                    errors.put(fieldName, message);
                });


        return ResponseEntity.badRequest().body(
                ApiResponse.<Map<String, String>>builder()
                        .code(ErrorCode.INVALID_DATA.getCode())
                        .message(ErrorCode.INVALID_DATA.getMessage())
                        .result(errors)
                        .build());
    }

    private String mapAttribute(String message, Map<String, Object> attributes, FieldError error) {

        // Thay thế các placeholder với giá trị từ attributes
        message = replacePlaceholder( message, MIN_ATTRIBUTE, attributes);
        message = replacePlaceholder(message, NAME_ATTRIBUTE, attributes);
        message = replaceEnumPlaceholder( message, ENUMCLASS_ATTRIBUTE, error);
        message = replacePlaceholder(message, MAX_ATTRIBUTE, attributes);

        // In ra message sau khi thay thế để kiểm tra
        System.out.println("message: " + message);
        return message;
    }


    private String replacePlaceholder(String message, String attribute, Map<String, Object> attributes) {
        if (message.contains("{" + attribute + "}")) {
            String value = String.valueOf(attributes.get(attribute));
            message = message.replace("{" + attribute + "}", value);
        }
        return message;
    }

    // Phương thức phụ để thay thế {enumClass} với các giá trị enum
    private String replaceEnumPlaceholder( String message, String attribute, FieldError error) {
        if (message.contains("{" + attribute + "}")) {
            String enumValues = extractEnumValues(error);
            message = message.replace("{" + attribute + "}", "[" + enumValues + "]");
        }
        return message;
    }

    private String extractEnumValues(FieldError fieldError) {
        Object[] arguments = fieldError.getArguments();
        if (arguments != null && arguments.length > 1) {
            Object enumClassArg = arguments[1];
            if (enumClassArg instanceof Class<?> enumClass && enumClass.isEnum()) {
                Object[] enumConstants = enumClass.getEnumConstants();
                return Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
            }
        }
        return "N/A";
    }
}
