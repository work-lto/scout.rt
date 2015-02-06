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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

/**
 * Utility to extract config.ini entries of the formats
 * <p>
 * <li>classOrInterfaceName#propertyName=value</li>
 * <li>classOrInterfaceName/filter#propertyName=value</li>
 * <p>
 * Example entries
 * <li>com.myapp.MyService#realm=MyRealm</li>
 * <li>realm=MyRealm2</li>
 * <li>com.myapp.MyService#realm=${realm}</li>
 * <li>com.myapp.MyService/process#realm=ExecRealm</li>
 * <li>com.myapp.MyService/query#realm=QueryRealm</li>
 * <p>
 * Supports for config.ini files in an external area
 *
 * <pre>
 * <code>
 * external.configuration.area=[url | file | ${variable}]
 * external.configuration.area.0=[url | file | ${variable}]
 * external.configuration.area.1=[url | file | ${variable}]
 * external.configuration.area.2=[url | file | ${variable}]
 * ...
 * </code>
 * </pre>
 *
 * where ${variable} is a system property or an environment variable containing an URL or a file path to the FOLDER
 * containing the config.ini
 */
public final class ConfigIniUtility {
  public static final String CONFIG_INI = "config.ini";
  public static final char FILTER_DELIM = '/';

  private static final Pattern CONFIG_LINE_PATTERN = Pattern.compile("([^#" + FILTER_DELIM + "]+)(" + FILTER_DELIM + "[^#]*)?\\#([^=]+)");
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");
  private static final Map<String, String> configProperties;

  private ConfigIniUtility() {
  }

  static {
    configProperties = new HashMap<>();

    Set<String> externalConfigPaths = new LinkedHashSet<>();
    parseLocalConfigIniFile(externalConfigPaths);
    parseExternalConfigIniFiles(externalConfigPaths);
    resolveAll();
  }

  public static Map<String, String> getProperties(Class beanType) {
    return getProperties(beanType, null);
  }

  public static Map<String, String> getProperties(Class beanType, String filter) {
    Map<String, String> props = new HashMap<>();
    for (Entry<String, String> entry : configProperties.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (value != null && value.length() > 0) {
        String classProperty = getClassProperty(key, beanType, filter);
        if (classProperty != null && classProperty.length() > 0) {
          // filter accepted
          String propName = key.substring(key.length() - classProperty.length());
          props.put(propName, value);
        }
      }
    }
    return props;
  }

  public static boolean getBooleanProperty(String key, boolean def) {
    String val = getProperty(key);
    if (val == null) {
      return def;
    }
    return Boolean.parseBoolean(val);
  }

  public static String getProperty(String key) {
    if (key == null) {
      return null;
    }

    // 1. App config (config.ini)
    String value = configProperties.get(key);
    if (value != null) {
      return value;
    }

    // 2. system config
    value = System.getProperty(key);
    if (value != null) {
      return value;
    }

    // 3. environment config
    return System.getenv(key);
  }

  public static String resolve(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    String t = s;
    Matcher m = VARIABLE_PATTERN.matcher(t);
    while (m.find()) {
      String key = m.group(1);

      String value = getProperty(key);
      try {
        // in case it is an URL: make absolute file path
        value = new File(new URL(value).getFile()).getAbsolutePath();
      }
      catch (Exception e) {
      }

      if (value == null) {
        throw new IllegalArgumentException("resolving expression \"" + s + "\": variable ${" + key + "} is not defined in the context");
      }
      t = t.substring(0, m.start()) + value + t.substring(m.end());
      // next
      m = VARIABLE_PATTERN.matcher(t);
    }

    t = t.replace("@user.home", getProperty("user.home"));
    t = t.replace("@user.dir", getProperty("user.dir"));
    return t;
  }

