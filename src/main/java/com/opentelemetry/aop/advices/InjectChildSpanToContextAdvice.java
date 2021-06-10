package com.opentelemetry.aop.advices;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import com.opentelemetry.annotations.InjectChildSpanToContext;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

@Aspect
public class InjectChildSpanToContextAdvice {

	@Around("@annotation(com.opentelemetry.annotations.InjectChildSpanToContext) && execution(* *(..))")
	public Object injectSpan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
		Object object;

		try {

			if (openTelemetry != null) {

				MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
				Method method = signature.getMethod();
				InjectChildSpanToContext injectTraceSpan = method.getAnnotation(InjectChildSpanToContext.class);
				String spanName = injectTraceSpan.spanName();

				String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
				Tracer tracer = openTelemetry.getTracer(className);

				String methodName = proceedingJoinPoint.getSignature().getName();

				Span span = tracer.spanBuilder(spanName == null ? methodName : spanName).setParent(Context.current())
						.startSpan();

				object = proceedingJoinPoint.proceed(); // Invoking InjectChildSpanToContext annotation added method
				span.end();

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
