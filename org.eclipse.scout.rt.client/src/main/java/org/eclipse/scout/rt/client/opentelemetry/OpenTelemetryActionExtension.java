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

import org.eclipse.scout.rt.client.extension.ui.action.AbstractActionExtension;
import org.eclipse.scout.rt.client.extension.ui.action.ActionChains.ActionActionChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.action.AbstractAction;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryActionExtension extends AbstractActionExtension<AbstractAction> implements IOpenTelemetryExtension<AbstractAction> {
  private Instrumenter<OpenTelemetryExtensionRequest<AbstractAction>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.action";

  public OpenTelemetryActionExtension(AbstractAction owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractAction> r) -> r.getOwner().getText());
  }

  @Override
  public void execAction(ActionActionChain chain) {
    wrapCall(() -> super.execAction(chain),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execAction")
            .withEventInfo("id", getOwner().getActionId()));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractAction>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
