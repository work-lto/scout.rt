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

import org.eclipse.scout.rt.client.extension.ui.form.fields.button.AbstractButtonExtension;
import org.eclipse.scout.rt.client.extension.ui.form.fields.button.ButtonChains.ButtonClickActionChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryButtonExtension extends AbstractButtonExtension<AbstractButton> implements IOpenTelemetryExtension<AbstractButton> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractButton>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.button";

  public OpenTelemetryButtonExtension(AbstractButton owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractButton> r) -> r.getOwner().getLabel());
  }

  @Override
  public void execClickAction(ButtonClickActionChain chain) {
    wrapCall(() -> super.execClickAction(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execClickAction"));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractButton>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
