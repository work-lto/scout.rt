/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.commons;

/**
 * @deprecated Will be removed in N release. Use {@link org.eclipse.scout.commons.mail.CharsetSafeMimeMessage} instead.
 */
@Deprecated
public class CharsetSafeMimeMessage extends org.eclipse.scout.commons.mail.CharsetSafeMimeMessage {

  public CharsetSafeMimeMessage() {
    super();
  }

  public CharsetSafeMimeMessage(String charset) {
    super(charset);
  }
}
