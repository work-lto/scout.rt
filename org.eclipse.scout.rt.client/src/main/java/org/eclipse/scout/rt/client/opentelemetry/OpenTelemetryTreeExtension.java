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

import org.eclipse.scout.rt.client.extension.ui.basic.tree.AbstractTreeExtension;
import org.eclipse.scout.rt.client.extension.ui.basic.tree.TreeChains.TreeNodeClickChain;
import org.eclipse.scout.rt.client.opentelemetry.OpenTelemetryExtensionInstrumenterFactory.OpenTelemetryExtensionRequest;
import org.eclipse.scout.rt.client.ui.MouseButton;
import org.eclipse.scout.rt.client.ui.basic.tree.AbstractTree;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class OpenTelemetryTreeExtension extends AbstractTreeExtension<AbstractTree> implements IOpenTelemetryExtension<AbstractTree> {

  private Instrumenter<OpenTelemetryExtensionRequest<AbstractTree>, Void> m_instrumenter;
  private static final String PREFIX = "scout.client.tree";

  public OpenTelemetryTreeExtension(AbstractTree owner) {
    super(owner);
    m_instrumenter = createInstrumenter(PREFIX, (OpenTelemetryExtensionRequest<AbstractTree> r) -> r.getOwner().getTitle());
  }

  @Override
  public void execNodeClick(TreeNodeClickChain chain, ITreeNode node, MouseButton mouseButton) {
    wrapCall(() -> super.execNodeClick(chain, node, mouseButton),
        new OpenTelemetryExtensionRequest<>(getOwner(), "execNodeClick")
            .withEventInfo("mouseButton", mouseButton.name())
            .withEventInfo("node.id", node.getNodeId())
            .withEventInfo("node.text", node.getCell().getText()));
  }

  @Override
  public Instrumenter<OpenTelemetryExtensionRequest<AbstractTree>, Void> getInstrumenter() {
    return m_instrumenter;
  }
}
