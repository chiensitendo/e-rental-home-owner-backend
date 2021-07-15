package com.e_rental.owner.handling;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.enums.StatusCode;
import com.e_rental.owner.dto.responses.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@ControllerAdvice(basePackages = "com.e_rental.owner.controllers")
public class GlobalExceptionHandling extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(ErrorDto.class)
    private ResponseEntity<? extends Response> processErrorDto(ErrorDto err) {
        Response res = new Response();
        res.setCode(StatusCode.BAD_REQUEST.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<? extends Response> processResourceNotFoundException(ResourceNotFoundException err) {
        Response res = new Response();
        res.setCode(StatusCode.NOT_FOUND.getCode());
        res.setMessage(err.getMessage());
        return new ResponseEntity<Response>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    private ResponseEntity<? extends Response> processForbiddenException(ForbiddenException err) {
        Response res = new Response();
        res.setCode(StatusCode.FORBIDDEN.getCode());
        res.setMessage(err.getMessage());
        return new ResponseEntity<Response>(res, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternationalErrorException.class)
    private ResponseEntity<? extends Response> processInternationalErrorException(InternationalErrorException err) {
        Response res = new Response();
        res.setCode(StatusCode.INTERNATIONAL_ERROR.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.internalServerError().body(res);
    }
}
