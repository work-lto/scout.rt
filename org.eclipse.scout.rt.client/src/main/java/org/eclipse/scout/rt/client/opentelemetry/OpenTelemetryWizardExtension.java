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

import org.eclipse.scout.rt.client.extension.ui.wizard.AbstractWizardExtension;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardCancelChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardFinishChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardNextStepChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardPostStartChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardPreviousStepChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardStartChain;
import org.eclipse.scout.rt.client.extension.ui.wizard.WizardChains.WizardSuspendChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.wizard.AbstractWizard;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryWizardExtension extends AbstractWizardExtension<AbstractWizard> implements IOpenTelemetryExtension<AbstractWizard> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractWizard>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.wizard";

  public OpenTelemetryWizardExtension(AbstractWizard owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractWizard> r) -> r.getOwner().getTitle());
  }

  @Override
  public void execStart(WizardStartChain chain) {
    wrapCall(() -> super.execStart(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execStart"));
  }

  @Override
  public void execPostStart(WizardPostStartChain chain) {
    wrapCall(() -> super.execPostStart(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execPostStart"));
  }

  @Override
  public void execPreviousStep(WizardPreviousStepChain chain) {
    wrapCall(() -> super.execPreviousStep(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execPreviousStep"));
  }

  @Override
  public void execNextStep(WizardNextStepChain chain) {
    wrapCall(() -> super.execNextStep(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execNextStep"));
  }

  @Override
  public void execSuspend(WizardSuspendChain chain) {
    wrapCall(() -> super.execSuspend(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execSuspend"));
  }

  @Override
  public void execCancel(WizardCancelChain chain) {
    wrapCall(() -> super.execCancel(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execCancel"));
  }

  @Override
  public void execFinish(WizardFinishChain chain) {
    wrapCall(() -> super.execFinish(chain), new OpenTelemetryExtensionRequest<>(getOwner(), "execFinish"));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractWizard>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
