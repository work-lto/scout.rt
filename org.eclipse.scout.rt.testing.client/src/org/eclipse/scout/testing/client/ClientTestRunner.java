/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.testing.client;

import org.eclipse.scout.commons.job.ICallable;
import org.eclipse.scout.rt.testing.platform.PlatformTestRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Tests that require a session context
 *
 * @since 5.1
 */
public class ClientTestRunner extends PlatformTestRunner {

  public ClientTestRunner(Class<?> clazz) throws InitializationError {
    super(clazz);
  }

  //TODO dwi use RunWithSession
  @Override
  protected ICallable<Statement> wrapMethodInvocation(ICallable<Statement> callable, FrameworkMethod method, Object test) {
    //TODO inner wrap using session...
    callable = callable;

    return super.wrapMethodInvocation(callable, method, test);
  }
}
