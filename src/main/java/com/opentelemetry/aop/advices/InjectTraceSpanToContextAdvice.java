package com.opentelemetry.aop.advices;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

@Aspect
public class InjectTraceSpanToContextAdvice {
	
	OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	@Around("@annotation(com.opentelemetry.annotations.InjectTraceSpanToContext) && execution(* *(..))")
	public Object injectSpan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example");
		String methodName = proceedingJoinPoint.getSignature().getName();
		String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
		Span span = tracer.spanBuilder(className+ "-" +methodName).setParent(Context.current()).startSpan();
		Object object;
		try (Scope scope = span.makeCurrent()) {
			object = proceedingJoinPoint.proceed();
		} finally {
			span.end();
		}
		return object;
	}

}
