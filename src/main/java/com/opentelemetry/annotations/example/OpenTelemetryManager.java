/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.opentelemetry.annotations.example;

import java.util.concurrent.TimeUnit;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.OpenTelemetrySdkAutoConfiguration;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

/**
 * All SDK management takes place here, away from the instrumentation code,
 * which should only access the OpenTelemetry APIs.
 */
public final class OpenTelemetryManager {

	/**
	 * Adds a BatchSpanProcessor initialized with OtlpGrpcSpanExporter to the
	 * TracerSdkProvider.
	 *
	 * @return a ready-to-use {@link OpenTelemetry} instance.
	 */
	public static OpenTelemetry initOpenTelemetry() {
		OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder().setTimeout(2, TimeUnit.SECONDS).build();
		BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(spanExporter)
				.setScheduleDelay(100, TimeUnit.MILLISECONDS).build();

		Resource serviceName = Resource
				.create(Attributes.of(AttributeKey.stringKey("service.name"), "AnnotationExampleService"));

		SdkTracerProvider tracerProvider = SdkTracerProvider.builder().addSpanProcessor(spanProcessor)
				.setResource(OpenTelemetrySdkAutoConfiguration.getResource().merge(serviceName)).build();
		OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider)
				.buildAndRegisterGlobal();

		Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

		return openTelemetrySdk;
	}

}