  protected static String getClassProperty(String key, Class beanType, String filter) {
    Matcher m = CONFIG_LINE_PATTERN.matcher(key);
    if (m.matches()) {
      String clazz = m.group(1);
      String f = m.group(2);
      String prop = m.group(3);

      if (f != null && (f.length() < 1 || f.charAt(0) != FILTER_DELIM)) {
        f = null;
      }

      try {
        if (filter == null || f == null || filter.equals(f)) {
          Class<?> myType = Class.forName(clazz, false, beanType.getClassLoader());
          if (myType.isAssignableFrom(beanType)) {
            return prop;
          }
        }
      }
      catch (Exception e) {
        //nop
      }
    }
    return null;
  }

  protected static void parseLocalConfigIniFile(Set<String> externalConfigPaths) {
    URL url = null;
    try {
      if (Platform.isRunning()) {
        if (Platform.inDevelopmentMode()) {
          url = new URL(Platform.getConfigurationLocation().getURL(), CONFIG_INI);
        }
        else {
          URL installLocationUrl = Platform.getInstallLocation().getURL();
          url = new URL(installLocationUrl, "configuration/" + CONFIG_INI);
        }
      }
    }
    catch (MalformedURLException e) {
      // Do not use logger here. Because the logger uses this class, it is not yet initialized
      e.printStackTrace();
    }

    if (url != null) {
      parseConfigIni(url, externalConfigPaths);
    }
  }

  protected static void parseExternalConfigIniFiles(Set<String> externalConfigPaths) {
    for (String path : externalConfigPaths) {
      URL url = getExternalConfigIniUrl(path);
      if (url != null) {
        parseConfigIni(url, null /* no second level of external files*/);
      }
    }
  }

  protected static void resolveAll() {
    for (Entry<String, String> entry : configProperties.entrySet()) {
      entry.setValue(resolve(entry.getValue()));
    }
  }

  protected static void parseConfigIni(URL configIniUrl, Set<String> externalConfigPaths) {
    Properties props = new Properties();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(configIniUrl.openStream(), "UTF-8"))) {
      props.load(in);
    }
    catch (Exception t) {
      // Do not use logger here. Because the logger uses this class, it is not yet initialized
      t.printStackTrace();
      return;
    }

    if (externalConfigPaths != null) {
      Object sharedConfigArea = props.remove("osgi.sharedConfiguration.area"); // legacy
      if (sharedConfigArea != null) {
        externalConfigPaths.add(sharedConfigArea.toString());
      }
    }

    Pattern externalConfigFilePattern = Pattern.compile("external\\.configuration\\.area(\\.[0-9]+)?");
    for (Entry<Object, Object> entry : props.entrySet()) {
      String key = (String) entry.getKey();
      String value = (String) entry.getValue();
      if (key != null && value != null) {
        if (externalConfigFilePattern.matcher(key).matches()) {
          if (externalConfigPaths != null) {
            externalConfigPaths.add(value);
          }
        }
        else {
          configProperties.put(key, value);
        }
      }
    }

    if (externalConfigPaths != null) {
      String furtherExternalConfigFile = System.getProperty("external.configuration.file");
      if (furtherExternalConfigFile != null) {
        externalConfigPaths.add(furtherExternalConfigFile);
      }
    }
  }

  protected static URL getExternalConfigIniUrl(String externalPath) {
    URL url = null;
    String resolvedPath = resolve(externalPath.replaceAll("\\\\(.)", "$1"));
    File f = new File(resolvedPath);

    try {
      if (f.exists()) {
        if (f.isFile()) {
          url = f.toURI().toURL();
        }
        else {
          url = new File(resolvedPath, CONFIG_INI).toURI().toURL();
        }
      }
      else {
        if (resolvedPath.toLowerCase().endsWith(".ini") || resolvedPath.toLowerCase().endsWith(".properties")) {
          url = new URL(resolvedPath);
        }
        else {
          url = new URL(new URL(resolvedPath), CONFIG_INI);
        }
      }
    }
    catch (Exception e) {
      // Do not use logger here. Because the logger uses this class, it is not yet initialized
      e.printStackTrace();
    }
    return url;
  }
}
