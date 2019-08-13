package com.upgrad.quora.api.controller.exception;

import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {


}
