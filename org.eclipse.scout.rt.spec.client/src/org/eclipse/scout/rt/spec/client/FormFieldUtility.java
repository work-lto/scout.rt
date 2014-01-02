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
package org.eclipse.scout.rt.spec.client;

import org.eclipse.scout.rt.client.ui.form.fields.ICompositeField;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;

/**
 * Utilities for {@link IFormField}
 */
public class FormFieldUtility {

  private FormFieldUtility() {
  }

  /**
   * @param formField
   * @return
   */
  public static String getUniqueFieldId(IFormField formField) {
    //TODO move to form https://bugs.eclipse.org/bugs/show_bug.cgi?id=420626
    StringBuilder uniqueId = new StringBuilder();
    uniqueId.append(formField.getClass().getName());
    for (ICompositeField enclosingField : formField.getEnclosingFieldList()) {
      uniqueId.append("|").append(enclosingField.getClass().getName());
    }
    return uniqueId.toString();
  }

}
