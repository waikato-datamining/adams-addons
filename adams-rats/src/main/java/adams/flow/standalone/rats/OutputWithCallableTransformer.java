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
 * OutputWithCallableTransformer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.QuickInfoHelper;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Meta-transmitter that passes the data through the callable transformer before forwarding it to the base-transmitter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.flow.standalone.rats.RatOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The transmitter to wrap.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyOutput
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable transformer to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutputWithCallableTransformer
  extends AbstractMetaRatOutput
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = -4073060833120998241L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected AbstractActor m_CallableActor;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Meta-transmitter that passes the data through the callable "
	+ "transformer before forwarding it to the base-transmitter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference("unknown"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
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
   * Sets the name of the callable transformer to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable transformer in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable transformer to use.";
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public AbstractActor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Tries to find the callable actor referenced by its global name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    AbstractActor	result;
    
    result = m_Helper.findCallableActorRecursive(getOwner(), getCallableName());
    if (!(ActorUtils.isTransformer(result))) {
      getLogger().severe("Callable actor '" + result.getFullName() + "' is not a transformer" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName()));
      result = null;
    }
    
    return result;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "callableName", m_CallableName);
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    if (m_CallableActor != null)
      return ((InputConsumer) m_CallableActor).accepts();
    else
      return new Class[]{Unknown.class};
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
      m_CallableActor = findCallableActor();
      if (m_CallableActor == null)
	result = "Failed to locate callable transformer '" + m_CallableName + "', check log!";
    }
    
    return result;
  }

  /**
   * Hook method before calling the base-output's transmit() method.
   * <p/>
   * Passes the input data through the callable transformer before 
   * passing it on to the base-transmitter.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preTransmit() {
    String	result;
    Token	input;
    Token	output;

    result = super.preTransmit();
    if (result == null) {
      if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped()) {
	if (isLoggingEnabled())
	  getLogger().info("Passing data through '" + m_CallableName + "'");
	input   = new Token(m_Input);
	m_Input = null;
	synchronized(m_CallableActor) {
	  ((InputConsumer) m_CallableActor).input(input);
	  result = m_CallableActor.execute();
	  if (result == null) {
	    if (((OutputProducer) m_CallableActor).hasPendingOutput()) {
	      output = ((OutputProducer) m_CallableActor).output();
	      if (output.getPayload() != null)
		m_Input = output.getPayload();
	      if (((OutputProducer) m_CallableActor).hasPendingOutput())
		getLogger().warning("Only retrieved first output token!");
	    }
	  }
	}
	if (isLoggingEnabled())
	  getLogger().info("Passed data through '" + m_CallableName + "'");
      }
    }

    return result;
  }
}
