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
 * LogEntryGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.log;

import adams.core.Properties;
import adams.db.LogEntry;
import adams.flow.core.Actor;
import adams.flow.standalone.Rat;

import java.util.Date;

/**
 * Generates simple LogEntry objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogEntryGenerator
  extends AbstractLogObjectGenerator<LogEntry> {

  private static final long serialVersionUID = -4921614768142682470L;

  /** the key to use for the error. */
  protected String m_ErrorKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates simple LogEntry objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "error-key", "errorKey",
      "Message");
  }

  /**
   * Sets the key to use for the error in the message.
   *
   * @param value	the key
   */
  public void setErrorKey(String value) {
    m_ErrorKey = value;
    reset();
  }

  /**
   * Returns the key to use for the error in the message.
   *
   * @return		the key
   */
  public String getErrorKey() {
    return m_ErrorKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorKeyTipText() {
    return "The key to use for the error in the message.";
  }

  /**
   * Handles the given error message.
   *
   * @param rat		the rat that captured this error
   * @param source	the source of the error
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		the generated log container
   */
  @Override
  public LogEntry generate(Rat rat, Actor source, String type, String msg) {
    LogEntry 		result;
    Properties		props;

    props  = new Properties();
    props.setProperty(m_ErrorKey, msg);
    result = new LogEntry();
    result.setGeneration(new Date());
    result.setSource(rat.getFullName());
    result.setType(type);
    result.setStatus(LogEntry.STATUS_NEW);
    result.setMessage(props);

    return result;
  }
}
