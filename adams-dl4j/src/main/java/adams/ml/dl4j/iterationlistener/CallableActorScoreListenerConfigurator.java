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
 * CallableActorScoreListenerConfigurator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.iterationlistener;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Configures a score iteration listener that forwards the iteration&#47;score pair (as Double array) to the specified callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-optional &lt;boolean&gt; (property: optional)
 * &nbsp;&nbsp;&nbsp;If enabled, then the callable actor is optional, ie no error is raised if 
 * &nbsp;&nbsp;&nbsp;not found, merely ignored.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-frequency &lt;int&gt; (property: frequency)
 * &nbsp;&nbsp;&nbsp;The update frequency.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActorScoreListenerConfigurator
  extends AbstractIteratorListenerConfigurator {

  private static final long serialVersionUID = -3325744412079265328L;

  /** the frequency. */
  protected int m_Frequency;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** whether the callable actor is optional. */
  protected boolean m_Optional;

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
      "Configures a score iteration listener that forwards the iteration/score "
	+ "pair (as Double array) to the specified callable actor.";
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

    m_OptionManager.add(
      "optional", "optional",
      false);

    m_OptionManager.add(
      "frequency", "frequency",
      10, 1, null);
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
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
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
    return "The name of the callable actor to use.";
  }

  /**
   * Sets whether the callable actor is optional.
   *
   * @param value 	true if optional
   */
  public void setOptional(boolean value) {
    m_Optional = value;
    reset();
  }

  /**
   * Returns whether the callable actor is optional.
   *
   * @return 		true if optional
   */
  public boolean getOptional() {
    return m_Optional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionalTipText() {
    return
	"If enabled, then the callable actor is optional, ie no error is "
	+ "raised if not found, merely ignored.";
  }

  /**
   * Sets the update frequency.
   *
   * @param value	the frequency
   */
  public void setFrequency(int value) {
    if (getOptionManager().isValid("frequency", value)) {
      m_Frequency = value;
      reset();
    }
  }

  /**
   * Returns the update frequency.
   *
   * @return 		the frequency
   */
  public int getFrequency() {
    return m_Frequency;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String frequencyTipText() {
    return "The update frequency.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "callableName", m_CallableName);
    result += QuickInfoHelper.toString(this, "optional", m_Optional, "optional", ", ");
    result += QuickInfoHelper.toString(this, "frequency", m_Frequency, ", frequency: ");

    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(m_FlowContext, getCallableName());
  }

  /**
   * Hook method before configuring the listener.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      m_CallableActor = findCallableActor();
      if ((m_CallableActor == null) && !m_Optional)
	result = "Callable actor not found: " + m_CallableName;
      else if ((m_CallableActor != null) && !(m_CallableActor instanceof InputConsumer))
	result = "Callable actor does not accept input: " + m_CallableActor;
    }

    return result;
  }

  /**
   * Configures the actual {@link IterationListener} and returns it.
   *
   * @return		the listeners
   */
  @Override
  protected List<IterationListener> doConfigureIterationListeners() {
    IterationListener		listener;

    listener = new IterationListener() {
      private static final long serialVersionUID = -6077509026351995338L;
      private int frequency = m_Frequency;
      private boolean invoked = false;
      private long iterCount = 0;

      @Override
      public boolean invoked() {
	return invoked;
      }

      @Override
      public void invoke() {
	invoked = true;
      }

      @Override
      public void iterationDone(Model model, int iteration) {
	if (frequency <= 0)
	  frequency = 1;
	if (iterCount % frequency == 0) {
	  invoke();
	  double result = model.score();
	  if (m_CallableActor != null) {
	    synchronized (m_CallableActor) {
	      ((InputConsumer) m_CallableActor).input(new Token(new Double[]{(double) iterCount, result}));
	      m_CallableActor.execute();
	    }
	  }
	  else {
	    m_FlowContext.getLogger().info("Iteration " + iterCount + ". score=" + result);
	  }
	}
	iterCount++;
      }
    };

    return new ArrayList<>(Arrays.asList(listener));
  }
}
