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
 * OutputWithExternalTransformer.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FlowFile;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Meta-transmitter that passes the data through the external transformer before forwarding it to the base-transmitter.
 * <br><br>
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
 * <pre>-file &lt;adams.core.io.FlowFile&gt; (property: actorFile)
 * &nbsp;&nbsp;&nbsp;The file containing the external transformer.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutputWithExternalTransformer
  extends AbstractMetaRatOutput
  implements ExternalActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4073060833120998241L;

  /** the file the external actor is stored in. */
  protected FlowFile m_ActorFile;

  /** the external actor itself. */
  protected Actor m_ExternalActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Meta-transmitter that passes the data through the external "
	+ "transformer before forwarding it to the base-transmitter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "actorFile",
	    new FlowFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "actorFile", m_ActorFile);
  }

  /**
   * Sets the file containing the external actor.
   *
   * @param value	the actor file
   */
  public void setActorFile(FlowFile value) {
    m_ActorFile = value;
    reset();
  }

  /**
   * Returns the file containing the external actor.
   *
   * @return		the actor file
   */
  public FlowFile getActorFile() {
    return m_ActorFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorFileTipText() {
    return "The file containing the external transformer.";
  }

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String		result;
    List<String>	errors;
    String		warning;

    result = null;

    if (!m_ActorFile.isFile()) {
      result = "'" + m_ActorFile.getAbsolutePath() + "' does not point to a file!";
    }
    else {
      errors = new ArrayList<String>();
      m_ExternalActor = ActorUtils.read(m_ActorFile.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
      }
      else if (m_ExternalActor == null) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "'!";
      }
      else {
	m_ExternalActor.setParent(getOwner());
	m_ExternalActor.setVariables(getOwner().getVariables());
	result = m_ExternalActor.setUp();
	// make sure we've got the current state of the variables
	if (result == null) {
	  warning = m_ExternalActor.getOptionManager().updateVariableValues(true);
	  if (warning != null)
	    getLogger().severe(
		"Updating variables ('" + getFullName() + "'/'" + m_ActorFile + "') resulted in the following error output "
	    + "(which gets ignored since variables might get initialized later on):\n" + warning);
	}
      }
    }

    return result;
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    if (m_ExternalActor != null)
      return ((InputConsumer) m_ExternalActor).accepts();
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
    
    if (result == null)
      result = setUpExternalActor();
    
    return result;
  }
  
  /**
   * Returns the internal actor.
   *
   * @return		the actor, null if not available
   */
  @Override
  public Actor getExternalActor() {
    return m_ExternalActor;
  }

  /**
   * Hook method before calling the base-output's transmit() method.
   * <br><br>
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
      if (!m_ExternalActor.getSkip() && !m_ExternalActor.isStopped()) {
	if (isLoggingEnabled())
	  getLogger().info("Passing data through '" + m_ActorFile + "'");
	input   = new Token(m_Input);
	m_Input = null;
	((InputConsumer) m_ExternalActor).input(input);
	result = m_ExternalActor.execute();
	if (result == null) {
	  if (((OutputProducer) m_ExternalActor).hasPendingOutput()) {
	    output = ((OutputProducer) m_ExternalActor).output();
	    if (output.getPayload() != null)
	      m_Input = output.getPayload();
	    if (((OutputProducer) m_ExternalActor).hasPendingOutput())
	      getLogger().warning("Only retrieved first output token!");
	  }
	}
	if (isLoggingEnabled())
	  getLogger().info("Passed data through '" + m_ActorFile + "'");
      }
    }

    return result;
  }

  /**
   * Cleans up the external actor.
   */
  public void cleanUpExternalActor() {
    if (m_ExternalActor != null) {
      m_ExternalActor.wrapUp();
      m_ExternalActor.cleanUp();
      m_ExternalActor = null;
    }
  }
}
