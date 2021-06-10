package com.opentelemetry.annotations.example;

import com.opentelemetry.annotations.InjectChildSpanToContext;
import com.opentelemetry.annotations.InjectSpan;
import com.opentelemetry.annotations.InjectSpanToContext;
import io.opentelemetry.api.OpenTelemetry;

/**
 * Example code for using Annotations.
 *
 * <p>
 * If you wish to use this annotation code, the aspectj plugin needs to be
 * included in the app to build and package.
 */
public final class ExportSpansWithAnnotation {

	public static OpenTelemetry openTelemetry = OpenTelemetryManager.initOpenTelemetry();

	public static void main(String[] args) throws InterruptedException {

		login();

	}

	@InjectSpan(spanName="Login")
	static void login() throws InterruptedException {
		Thread.sleep(1000);
		dashboard();
	}

	@InjectSpanToContext(spanName="Dashboard")
	private static void dashboard() throws InterruptedException {
		portfolio();
		latestUpdates();
		offers();
	}

	@InjectChildSpanToContext(spanName="Portfolio")
	private static void portfolio() throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(300);

	}

	@InjectChildSpanToContext(spanName="Latest Updates")
	private static void latestUpdates() throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(100);

	}

	@InjectChildSpanToContext(spanName="offers")
	private static void offers() throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(200);
	}

}
