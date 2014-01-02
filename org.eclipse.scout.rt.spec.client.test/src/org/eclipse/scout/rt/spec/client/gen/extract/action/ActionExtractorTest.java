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
package org.eclipse.scout.rt.spec.client.gen.extract.action;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.scout.rt.client.ui.action.AbstractAction;
import org.eclipse.scout.rt.client.ui.action.IAction;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.spec.client.gen.extract.AbstractBooleanTextExtractor;
import org.eclipse.scout.rt.spec.client.gen.extract.IDocTextExtractor;
import org.junit.Test;

/**
 * Tests for default column extractor: {@link IDocTextExtractor}< {@link org.eclipse.scout.rt.client.ui.action.IAction
 * IAction}>.
 */
public class ActionExtractorTest {

  /**
   * Tests that {@link SingleSelectionExtractor#getText(AbstractAction)} return the true text
   * {@link AbstractBooleanTextExtractor.DOC_ID_TRUE}, if single selection is enabled.
   */
  @Test
  public void testSingleSelectionExtractor() {
    SingleSelectionExtractor<AbstractAction> ex = new SingleSelectionExtractor<AbstractAction>();
    AbstractAction testAction = mock(AbstractAction.class);
    when(testAction.isSingleSelectionAction()).thenReturn(true);
    String trueText = TEXTS.get(AbstractBooleanTextExtractor.DOC_ID_TRUE);
    String text = ex.getText(testAction);
    assertEquals(trueText, text);
  }

  /**
   * Tests that {@link SingleSelectionExtractor#getText(AbstractAction)} for
   * the default case returns the text for {@link AbstractBooleanTextExtractor#DOC_ID_FALSE}
   */
  @Test
  public void testSingleSelectionExtractorDefault() {
    assertFalseTextForDefault(new SingleSelectionExtractor<AbstractAction>());
  }

  /**
   * Tests that {@link MultiSelectionExtractor#getText(AbstractAction)} return the true text
   * {@link AbstractBooleanTextExtractor.DOC_ID_TRUE}, if single selection is enabled.
   */
  @Test
  public void testMultiSelectionExtractor() {
    MultiSelectionExtractor<AbstractAction> ex = new MultiSelectionExtractor<AbstractAction>();
    AbstractAction testAction = mock(AbstractAction.class);
    when(testAction.isMultiSelectionAction()).thenReturn(true);
    String trueText = TEXTS.get(AbstractBooleanTextExtractor.DOC_ID_TRUE);
    String text = ex.getText(testAction);
    assertEquals(trueText, text);
  }

  /**
   * Tests that {@link MultiSelectionExtractor#getText(AbstractAction)} for
   * the default case returns the text for {@link AbstractBooleanTextExtractor#DOC_ID_FALSE}
   */
  @Test
  public void testMultiSelectionExtractorDefault() {
    assertFalseTextForDefault(new MultiSelectionExtractor<AbstractAction>());
  }

  /**
   * Tests that {@link EmptySpaceSelectionExtractor#getText(AbstractAction)} return the true text
   * {@link AbstractBooleanTextExtractor.DOC_ID_TRUE}, if single selection is enabled.
   */
  @Test
  public void testEmptySpaceSelectionExtractor() {
    EmptySpaceSelectionExtractor<AbstractAction> ex = new EmptySpaceSelectionExtractor<AbstractAction>();
    AbstractAction testAction = mock(AbstractAction.class);
    when(testAction.isEmptySpaceAction()).thenReturn(true);
    String trueText = TEXTS.get(AbstractBooleanTextExtractor.DOC_ID_TRUE);
    String text = ex.getText(testAction);
    assertEquals(trueText, text);
  }

  /**
   * Tests that {@link EmptySpaceSelectionExtractor#getText(AbstractAction)} for
   * the default case returns the text for {@link AbstractBooleanTextExtractor#DOC_ID_FALSE}
   */
  @Test
  public void testEmptySpaceSelectionExtractorDefault() {
    assertFalseTextForDefault(new EmptySpaceSelectionExtractor<AbstractAction>());
  }

  /**
   * Tests that {@link EmptySpaceSelectionExtractor#getText(AbstractAction)} return the true text
   * {@link AbstractBooleanTextExtractor.DOC_ID_TRUE}, if single selection is enabled.
   */
  @Test
  public void testActionPropertyExtractor() {
    final String testText = "TEST_TEXT";
    ActionPropertyExtractor<AbstractAction> ex = new ActionPropertyExtractor<AbstractAction>(IAction.PROP_TEXT, "TEST_HEADER");
    AbstractAction testAction = mock(AbstractAction.class);
    when(testAction.getProperty(IAction.PROP_TEXT)).thenReturn(testText);
    String text = ex.getText(testAction);
    assertEquals(testText, text);
  }

  /**
   * Asserts that the default text for false is extracted, for a default action
   * 
   * @param ex
   *          {@link IDocTextExtractor}
   */
  private void assertFalseTextForDefault(IDocTextExtractor<AbstractAction> ex) {
    String defaultValue = TEXTS.get(AbstractBooleanTextExtractor.DOC_ID_FALSE);
    AbstractAction a = mock(AbstractAction.class);
    String text = ex.getText(a);
    assertEquals(defaultValue, text);
  }

}
