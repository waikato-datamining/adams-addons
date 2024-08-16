/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * JepUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import jep.JepConfig;
import jep.SharedInterpreter;

/**
 * Helper methods around Jep.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepUtils {

  /** the global configuration for the shared interpreters. */
  protected static JepConfig m_SharedInterpreterConfig;

  /** whether we checked for jep presence. */
  protected static Boolean m_Present;

  /**
   * Returns whether Jep is present.
   *
   * @return true if available
   */
  public static synchronized boolean isPresent() {
    if (m_Present == null) {
      try {
	try (SharedInterpreter interpreter = JepUtils.getSharedInterpreter()) {
	  interpreter.exec("import importlib.util");
	  try {
	    interpreter.exec("if (importlib.util.find_spec('jep') is None): raise Exception('jep not installed! Use: pip install jep')");
	    m_Present = true;
	  }
	  catch (Exception e) {
	    m_Present = false;
	  }
	}
      }
      catch (Exception e) {
	m_Present = false;
      }
    }
    return m_Present;
  }

  /**
   * Configures the shared interpeters.
   */
  public static synchronized void configureSharedInterpeter() {
    if (m_SharedInterpreterConfig == null) {
      m_SharedInterpreterConfig = new JepConfig();
      // ensure that Python's stdout/stderr are printed in IDE
      m_SharedInterpreterConfig.redirectStdout(System.out);
      m_SharedInterpreterConfig.redirectStdErr(System.err);
      // set global config
      SharedInterpreter.setConfig(m_SharedInterpreterConfig);
    }
  }

  /**
   * Returns the shared interpreter instance.
   *
   * @return		the instance
   */
  public static synchronized SharedInterpreter getSharedInterpreter() {
    configureSharedInterpeter();
    return new SharedInterpreter();
  }

  /**
   * Returns the Jep project URL.
   *
   * @return		the URL
   */
  public static String projectURL() {
    return "https://github.com/ninia/jep/";
  }
}
