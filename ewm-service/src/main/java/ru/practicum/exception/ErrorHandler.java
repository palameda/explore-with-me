package ru.practicum.exception;

import lombok.ToString;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.exception.ApiError;
import ru.practicum.exception.logs.ExceptionLog;

@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleDateParameterException(final DataIntegrityViolationException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleDeleteEntityException(final DeleteEntityException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleForbiddenActionException(final ForbiddenActionException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleRequestProcessingException(final RequestProcessingException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleDateParameterException(final DateParameterException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    @ExceptionLog
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.createApiErrorFromException(e, HttpStatus.NOT_FOUND);
    }
}
