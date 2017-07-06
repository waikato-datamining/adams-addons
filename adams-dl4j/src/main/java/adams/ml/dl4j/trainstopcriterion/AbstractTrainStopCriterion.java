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
 * AbstractTrainStopCriterion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for schemes that check whether training should be stopped.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrainStopCriterion
  extends AbstractOptionHandler
  implements FlowContextHandler, QuickInfoSupporter {

  private static final long serialVersionUID = 3803261952968636827L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the (optional) name of the criterion. */
  protected String m_Name;

  /**
   * Initializes the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    start();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "name", "name",
      "");
  }

  /**
   * Sets the name for the criterion, which is used in the trigger messages.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name for the criterion, which is used in the trigger messages.
   *
   * @return  		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return
      "The (optional) name for the criterion, which is used in the trigger "
	+ "messages; can be used to create unique name.";
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  public abstract boolean requiresFlowContext();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * For initializing the scheme.
   */
  public void start() {
  }

  /**
   * Hook method for checks.
   *
   * @param cont	the container to use
   * @return		null if successful, otherwise error message
   */
  protected String check(DL4JModelContainer cont) {
    if (requiresFlowContext()) {
      if (m_FlowContext == null)
	return "No flow context set!";
    }
    return null;
  }

  /**
   * Gets called if the criterion triggered and adds a message to the
   * trigger message collection, using either the supplied name or
   * 'unnamed'.
   *
   * @param triggers	the message collection to add the message to
   */
  protected void addTriggerMessage(MessageCollection triggers) {
    String	name;

    name = (m_Name.isEmpty() ? "unnamed" : m_Name);
    triggers.add(name + ": " + toCommandLine());
  }

  /**
   * Performs the actual checking for stopping the training.
   *
   * @param cont	the container to use for stopping
   * @param triggers	for storing trigger messages
   * @return		true if to stop training
   */
  protected abstract boolean doCheckStopping(DL4JModelContainer cont, MessageCollection triggers);

  /**
   * Checks the stopping.
   *
   * @param cont	the container to use for stopping
   * @param triggers	for storing trigger messages
   * @return		true if to stop training
   */
  public boolean checkStopping(DL4JModelContainer cont, MessageCollection triggers) {
    boolean	result;
    String	msg;

    msg = check(cont);
    if (msg != null) {
      getLogger().warning(msg);
      return false;
    }

    result = doCheckStopping(cont, triggers);
    if (result)
      addTriggerMessage(triggers);

    return result;
  }
}
