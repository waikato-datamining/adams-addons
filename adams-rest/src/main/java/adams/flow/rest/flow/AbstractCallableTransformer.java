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
 * AbstractCallableTransformer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.flow;

import adams.core.MessageCollection;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.rest.AbstractRESTPluginWithFlowContext;

/**
 * Ancestor for plugins that process the data with a callable transformer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCallableTransformer<T>
  extends AbstractRESTPluginWithFlowContext
  implements CallableActorUser {

  private static final long serialVersionUID = -4960256014415499156L;

  /** the callable transformer to use for processing. */
  protected CallableActorReference m_Transformer;

  /** the actual actor. */
  protected transient Actor m_TransformerActor;

  /** whether the transformer actor has been set up. */
  protected boolean m_TransformerActorInitialized;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  protected String globalInfoBase() {
    return "Uses the specified callable transformer for processing "
      + "the incoming data and sending back the resulting data.\n"
      + "Please note, that a callable transformer is executed in synchronized "
      + "fashion and no other calls can be processed in the meantime.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "transformer", "transformer",
      new CallableActorReference());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_TransformerActor = null;
  }

  /**
   * Sets the name of the callable actor to use as transformer.
   *
   * @param value 	the callable name
   */
  public void setTransformer(CallableActorReference value) {
    m_Transformer = value;
    reset();
  }

  /**
   * Returns the name of the callable actor to use as transformer.
   *
   * @return 		the callable name
   */
  public CallableActorReference getTransformer() {
    return m_Transformer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformerTipText() {
    return "The name of the callable actor to use as transformer for processing data.";
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_TransformerActor;
  }

  /**
   * Hook method to add compatibility checks.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if compatible, otherwise error message
   */
  protected String checkCompatibility() {
    return null;
  }

  /**
   * Initializes the transformer.
   *
   * @return		null if successfully initialized
   */
  protected String initTransformer() {
    String		result;

    result = null;

    if (!m_TransformerActorInitialized) {
      m_TransformerActor = m_Helper.findCallableActorRecursive(m_FlowContext, getTransformer());
      if (m_TransformerActor == null) {
	result = "Couldn't find transformer actor '" + getTransformer() + "'!";
      }
      else {
	if (!ActorUtils.isTransformer(m_TransformerActor))
	  result = "Transformer actor '" + getTransformer() + "' is not a transformer!";
	else
	  result = checkCompatibility();
      }
      m_TransformerActorInitialized = true;
    }
    else {
      if (m_TransformerActor == null)
	result = "No transformer actor available!";
    }

    return result;
  }

  /**
   * Processes the incoming data.
   *
   * @param input	the input data
   * @param errors	for collecting errors
   * @return		the generated output, null if none generated
   */
  protected Object doProcess(Object input, MessageCollection errors) {
    Object	result;
    String	msg;

    result = null;

    msg = initTransformer();
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    synchronized(m_TransformerActor) {
      try {
	((InputConsumer) m_TransformerActor).input(new Token(input));
	msg = m_TransformerActor.execute();
	if (msg != null) {
	  errors.add("Failed to execute transformer (" + getTransformer() + "):\n" + msg);
	  return null;
	}

	if (((OutputProducer) m_TransformerActor).hasPendingOutput())
	  result = ((OutputProducer) m_TransformerActor).output().getPayload();
	if (result == null)
	  errors.add("Transformer generated no output (" + getTransformer() + ")!");
      }
      catch (Exception e) {
	errors.add("Failed to process data: " + input, e);
      }
    }

    return result;
  }
}
