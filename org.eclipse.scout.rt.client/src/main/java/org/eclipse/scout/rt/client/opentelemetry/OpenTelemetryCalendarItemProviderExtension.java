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

import java.util.Date;
import java.util.Set;

import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.extension.ui.basic.calendar.provider.AbstractCalendarItemProviderExtension;
import org.eclipse.scout.rt.client.extension.ui.basic.calendar.provider.CalendarItemProviderChains.CalendarItemProviderItemActionChain;
import org.eclipse.scout.rt.client.extension.ui.basic.calendar.provider.CalendarItemProviderChains.CalendarItemProviderLoadItemsChain;
import org.eclipse.scout.rt.client.extension.ui.basic.calendar.provider.CalendarItemProviderChains.CalendarItemProviderLoadItemsInBackgroundChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.basic.calendar.provider.AbstractCalendarItemProvider;
import org.eclipse.scout.rt.shared.services.common.calendar.ICalendarItem;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryCalendarItemProviderExtension extends AbstractCalendarItemProviderExtension<AbstractCalendarItemProvider> implements IOpenTelemetryExtension<AbstractCalendarItemProvider> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractCalendarItemProvider>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.calendarItemProvider";

  public OpenTelemetryCalendarItemProviderExtension(AbstractCalendarItemProvider owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractCalendarItemProvider> r) -> "");
  }

  @Override
  public void execLoadItems(CalendarItemProviderLoadItemsChain chain, Date minDate, Date maxDate, Set<ICalendarItem> result) {
    wrapCall(() -> super.execLoadItems(chain, minDate, maxDate, result),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execLoadItems")
            .withEventInfo("minDate", minDate.toString())
            .withEventInfo("maxDate", maxDate.toString()));
  }

  @Override
  public void execLoadItemsInBackground(CalendarItemProviderLoadItemsInBackgroundChain chain, IClientSession session, Date minDate, Date maxDate, Set<ICalendarItem> result) {
    wrapCall(() -> super.execLoadItemsInBackground(chain, session, minDate, maxDate, result),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execLoadItemsInBackground")
            .withEventInfo("minDate", minDate.toString())
            .withEventInfo("maxDate", maxDate.toString()));
  }

  @Override
  public void execItemAction(CalendarItemProviderItemActionChain chain, ICalendarItem item) {
    wrapCall(() -> super.execItemAction(chain, item),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execItemAction")
            .withEventInfo("item.text", item.getSubject()));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractCalendarItemProvider>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
