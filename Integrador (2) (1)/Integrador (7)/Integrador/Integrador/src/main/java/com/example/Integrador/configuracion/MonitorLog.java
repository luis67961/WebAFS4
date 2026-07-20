package com.example.Integrador.configuracion;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class MonitorLog {
    private static final Logger logger = LoggerFactory.getLogger(MonitorLog.class);
    // Mide el tiempo de ejecución (rendimiento) y escribe el log automáticamente
    @Around("execution(* com.example.Integrador.service.*.*(..))")
    public Object monitorearRendimientoYLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long tiempoInicio = System.currentTimeMillis();
        
        // Ejecuta el método original del servicio
        Object resultado = joinPoint.proceed();
        
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
        String metodo = joinPoint.getSignature().getName();
        
        // Log combinado: Auditoría + Rendimiento
        logger.info("MONITOREO | Método: {} | Tiempo de respuesta: {} ms | Estado: COMPLETADO", metodo, tiempoTotal);
        
        return resultado;
    }
}