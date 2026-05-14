package com.kira.leavemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status, String message, HttpServletRequest req) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());

        return new ResponseEntity<>(body, status);
    }

    // Validation errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(
            IllegalArgumentException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Map<String,Object>> handleUnAuthorEntity(UnAuthorizedException e,HttpServletRequest req){
        return build(HttpStatus.FORBIDDEN,e.getMessage(),req);
    }
    // Not found
    @ExceptionHandler({java.util.NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(
            RuntimeException e, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    // Auth / JWT (used if thrown from controller/service)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpired(
            ExpiredJwtException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Token expired", req);
    }

    @ExceptionHandler({ MalformedJwtException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidJwt(
            RuntimeException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid token", req);
    }

    // 🔹 Fallback (server errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(
            Exception e, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(), req);
    }
}