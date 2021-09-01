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
 * RedisSet.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.Unknown;
import adams.flow.standalone.RedisConnection;

/**
 * TODO: What this class does.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisSet
    extends AbstractSink {

  /** the key to use for storing the object. */
  protected String m_Key;

  /** the current connection. */
  protected transient RedisConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores the incoming object under the specified key.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "key", "key",
        "");
  }

  /**
   * Sets the name of the Key.
   *
   * @param value	the name
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the name of the Key.
   *
   * @return 		the name
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String KeyTipText() {
    return "The name of the Key.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "key", (m_Key.isEmpty() ? "-empty-" : m_Key), "key: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, byte[].class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (RedisConnection) ActorUtils.findClosestType(this, RedisConnection.class);
      if (m_Connection == null)
        result = "No " + RedisConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String      result;
    String      str;
    byte[]      bytes;

    result = null;

    try {
      if (m_InputToken.hasPayload(String.class))
        m_Connection.getConnection().set(m_Key, (String) m_InputToken.getPayload());
      else
        m_Connection.getConnection().set(m_Key, (byte[]) m_InputToken.getPayload());
    }
    catch (Exception e) {
      result = handleException("Failed to set value for key: " + m_Key, e);
    }

    return null;
  }
}
