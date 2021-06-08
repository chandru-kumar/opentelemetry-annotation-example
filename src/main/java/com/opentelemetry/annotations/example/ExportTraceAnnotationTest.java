/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.opentelemetry.annotations.example;

import com.opentelemetry.annotations.InjectTraceSpan;
import com.opentelemetry.annotations.InjectTraceSpanToContext;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongValueRecorder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

/**
 * Example code for setting up the OTLP exporters.
 *
 * <p>
 * If you wish to use this code, you'll need to run a copy of the collector
 * locally, on the default port. There is a docker-compose configuration for
 * doing this in the docker subdirectory of this module.
 */
public final class ExportTraceAnnotationTest {

	public static OpenTelemetry openTelemetry = OpenTelemetryManager.initOpenTelemetry();

	public static void main(String[] args) throws InterruptedException {
		// this will make sure that a proper service.name attribute is set on all the
		// spans/metrics.
		// note: this is not something you should generally do in code, but should be
		// provided on the
		// command-line. This is here to make the example more self-contained.
		System.setProperty("otel.resource.attributes", "service.name=ExportTraceAnnotationExample");

		// it is important to initialize your SDK as early as possible in your
		// application's lifecycle

		// note: currently metrics is alpha and the configuration story is still
		// unfolding. This will
		// definitely change in the future.
		MeterProvider meterProvider = OpenTelemetryManager.initOpenTelemetryMetrics();
		Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example");
		Meter meter = meterProvider.get("io.opentelemetry.example");
		LongCounter counter = meter.longCounterBuilder("example_counter").build();
		LongValueRecorder recorder = meter.longValueRecorderBuilder("super_timer").setUnit("ms").build();
		Span span = tracer.spanBuilder("AnnotationExample").startSpan();
		long startTime = System.currentTimeMillis();

		try (Scope scope = span.makeCurrent()) {
			counter.add(1);
			span.setAttribute("good", "true");
			span.setAttribute("exampleNumber", 1);
			Thread.sleep(100);
			
			Span span1 = tracer.spanBuilder("Span - 1").startSpan();
			Thread.sleep(200);
			span1.end();
			
			injectSpanInContext();
		} finally {
			recorder.record(System.currentTimeMillis() - startTime);
			span.end();
		}
		
	}
	
	@InjectTraceSpanToContext
	private static void injectSpanInContext() throws InterruptedException {
		Thread.sleep(1000);
		
	}

	@InjectTraceSpan
	static void createTrace(LongValueRecorder recorder) {
		System.out.println("createTraceMethod");
		recorder.record(System.currentTimeMillis());
		System.out.println("createTraceMethod....End");
	}
	
	
}
