package com.aptmgt.customer.exceptions;

import com.aptmgt.commons.dto.ErrorResponse;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.ResponseMessages;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            details.add(errorMessage);
        });

        ErrorResponse error = new ErrorResponse(ResponseMessages.BAD_REQUEST_MSG, false, details);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());

        ErrorResponse error = new ErrorResponse(ResponseMessages.BAD_REQUEST_MSG, false, details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public static final ResponseEntity<ErrorResponse> handleRecordNotFoundException(RecordNotFoundException ex,
                                                                                    WebRequest request) {
        List<String> details = new ArrayList<>();
        if (ex.getLocalizedMessage() != null) {
            details.add(ex.getLocalizedMessage());
        }
        ErrorResponse error = new ErrorResponse(ResponseMessages.INCORRECT_REQUEST, false, details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
