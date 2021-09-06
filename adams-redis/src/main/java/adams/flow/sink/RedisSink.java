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
 * RedisSink.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.sink.redisaction.AbstractRedisAction;
import adams.flow.standalone.RedisConnection;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisSink
  extends AbstractSink {

  /** the action to execute. */
  protected AbstractRedisAction m_Action;

  /** the current connection. */
  protected transient RedisConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified action to the incoming object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "action",
      new adams.flow.sink.redisaction.Null());
  }

  /**
   * Sets the action to execute.
   *
   * @param value	the action
   */
  public void setAction(AbstractRedisAction value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to execute.
   *
   * @return 		the action
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
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Action.accepts();
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
        result = "No " + Utils.classToString(RedisConnection.class) + " actor found!";
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

    try {
      result = m_Action.execute(m_Connection.getConnection(), m_InputToken.getPayload());
    }
    catch (Exception e) {
      result = handleException("Failed to execute action: " + m_Action, e);
    }

    return result;
  }
}
