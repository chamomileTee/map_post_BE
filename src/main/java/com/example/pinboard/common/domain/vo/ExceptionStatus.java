package com.example.pinboard.common.domain.vo;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ExceptionStatus
 * <p>예외의 상태, 메시지, HTTP 코드 등을 정의하는 Enum 클래스</p>
 * <p>예외 상태와 메시지 정의를 일관되게 하고, 여러 도메인에서 재사용할 수 있는 상수 집합으로 사용한다.</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-09
 */
@Getter
public enum ExceptionStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request",4001),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid Input",4002),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"Invalid Password",4003),
    EXPIRED(HttpStatus.GONE,"Resource Expired",4101),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized",401),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"Expired Token",4102),
    NO_VALID_TOKEN(HttpStatus.NOT_FOUND, "No valid token found for the provided user ID", 4042),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED,"Invalid Token Signature",4103),
    MALFORMED_TOKEN(HttpStatus.BAD_REQUEST, "Malformed Token", 4104),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "Access Denied",403),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User is not found with the provided email, ID or nickname", 4041),
    ALREADY_LOGGED_IN(HttpStatus.CONFLICT, "User is already logged in",409),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password", 4011),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",500),
    DATA_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,"Data not found in DB",5001);

    private final HttpStatus httpStatus;
    private final String message;
    private final int errorCode;
    ExceptionStatus(HttpStatus httpStatus, String message, int errorCode) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorCode = errorCode;
    }
}
