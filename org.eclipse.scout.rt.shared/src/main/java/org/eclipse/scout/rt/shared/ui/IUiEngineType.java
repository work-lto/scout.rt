/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.shared.ui;

import java.io.Serializable;

import org.eclipse.scout.rt.dataobject.enumeration.IEnum;

/**
 * Browser engine type
 *
 * @since 6.0.0
 */
@FunctionalInterface
public interface IUiEngineType extends Serializable, IEnum {
}
