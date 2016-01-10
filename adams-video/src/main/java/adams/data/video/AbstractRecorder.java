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
 * AbstractRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import adams.core.option.AbstractOptionHandler;
import com.github.fracpete.screencast4j.record.Recorder;

/**
 * Ancestor for {@link com.github.fracpete.screencast4j.record.Recorder} wrappers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRecorder
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 448567829871179417L;

  /**
   * Returns a fully configured recorder instance.
   *
   * @return		the new instance
   */
  protected abstract Recorder doConfigure();

  /**
   * Returns the configured recorder instance.
   *
   * @return		the recorder
   * @throws Exception	if {@link Recorder#setUp()} fails
   */
  public Recorder configure() throws Exception {
    Recorder	result;
    String	msg;

    result = doConfigure();
    msg    = result.setUp();
    if (msg != null)
      throw new Exception("Failed to configure " + result.getClass().getName() + ": " + msg);

    return result;
  }
}
