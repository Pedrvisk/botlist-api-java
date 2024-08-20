package xyz.mdbots.api.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception e) {

        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.put("statusText", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException e) {

        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", e.getStatusCode().value());
        res.put("statusText", e.getReason());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", HttpStatus.BAD_REQUEST.value());
        res.put("statusText", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException e) {

        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", HttpStatus.BAD_REQUEST.value());
        res.put("statusText", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {

        Map<String, Object> res = new HashMap<>();
        res.put("statusCode", HttpStatus.CONFLICT.value());
        res.put("statusText", e.getMostSpecificCause().getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }
}
