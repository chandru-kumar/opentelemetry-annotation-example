package com.opentelemetry.aop.advices;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import com.opentelemetry.annotations.InjectSpanToContext;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

@Aspect
public class InjectSpanToContextAdvice {

	@Around("@annotation(com.opentelemetry.annotations.InjectSpanToContext) && execution(* *(..))")
	public Object injectSpan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
		Object object;

		try {

			if (openTelemetry != null) {

				MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
				Method method = signature.getMethod();
				InjectSpanToContext injectTraceSpan = method.getAnnotation(InjectSpanToContext.class);
				String spanName = injectTraceSpan.spanName();

				String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
				Tracer tracer = openTelemetry.getTracer(className);

				String methodName = proceedingJoinPoint.getSignature().getName();

				Span span = tracer.spanBuilder(spanName == null ? methodName : spanName).setParent(Context.current())
						.startSpan();
				try (Scope scope = span.makeCurrent()) {
					object = proceedingJoinPoint.proceed(); // Invoking InjectSpanToContext annotation added method
				} finally {
					span.end();
				}

				return object;

			} else {
				object = proceedingJoinPoint.proceed();
				return object;
			}

		} catch (Exception e) {
			object = proceedingJoinPoint.proceed();
			return object;
		}

	}

}
