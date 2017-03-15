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

/**
 * LatexHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex;

import adams.core.Properties;
import adams.core.io.PlaceholderDirectory;
import adams.core.management.OS;
import adams.env.Environment;
import adams.env.LatexDefinition;

/**
 * Helper class for LaTeX setup.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LatexHelper {

  /** the name of the props file. */
  public final static String FILENAME = "LaTeX.props";

  /** the binaries dir. */
  public final static String BINARIES_DIR = "BinariesDir";

  /** the executable. */
  public final static String EXECUTABLE = "Executable";

  /** the bibtex executable. */
  public final static String BIBTEX = "Bibtex";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(LatexDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(LatexDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the binaries dir.
   *
   * @return		the binaries dir
   */
  public static PlaceholderDirectory getBinariesDir() {
    String	defPath;

    if (OS.isLinux())
      defPath = "/usr/bin";
    else
      defPath = ".";

    return new PlaceholderDirectory(getProperties().getPath(BINARIES_DIR, defPath));
  }

  /**
   * Returns the executable, eg pdflatex.
   *
   * @return		the executable (no path)
   */
  public static String getExecutable() {
    String 	defExec;

    if (OS.isWindows())
      defExec = "pdflatex.exe";
    else
      defExec = "pdflatex";

    return getProperties().getProperty(EXECUTABLE, defExec);
  }

  /**
   * Returns the bibtex executable.
   *
   * @return		the bibtex executable (no path)
   */
  public static String getBibtex() {
    String 	defExec;

    if (OS.isWindows())
      defExec = "bibtex.exe";
    else
      defExec = "bibtex";

    return getProperties().getProperty(EXECUTABLE, defExec);
  }
}
