/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.client.transformation;

import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryActionExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryButtonExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryCalendarItemProviderExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryFormHandlerExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryPageWithTableExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryTableExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryTreeExtension;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryWizardExtension;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IPlatform.State;
import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;
import org.eclipse.scout.rt.shared.extension.IExtensionRegistry;

public class DeviceTransformationPlatformListener implements IPlatformListener {

  @Override
  public void stateChanged(PlatformEvent event) {
    if (event.getState() == State.PlatformStarted) {
      BEANS.get(IExtensionRegistry.class).register(DesktopExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OutlineExtension.class);
      BEANS.get(IExtensionRegistry.class).register(PageExtension.class);
      BEANS.get(IExtensionRegistry.class).register(PageWithTableExtension.class);
      BEANS.get(IExtensionRegistry.class).register(FormExtension.class);
      BEANS.get(IExtensionRegistry.class).register(FormFieldExtension.class);

      // Observability
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryActionExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryButtonExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryCalendarItemProviderExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryFormHandlerExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryPageWithTableExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryTableExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryTreeExtension.class);
      BEANS.get(IExtensionRegistry.class).register(OpenTelemetryWizardExtension.class);
    }
  }

}
