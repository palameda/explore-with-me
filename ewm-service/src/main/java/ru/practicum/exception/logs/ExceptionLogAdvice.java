package ru.practicum.exception.logs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLogAdvice {

    @Pointcut("@annotation(ExceptionLog)")
    public void processing() {}

    @After("processing()")
    @SneakyThrows
    public void doLogging(JoinPoint jp) {
        log.info("Обработчиком {} перехвачена ошибка {}", jp.getSignature().getName(), jp.getArgs());
    }
}
