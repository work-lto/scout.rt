/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.server.commons.opentelemetry;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.opentelemetry.OpenTelemetryProperties.OpenTelemetryTracingEnabledProperty;
import org.eclipse.scout.rt.platform.util.CollectionUtility;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerAttributesExtractor;
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerAttributesGetter;
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerMetrics;
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerRoute;
import io.opentelemetry.instrumentation.api.semconv.http.HttpSpanNameExtractor;
import io.opentelemetry.instrumentation.api.semconv.http.HttpSpanNameExtractorBuilder;
import io.opentelemetry.instrumentation.api.semconv.http.HttpSpanStatusExtractor;

@ApplicationScoped
public class OpenTelemetryFilterInstrumenterFactory {

  public static final String INSTRUMENTATION_NAME = "scout.OpenTelemetryFilter";

  public Instrumenter<HttpServletRequest, HttpServletResponse> createInstrumenter() {
    OpenTelemetryServerHttpAttributesGetter attributesGetter = OpenTelemetryServerHttpAttributesGetter.INSTANCE;

    Function<SpanNameExtractor<? super HttpServletRequest>, ? extends SpanNameExtractor<? super HttpServletRequest>> spanNameExtractorTransformer = Function.identity();
    Function<SpanStatusExtractor<? super HttpServletRequest, ? super HttpServletResponse>, ? extends SpanStatusExtractor<? super HttpServletRequest, ? super HttpServletResponse>> statusExtractorTransformer = Function.identity();
    HttpSpanNameExtractorBuilder<HttpServletRequest> httpSpanNameExtractorBuilder = HttpSpanNameExtractor.builder(attributesGetter);
    SpanNameExtractor<? super HttpServletRequest> spanNameExtractor =
        spanNameExtractorTransformer.apply(httpSpanNameExtractorBuilder.build());

    return Instrumenter.<HttpServletRequest, HttpServletResponse> builder(
        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanNameExtractor)
        .setSpanStatusExtractor(
            statusExtractorTransformer.apply(HttpSpanStatusExtractor.create(attributesGetter)))
        .addAttributesExtractor(HttpServerAttributesExtractor.builder(attributesGetter).build())
        .addContextCustomizer(new SpanNamePropagationFromDownstream())
        .addContextCustomizer(HttpServerRoute.builder(attributesGetter).build())
        .addOperationMetrics(HttpServerMetrics.get())
        .setEnabled(CONFIG.getPropertyValue(OpenTelemetryTracingEnabledProperty.class))
        .buildInstrumenter(SpanKindExtractor.alwaysServer());
  }

  public enum OpenTelemetryServerHttpAttributesGetter
      implements HttpServerAttributesGetter<HttpServletRequest, HttpServletResponse> {
    INSTANCE;

    @Override
    public String getUrlScheme(HttpServletRequest httpServletRequest) {
      return httpServletRequest.getProtocol();
    }

    @Override
    public String getUrlPath(HttpServletRequest httpServletRequest) {
      return httpServletRequest.getRequestURI();
    }

    @Override
    public String getUrlQuery(HttpServletRequest httpServletRequest) {
      return httpServletRequest.getQueryString();
    }

    @Override
    public String getHttpRequestMethod(HttpServletRequest httpServletRequest) {
      return httpServletRequest.getMethod();
    }

    @Override
    public List<String> getHttpRequestHeader(HttpServletRequest httpServletRequest, String name) {
      Enumeration<String> enumeration = httpServletRequest.getHeaders(name);
      ArrayList<String> list = new ArrayList<>();
      while (enumeration.hasMoreElements()) {
        list.add(enumeration.nextElement());
      }
      return list;
    }

    @Override
    public Integer getHttpResponseStatusCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable error) {
      return httpServletResponse.getStatus();
    }

    @Override
    public List<String> getHttpResponseHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String name) {
      return CollectionUtility.arrayList(httpServletResponse.getHeaders(name));
    }

    @Override
    public String getHttpRoute(HttpServletRequest httpServletRequest) {
      return httpServletRequest.getRequestURI();
    }
  }
}
