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
 * CNTK.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.ml.cntk;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;

/**
 * Helper class for fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTK {

  /** the name of the props file. */
  public final static String FILENAME = "adams/ml/cntk/CNTK.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Initializes the properties if necessary.
   */
  protected static synchronized void initializeProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }
  }

  /**
   * Returns the properties in use.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    initializeProperties();
    return m_Properties;
  }

  /**
   * Returns the system wide Monospaced font.
   *
   * @return		the font
   */
  public static PlaceholderFile getBinary() {
    return new PlaceholderFile(getProperties().getPath("Binary", "."));
  }
}
