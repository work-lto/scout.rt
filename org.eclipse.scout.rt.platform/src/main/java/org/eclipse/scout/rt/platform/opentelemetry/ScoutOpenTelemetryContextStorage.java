/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.platform.opentelemetry;

import static org.eclipse.scout.rt.platform.IPlatform.State.BeanManagerValid;

import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.opentelemetry.OpenTelemetryProperties.OpenTelemetryTracingEnabledProperty;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.ContextStorageProvider;
import io.opentelemetry.context.Scope;

public class ScoutOpenTelemetryContextStorage implements ContextStorageProvider, IPlatformListener {
  private volatile boolean m_tracingEnabled = false;

  @Override
  public void stateChanged(PlatformEvent event) {
    if (event.getState() == BeanManagerValid) {
      m_tracingEnabled = CONFIG.getPropertyValue(OpenTelemetryTracingEnabledProperty.class);
    }
  }

  @Override
  public ContextStorage get() {
    ContextStorage threadLocalStorage = ContextStorage.defaultStorage();

    if (!m_tracingEnabled) {
      return threadLocalStorage;
    }

    return new ContextStorage() {
      @Override
      public Scope attach(Context toAttach) {
        Context current = current();
        RunContext runContext = RunContext.CURRENT.get();
        if (runContext != null) {
          runContext.withOpenTelemetryContext(toAttach);
        }
        Scope scope = threadLocalStorage.attach(toAttach);
        return () -> {
          if (runContext != null) {
            runContext.withOpenTelemetryContext(current);
          }
          scope.close();
        };
      }

      @Override
      public Context current() {
        return threadLocalStorage.current();
      }
    };
  }

}
