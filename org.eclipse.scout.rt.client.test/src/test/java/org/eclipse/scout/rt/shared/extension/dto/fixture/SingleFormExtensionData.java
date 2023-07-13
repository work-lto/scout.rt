/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.shared.extension.dto.fixture;

import java.math.BigDecimal;

import javax.annotation.Generated;

import org.eclipse.scout.rt.platform.extension.Extends;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Extends(OrigFormData.class)
@Generated(value = "org.eclipse.scout.rt.shared.extension.dto.fixture.SingleFormExtension", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SingleFormExtensionData extends AbstractFormFieldData {
  private static final long serialVersionUID = 1L;

  public SecondBigDecimal getSecondBigDecimal() {
    return getFieldByClass(SecondBigDecimal.class);
  }

  public static class SecondBigDecimal extends AbstractValueFieldData<BigDecimal> {
    private static final long serialVersionUID = 1L;
  }
}
