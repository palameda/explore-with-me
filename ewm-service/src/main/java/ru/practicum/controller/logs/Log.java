package ru.practicum.controller.logs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class Log {
    private final HttpServletRequest httpRequest;

    @Pointcut("@annotation(ControllerLog)")
    void loggingProcess() {
    }

    @Before("loggingProcess()")
    @SneakyThrows
    void doLogging() {
        log.info("Метод: {}, URI: {}, Запрос: {}", httpRequest.getMethod(), httpRequest.getRequestURI(), httpRequest.getQueryString());
    }
}
