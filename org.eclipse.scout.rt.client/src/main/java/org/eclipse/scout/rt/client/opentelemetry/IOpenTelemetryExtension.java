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

import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.platform.BEANS;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public interface IOpenTelemetryExtension<OWNER> {
  Instrumenter<OpenTelemetryExtensionRequest<OWNER>, Void> getInstrumenter();

  default Instrumenter<OpenTelemetryExtensionRequest<OWNER>, Void> createInstrumenter(String prefix,
      Function<OpenTelemetryExtensionRequest<OWNER>, String> getTextFunction) {
    return BEANS.get(OpenTelemetryExtensionInstrumenterFactory.class).createInstrumenter("scout." + this.getClass().getSimpleName(), prefix, getTextFunction);
  }

  default <T> T wrapCall(Supplier<T> execMethod, OpenTelemetryExtensionRequest<OWNER> request) {
    Context parentContext = Context.current();

    if (!getInstrumenter().shouldStart(parentContext, request)) {
      return execMethod.get();
    }

    Context context = getInstrumenter().start(parentContext, request);
    T result;
    try (Scope ignored = context.makeCurrent()) {
      result = execMethod.get();
    }
    catch (Throwable t) {
      getInstrumenter().end(context, request, null, t);
      throw t;
    }
    getInstrumenter().end(context, request, null, null);
    return result;
  }

  default void wrapCall(Runnable execMethod, OpenTelemetryExtensionRequest<OWNER> request) {
    Context parentContext = Context.current();

    if (!getInstrumenter().shouldStart(parentContext, request)) {
      execMethod.run();
      return;
    }

    Context context = getInstrumenter().start(parentContext, request);

    try (Scope ignored = context.makeCurrent()) {
      execMethod.run();
    }
    catch (Throwable t) {
      getInstrumenter().end(context, request, null, t);
      throw t;
    }
    getInstrumenter().end(context, request, null, null);
  }

}
