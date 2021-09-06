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
 * RedisAction.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.sink.redisaction.AbstractRedisAction;
import adams.flow.sink.redisaction.Null;
import adams.flow.standalone.RedisConnection;

/**
 * Executes the specified Redis action.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisAction
  extends AbstractRatOutput {

  /** the name of the action. */
  protected AbstractRedisAction m_Action;

  /** the redis connection to use. */
  protected transient RedisConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified Redis action.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"action", "action",
	new Null());
  }

  /**
   * Sets the name of the action.
   *
   * @param value	the name
   */
  public void setAction(AbstractRedisAction value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the name of the action.
   *
   * @return 		the name
   */
  public AbstractRedisAction getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The action to execute.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "action", m_Action, "action: ");
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return the type of data
   */
  @Override
  public Class[] accepts() {
    return m_Action.accepts();
  }

  /**
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (RedisConnection) ActorUtils.findClosestType(m_Owner, RedisConnection.class, true);
      if (m_Connection == null)
        result = "Failed to locate a " + Utils.classToString(RedisConnection.class) + "!";
    }

    return result;
  }

  /**
   * Performs the actual transmission.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String    result;

    try {
      result = m_Action.execute(m_Connection.getConnection(), "" + m_Input);
    }
    catch (Exception e) {
      result = handleException("Failed to execute action '" + m_Action + "' using: " + m_Input, e);
    }

    return result;
  }
}
