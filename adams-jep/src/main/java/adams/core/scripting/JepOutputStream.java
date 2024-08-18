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
 * JepOutputStream.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.logging.LoggingLevel;
import adams.gui.core.ConsolePanel.ConsolePanelOutputStream;
import adams.gui.core.GUIHelper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * For redirecting output from the Python stdout/stderr to Java.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepOutputStream
  extends OutputStream {

  /** whether stdout or stderr. */
  protected boolean m_StdOut;

  /** the output stream for the console panel. */
  protected ConsolePanelOutputStream m_Console;

  /**
   * Initializes the output stream.
   *
   * @param stdout	whether this is for stdout or stderr
   */
  public JepOutputStream(boolean stdout) {
    m_StdOut  = stdout;
    m_Console = null;
    if (!GUIHelper.isHeadless())
      m_Console = new ConsolePanelOutputStream(stdout ? LoggingLevel.INFO : LoggingLevel.SEVERE);
  }

  /**
   * Returns whether for stdout or stderr.
   *
   * @return		true if stdout
   */
  public boolean isStdOut() {
    return m_StdOut;
  }

  /**
   * Writes the specified byte to this output stream. The general
   * contract for {@code write} is that one byte is written
   * to the output stream. The byte to be written is the eight
   * low-order bits of the argument {@code b}. The 24
   * high-order bits of {@code b} are ignored.
   *
   * @param b the {@code byte}.
   * @throws IOException if an I/O error occurs. In particular,
   *                     an {@code IOException} may be thrown if the
   *                     output stream has been closed.
   */
  @Override
  public void write(int b) throws IOException {
    if (m_Console != null)
      m_Console.write(b);
    if (m_StdOut)
      System.out.write(b);
    else
      System.err.write(b);
  }
}
