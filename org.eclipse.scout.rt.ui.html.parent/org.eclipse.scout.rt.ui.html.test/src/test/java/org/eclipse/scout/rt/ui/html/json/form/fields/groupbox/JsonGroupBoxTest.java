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
package org.eclipse.scout.rt.ui.html.json.form.fields.groupbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractFormField;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBox;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.eclipse.scout.rt.ui.html.json.fixtures.JsonSessionMock;
import org.eclipse.scout.rt.ui.html.json.form.fields.BaseFormFieldTest;
import org.eclipse.scout.rt.ui.html.json.form.fields.JsonFormField;
import org.eclipse.scout.rt.ui.html.json.testing.JsonTestUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(PlatformTestRunner.class)
public class JsonGroupBoxTest extends BaseFormFieldTest {
  private JsonSessionMock m_jsonSession;

  private AbstractGroupBox m_model = new AbstractGroupBox() {
  };

  private JsonGroupBox m_groupBox = new JsonGroupBox<IGroupBox>(m_model, m_session, m_session.createUniqueIdFor(null), null);

  @Before
  public void setUp() {
    m_groupBox.init();
    m_model.setBorderDecoration("x");
    m_model.setBorderVisible(true);
    m_jsonSession = new JsonSessionMock();
  }

  @Test
  public void testToJson() throws JSONException {
    JSONObject json = m_groupBox.toJson();
    assertEquals("x", json.get("borderDecoration"));
    assertEquals(Boolean.TRUE, json.get("borderVisible"));
  }

  /**
   * Tests whether non displayable fields are sent.
   * <p>
   * This limits response size and also leverages security because the fields are never visible to the user, not even
   * with the dev tools of the browser
   */
  @Test
  public void testDontSendNotDisplayableFields() throws Exception {
    IGroupBox groupBox = new GroupBoxWithNotDisplayableField();
    JsonTestUtility.initField(groupBox);

    JsonGroupBox<IGroupBox> jsonGroupBox = m_jsonSession.newJsonAdapter(groupBox, null, null);
    JsonFormField<IFormField> jsonDisplayableField = m_jsonSession.getJsonAdapter(groupBox.getFieldByClass(GroupBoxWithNotDisplayableField.DisplayableField.class), jsonGroupBox);
    JsonFormField<IFormField> jsonNotDisplayableField = m_jsonSession.getJsonAdapter(groupBox.getFieldByClass(GroupBoxWithNotDisplayableField.NotDisplayableField.class), jsonGroupBox);

    // Adapter for NotDisplayableField must not exist
    assertNull(jsonNotDisplayableField);

    // Json response must not contain NotDisplayableField
    JSONObject json = jsonGroupBox.toJson();
    JSONArray jsonFormFields = json.getJSONArray("fields");
    assertEquals(1, jsonFormFields.length());
    assertEquals(jsonDisplayableField.getId(), jsonFormFields.get(0));
  }

  private class GroupBoxWithNotDisplayableField extends AbstractGroupBox {

    @Order(10)
    public class DisplayableField extends AbstractFormField {

    }

    @Order(20)
    public class NotDisplayableField extends AbstractFormField {

      @Override
      protected void execInitField() throws ProcessingException {
        setVisibleGranted(false);
      }
    }
  }
}
