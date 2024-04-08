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

import org.eclipse.scout.rt.client.extension.ui.form.AbstractFormHandlerExtension;
import org.eclipse.scout.rt.client.extension.ui.form.FormHandlerChains.FormHandlerDiscardChain;
import org.eclipse.scout.rt.client.extension.ui.form.FormHandlerChains.FormHandlerLoadChain;
import org.eclipse.scout.rt.client.extension.ui.form.FormHandlerChains.FormHandlerPostLoadChain;
import org.eclipse.scout.rt.client.extension.ui.form.FormHandlerChains.FormHandlerStoreChain;
import org.eclipse.scout.rt.client.extension.ui.form.FormHandlerChains.FormHandlerValidateChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryFormHandlerExtension extends AbstractFormHandlerExtension<AbstractFormHandler> implements IOpenTelemetryExtension<AbstractFormHandler> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractFormHandler>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.formhandler";

  public OpenTelemetryFormHandlerExtension(AbstractFormHandler owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractFormHandler> r) -> r.getOwner().getForm().getTitle());
  }

  @Override
  public void execLoad(FormHandlerLoadChain chain) {
    wrapCall(() -> super.execLoad(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execLoad"));
  }

  @Override
  public void execPostLoad(FormHandlerPostLoadChain chain) {
    wrapCall(() -> super.execPostLoad(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execPostLoad"));
  }

  @Override
  public boolean execValidate(FormHandlerValidateChain chain) {
    return wrapCall(() -> super.execValidate(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execValidate"));
  }

  @Override
  public void execStore(FormHandlerStoreChain chain) {
    wrapCall(() -> super.execStore(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execStore"));
  }

  @Override
  public void execDiscard(FormHandlerDiscardChain chain) {
    wrapCall(() -> super.execDiscard(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execDiscard"));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractFormHandler>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
