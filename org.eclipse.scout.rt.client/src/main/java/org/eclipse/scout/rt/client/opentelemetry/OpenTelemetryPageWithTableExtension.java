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

import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.AbstractPageWithTableExtension;
import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.PageWithTableChains.PageWithTablePopulateTableChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryPageWithTableExtension<T extends ITable> extends AbstractPageWithTableExtension<T, AbstractPageWithTable<T>> implements IOpenTelemetryExtension<AbstractPageWithTable<T>> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractPageWithTable<T>>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.pageWithTable";

  public OpenTelemetryPageWithTableExtension(AbstractPageWithTable<T> owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractPageWithTable<T>> r) -> r.getOwner().getTable().getTitle());
  }

  @Override
  public void execPopulateTable(PageWithTablePopulateTableChain<? extends ITable> chain) {
    wrapCall(() -> super.execPopulateTable(chain),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execPopulateTable")
            .withEventInfo("id", getOwner().getNodeId()));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractPageWithTable<T>>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
