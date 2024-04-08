/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.client.opentelemetry;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.opentelemetry.OpenTelemetryProperties.OpenTelemetryTracingEnabledProperty;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

@ApplicationScoped
public class OpenTelemetryExtensionInstrumenterFactory {

  public <OWNER> Instrumenter<OpenTelemetryExtensionRequest<OWNER>, Void> createInstrumenter(String instrumentationName, String prefix,
      Function<OpenTelemetryExtensionRequest<OWNER>, String> getTextFunction) {
    SpanNameExtractor<OpenTelemetryExtensionRequest<OWNER>> spanNameExtractor = request -> request.getOwner().getClass().getSimpleName() + "." + request.getEventName();
    AttributesExtractor<OpenTelemetryExtensionRequest<OWNER>, Void> attributesExtractor = new AttributesExtractor<>() {
      private final AttributeKey<String> m_requestClass = io.opentelemetry.api.common.AttributeKey.stringKey(prefix + ".class");
      private final AttributeKey<String> m_requestName = AttributeKey.stringKey(prefix + ".text");
      private final AttributeKey<String> m_eventName = AttributeKey.stringKey(prefix + ".event.name");

      @Override
      public void onStart(AttributesBuilder attributes, Context parentContext, OpenTelemetryExtensionRequest<OWNER> request) {
        internalSet(attributes, m_requestClass, request.getOwner().getClass().getName());
        internalSet(attributes, m_requestName, getTextFunction.apply(request));
        internalSet(attributes, m_eventName, request.getEventName());

        for (Entry<String, String> eventInfo : request.getEventMap().entrySet()) {
          internalSet(attributes, AttributeKey.stringKey(prefix + ".event." + eventInfo.getKey()), eventInfo.getValue());
        }
      }

      @Override
      public void onEnd(AttributesBuilder attributes, Context context, OpenTelemetryExtensionRequest<OWNER> request, @Nullable Void unused, @Nullable Throwable error) {
        // nop
      }
    };
    return Instrumenter.<OpenTelemetryExtensionRequest<OWNER>, Void> builder(GlobalOpenTelemetry.get(),
        "scout." + instrumentationName,
        spanNameExtractor)
        .addAttributesExtractor(attributesExtractor)
        .setEnabled(CONFIG.getPropertyValue(OpenTelemetryTracingEnabledProperty.class))
        .buildInstrumenter();
  }

  public static class OpenTelemetryExtensionRequest<T> {
    private T m_owner;
    private String m_eventName;
    private Map<String, String> m_eventMap;

    public OpenTelemetryExtensionRequest(T owner, String eventName) {
      m_owner = owner;
      m_eventName = eventName;
      m_eventMap = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      OpenTelemetryExtensionRequest<?> that = (OpenTelemetryExtensionRequest<?>) o;
      return Objects.equals(m_owner, that.m_owner) && Objects.equals(m_eventName, that.m_eventName) && Objects.equals(m_eventMap, that.m_eventMap);
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_owner, m_eventName, m_eventMap);
    }

    public T getOwner() {
      return m_owner;
    }

    public String getEventName() {
      return m_eventName;
    }

    public Map<String, String> getEventMap() {
      return m_eventMap;
    }

    public OpenTelemetryExtensionRequest<T> withEventInfo(String eventInfoKey, String eventInfo) {
      m_eventMap.put(eventInfoKey, eventInfo);
      return this;
    }
  }
}
