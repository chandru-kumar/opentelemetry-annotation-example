package com.opentelemetry.aop.advices;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@Aspect
public class InjectTraceSpanAdvice {
	
	OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	@Around("@annotation(com.opentelemetry.annotations.InjectTraceSpan) && execution(* *(..))")
	public Object injectSpan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example");
		Object[] args = proceedingJoinPoint.getArgs();
		System.out.println(args.toString());
		System.out.println("Export Trace uisng Annotations...");
		String methodName = proceedingJoinPoint.getSignature().getName();
		String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
		Span span = tracer.spanBuilder(className+ "-" +methodName).startSpan();
		Object object;
		try (Scope scope = span.makeCurrent()) {
			object = proceedingJoinPoint.proceed();
		} finally {
			span.end();
		}
		System.out.println("Export Trace uisng Annotations...Done");
		return object;
	}

}
