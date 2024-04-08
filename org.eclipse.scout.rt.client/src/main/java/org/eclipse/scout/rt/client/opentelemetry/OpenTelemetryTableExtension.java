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

import java.util.List;

import org.eclipse.scout.rt.client.extension.ui.basic.table.AbstractTableExtension;
import org.eclipse.scout.rt.client.extension.ui.basic.table.TableChains.TableRowClickChain;
import org.eclipse.scout.rt.client.extension.ui.basic.table.TableChains.TableRowsSelectedChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.MouseButton;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryTableExtension extends AbstractTableExtension<AbstractTable> implements IOpenTelemetryExtension<AbstractTable> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractTable>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.table";

  public OpenTelemetryTableExtension(AbstractTable owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractTable> r) -> r.getOwner().getTitle());
  }

  @Override
  public void execRowsSelected(TableRowsSelectedChain chain, List<? extends ITableRow> rows) {
    wrapCall(() -> super.execRowsSelected(chain, rows),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execRowsSelected")
            .withEventInfo("rows.count", String.valueOf(rows.size())));
  }

  @Override
  public void execRowClick(TableRowClickChain chain, ITableRow row, MouseButton mouseButton) {
    wrapCall(() -> super.execRowClick(chain, row, mouseButton),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execRowClick")
            .withEventInfo("mouseButton", mouseButton.name())
            .withEventInfo("row.index", String.valueOf(row.getRowIndex())));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractTable>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
