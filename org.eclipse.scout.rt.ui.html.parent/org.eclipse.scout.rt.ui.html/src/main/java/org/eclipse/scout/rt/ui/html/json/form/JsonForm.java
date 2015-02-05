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
package org.eclipse.scout.rt.ui.html.json.form;

import java.util.List;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipse.scout.rt.client.ui.action.menu.root.IContextMenu;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.IForm5;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.IButton;
import org.eclipse.scout.rt.ui.html.json.AbstractJsonPropertyObserver;
import org.eclipse.scout.rt.ui.html.json.IJsonAdapter;
import org.eclipse.scout.rt.ui.html.json.IJsonSession;
import org.eclipse.scout.rt.ui.html.json.JsonEvent;
import org.eclipse.scout.rt.ui.html.json.JsonObjectUtility;
import org.eclipse.scout.rt.ui.html.json.JsonResponse;
import org.eclipse.scout.rt.ui.html.json.menu.IContextMenuOwner;
import org.eclipse.scout.rt.ui.html.json.menu.JsonContextMenu;
import org.json.JSONObject;

public class JsonForm<T extends IForm> extends AbstractJsonPropertyObserver<T> implements IContextMenuOwner {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(JsonForm.class);

  public JsonForm(T model, IJsonSession jsonSession, String id, IJsonAdapter<?> parent) {
    super(model, jsonSession, id, parent);
  }

  public static final String EVENT_FORM_CLOSING = "formClosing";
  public static final String PROP_FORM_ID = "formId";
  public static final String PROP_TITLE = IForm.PROP_TITLE;
  public static final String PROP_SUB_TITLE = IForm.PROP_SUB_TITLE;
  public static final String PROP_ICON_ID = IForm.PROP_ICON_ID;
  public static final String PROP_MINIMIZE_ENABLED = IForm.PROP_MINIMIZE_ENABLED;
  public static final String PROP_MAXIMIZE_ENABLED = IForm.PROP_MAXIMIZE_ENABLED;
  public static final String PROP_MINIMIZED = IForm.PROP_MINIMIZED;
  public static final String PROP_MAXIMIZED = IForm.PROP_MAXIMIZED;
  public static final String PROP_MODAL = "modal";
  public static final String PROP_DISPLAY_HINT = "displayHint";
  public static final String PROP_DISPLAY_VIEW_ID = "displayViewId";
  public static final String PROP_CLOSABLE = "closable";

  private FormListener m_formListener;

  @Override
  public String getObjectType() {
    return "Form";
  }

  @Override
  protected void attachChildAdapters() {
    super.attachChildAdapters();
    attachAdapter(getModel().getRootGroupBox());
    if (getModel() instanceof IForm5) {
      attachAdapter(((IForm5) getModel()).getContextMenu());
    }
  }

  @Override
  protected void attachModel() {
    super.attachModel();
    if (m_formListener != null) {
      throw new IllegalStateException();
    }
    m_formListener = new P_FormListener();
    getModel().addFormListener(m_formListener);
  }

  @Override
  protected void detachModel() {
    super.detachModel();
    if (m_formListener == null) {
      throw new IllegalStateException();
    }
    getModel().removeFormListener(m_formListener);
    m_formListener = null;
  }

  @Override
  public JSONObject toJson() {
    JSONObject json = super.toJson();
    IForm model = getModel();
    putProperty(json, PROP_TITLE, model.getTitle());
    putProperty(json, PROP_SUB_TITLE, model.getSubTitle());
    putProperty(json, PROP_ICON_ID, model.getIconId());
    putProperty(json, PROP_MAXIMIZE_ENABLED, model.isMaximizeEnabled());
    putProperty(json, PROP_MINIMIZE_ENABLED, model.isMinimizeEnabled());
    putProperty(json, PROP_MAXIMIZED, model.isMaximized());
    putProperty(json, PROP_MINIMIZED, model.isMinimized());
    putProperty(json, PROP_MODAL, model.isModal());
    putProperty(json, PROP_DISPLAY_HINT, displayHintToJson(model.getDisplayHint()));
    putProperty(json, PROP_DISPLAY_VIEW_ID, model.getDisplayViewId());
    putProperty(json, PROP_CLOSABLE, isClosable());
    putAdapterIdProperty(json, "rootGroupBox", model.getRootGroupBox());
    if (getModel() instanceof IForm5) {
      JsonContextMenu<IContextMenu> jsonContextMenu = getAdapter(((IForm5) getModel()).getContextMenu());
      if (jsonContextMenu != null) {
        JsonObjectUtility.putProperty(json, PROP_MENUS, JsonObjectUtility.adapterIdsToJson(jsonContextMenu.getJsonChildActions()));
      }
    }
    return json;
  }

  private boolean isClosable() {
    for (IFormField f : getModel().getAllFields()) {
      if (f.isEnabled() && f.isVisible() && (f instanceof IButton)) {
        switch (((IButton) f).getSystemType()) {
          case IButton.SYSTEM_TYPE_CLOSE:
          case IButton.SYSTEM_TYPE_CANCEL: {
            return true;
          }
        }
      }
    }
    return false;
  }

  protected String displayHintToJson(int displayHint) {
    switch (displayHint) {
      case IForm.DISPLAY_HINT_DIALOG:
        return "dialog";
      case IForm.DISPLAY_HINT_VIEW:
        return "view";
      case IForm.DISPLAY_HINT_POPUP_DIALOG:
        return "popupDialog";
      case IForm.DISPLAY_HINT_POPUP_WINDOW:
        return "popupWindow";
      default:
        return null;
    }
  }

  protected void handleModelFormChanged(FormEvent event) {
    switch (event.getType()) {
      case FormEvent.TYPE_CLOSED:
        handleModelFormClosed(event.getForm());
        break;
      default:
        // NOP
    }
  }

  protected void handleModelFormClosed(IForm form) {
    // FIXME CGU: what happens if isAutoAddRemoveOnDesktop = false and form removed comes after closing? maybe remove form first?
    if (ClientSyncJob.getCurrentSession().getDesktop().isShowing(form)) {
      LOG.error("Form closed but is still showing on desktop.");
      // handleModelFormRemoved(form);
    }
    dispose();
    addActionEvent("formClosed", new JSONObject());
  }

  @Override
  public void handleModelContextMenuChanged(List<IJsonAdapter<?>> menuAdapters) {
    addPropertyChangeEvent(PROP_MENUS, JsonObjectUtility.adapterIdsToJson(menuAdapters));
  }

  @Override
  public void handleUiEvent(JsonEvent event, JsonResponse res) {
    if (EVENT_FORM_CLOSING.equals(event.getType())) {
      handleUiFormClosing(event, res);
    }
  }

  public void handleUiFormClosing(JsonEvent event, JsonResponse res) {
    getModel().getUIFacade().fireFormClosingFromUI();
  }

  protected class P_FormListener implements FormListener {

    @Override
    public void formChanged(FormEvent e) throws ProcessingException {
      handleModelFormChanged(e);
    }
  }

}
