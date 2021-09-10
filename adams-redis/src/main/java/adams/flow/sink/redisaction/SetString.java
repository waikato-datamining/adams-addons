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
 * SetString.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.redisaction;

import adams.core.QuickInfoHelper;
import adams.flow.core.Unknown;
import redis.clients.jedis.Jedis;

/**
 * Sets the incoming string under the specified key.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SetString
  extends AbstractRedisAction {

  /** the key to use for storing the object. */
  protected String m_Key;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the incoming string under the specified key.";
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
  public String keyTipText() {
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
   * Returns the classes the action accepts as input.
   *
   * @return the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the action on the specified object.
   *
   * @param connection the Redis connection
   * @param o          the object to process
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doExecute(Jedis connection, Object o) {
    connection.set(m_Key, "" + o);
    return null;
  }
}
