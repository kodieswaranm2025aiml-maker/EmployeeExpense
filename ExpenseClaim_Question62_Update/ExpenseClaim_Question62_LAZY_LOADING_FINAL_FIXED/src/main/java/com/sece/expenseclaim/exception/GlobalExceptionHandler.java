package com.sece.expenseclaim.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String,Object>> response(HttpStatus status,String message){
        Map<String,Object> body=new LinkedHashMap<>(); body.put("timestamp", LocalDateTime.now()); body.put("status",status.value()); body.put("error",status.getReasonPhrase()); body.put("message",message); return ResponseEntity.status(status).body(body);
    }
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e){return response(HttpStatus.NOT_FOUND,e.getMessage());}
    @ExceptionHandler(BusinessRuleException.class) public ResponseEntity<Map<String,Object>> rule(BusinessRuleException e){return response(HttpStatus.CONFLICT,e.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){
        Map<String,String> fields=new LinkedHashMap<>(); for(FieldError f:e.getBindingResult().getFieldErrors()) fields.put(f.getField(),f.getDefaultMessage());
        Map<String,Object> body=new LinkedHashMap<>(); body.put("timestamp",LocalDateTime.now()); body.put("status",400); body.put("error","Bad Request"); body.put("message","Validation failed"); body.put("fields",fields); return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(Exception.class) public ResponseEntity<Map<String,Object>> general(Exception e){return response(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error: "+e.getMessage());}
}
