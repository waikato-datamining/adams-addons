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
 * SimpleDockerCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.docker.simpledocker.DockerCommandWithProgrammaticArguments;
import adams.docker.simpledocker.StopContainers;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.SimpleDockerConnection;

/**
 <!-- globalinfo-start -->
 * Supplies the specified docker command with the incoming additional arguments and forwards the output it generates on execution.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SimpleDockerCommand
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-command &lt;adams.docker.simpledocker.DockerCommandWithProgrammaticArguments&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The docker command to run.
 * &nbsp;&nbsp;&nbsp;default: adams.docker.simpledocker.StopContainers
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleDockerCommand
  extends AbstractTransformer {

  private static final long serialVersionUID = 7628077317238673283L;

  /** the command to execute. */
  protected DockerCommandWithProgrammaticArguments m_Command;

  /** the docker connection. */
  protected transient SimpleDockerConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Supplies the specified docker command with the incoming additional arguments "
      + "and forwards the output it generates on execution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command", "command",
      new StopContainers());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "command", m_Command);
  }

  /**
   * Sets the command to run.
   *
   * @param value	the command
   */
  public void setCommand(DockerCommandWithProgrammaticArguments value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return		the command
   */
  public DockerCommandWithProgrammaticArguments getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The docker command to run.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_Command.generates()};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (SimpleDockerConnection) ActorUtils.findClosestType(this, SimpleDockerConnection.class, true);
      if (m_Connection == null)
        result = "No " + Utils.classToString(SimpleDockerConnection.class) + " actor found!";
      else if (m_Connection.getAcualBinary() == null)
        result = "No docker binary available from: " + m_Connection.getFullName();
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
    String	result;
    String[]  	args;

    result = null;
    args   = new String[0];

    if (m_InputToken.hasPayload(String.class))
      args = new String[]{m_InputToken.getPayload(String.class)};
    else if (m_InputToken.hasPayload(String[].class))
      args = m_InputToken.getPayload(String[].class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      m_Command.setFlowContext(this);
      m_Command.setConnection(m_Connection);
      m_Command.setAdditionalArguments(args);
      result = m_Command.execute();
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return the generated token
   */
  @Override
  public Token output() {
    Token	result;
    Object 	output;

    result = null;
    output = null;
    if (m_Command.hasOutput())
      output = m_Command.output();
    if (output != null)
      result = new Token(output);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br><br>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return isExecuted() && (m_Command.isRunning() || m_Command.hasOutput());
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Command.stopExecution();
    super.stopExecution();
  }
}
