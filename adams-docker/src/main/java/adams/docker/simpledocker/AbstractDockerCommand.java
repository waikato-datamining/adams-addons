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
 * AbstractDockerCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.management.CommandResult;
import adams.core.option.AbstractOptionHandler;
import adams.docker.simpledocker.stderrprocessing.AbstractStdErrProcessing;
import adams.docker.simpledocker.stderrprocessing.Log;
import adams.flow.core.Actor;
import adams.flow.standalone.SimpleDockerConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for docker commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDockerCommand
  extends AbstractOptionHandler
  implements DockerCommand {

  private static final long serialVersionUID = -3060945925413859934L;

  /** the handler for processing output on stderr. */
  protected AbstractStdErrProcessing m_StdErrProcessing;

  /** the docker connection. */
  protected transient SimpleDockerConnection m_Connection;

  /** the command was executed. */
  protected boolean m_Executed;

  /** whether the command is still running. */
  protected boolean m_Running;

  /** whether the execution was stopped. */
  protected boolean m_Stopped;

  /** for buffering output. */
  protected List m_Output;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stderr-processing", "stdErrProcessing",
      getDefaultStdErrProcessing());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Output = new ArrayList();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Output.clear();
  }

  /**
   * Returns the default handler for processing output on stderr.
   *
   * @return		the handler
   */
  protected AbstractStdErrProcessing getDefaultStdErrProcessing() {
    return new Log();
  }

  /**
   * Sets the handler for processing the output received on stderr.
   *
   * @param value	the handler
   */
  public void setStdErrProcessing(AbstractStdErrProcessing value) {
    m_StdErrProcessing = value;
    reset();
  }

  /**
   * Returns the handler for processing the output received on stderr.
   *
   * @return		the handler
   */
  public AbstractStdErrProcessing getStdErrProcessing() {
    return m_StdErrProcessing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stdErrProcessingTipText() {
    return "The handler for processing output received from the docker command on stderr.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Sets the docker connection to use.
   *
   * @param value	the connection
   */
  @Override
  public void setConnection(SimpleDockerConnection value) {
    m_Connection = value;
  }

  /**
   * Returns the docker connection in use.
   *
   * @return		the connection, null if none set
   */
  @Override
  public SimpleDockerConnection getConnection() {
    return m_Connection;
  }

  /**
   * Hook method for performing checks before executing the command.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    String	result;

    result = null;

    if (m_FlowContext == null)
      result = "No flow context set!";

    if (result == null) {
      if (m_Connection == null)
	result = "No docker connection available! Missing " + Utils.classToString(SimpleDockerConnection.class) + " standalone?";
    }

    if (result == null)
      result = m_StdErrProcessing.setUp(this);

    return result;
  }

  /**
   * Logs the command for execution.
   *
   * @param cmd		the command
   */
  protected void log(String[] cmd) {
    log(Arrays.asList(cmd));
  }

  /**
   * Logs the command for execution.
   *
   * @param cmd		the command
   */
  protected void log(List<String> cmd) {
    if (isLoggingEnabled())
      getLogger().info("Command: " + Utils.flatten(cmd, " "));
  }

  /**
   * For logging the result of a command.
   *
   * @param res		the result to log
   */
  protected void log(CommandResult res) {
    if (isLoggingEnabled()) {
      getLogger().info("Command: " + Utils.flatten(res.command, " "));
      getLogger().info("Exit code: " + res.exitCode);
      if (res.stdout != null)
	getLogger().info("Stdout: " + res.stdout);
      if (res.stderr != null)
	getLogger().info("Stderr: " + res.stderr);
    }
  }

  /**
   * Generates an error message from the command result.
   *
   * @param res		the result to turn into an error message
   * @return		the error message
   */
  protected String commandResultToError(CommandResult res) {
    String	result;

    result = "Failed to execute: " + Utils.flatten(res.command, " ") + "\n"
      + "Exit code: " + res.exitCode;
    if (res.stdout != null)
      result += "\nStdout: " + res.stdout;
    if (res.stderr != null)
      result += "\nStderr: " + res.stderr;

    return result;
  }

  /**
   * Executes the command in asynchronous fashion.
   * Async commands must set the {@link #m_Running} flag to false themselves.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  protected Object doAsyncExecute() {
    return null;
  }

  /**
   * Executes the command in blocking fashion (ie waits till it finishes).
   * The {@link #m_Running} flag is set to false automatically.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  protected Object doBlockingExecute() {
    return null;
  }

  /**
   * For post-processing the output (async mode).
   * <br>
   * Default implementation simply returns the input.
   *
   * @param output	the output
   * @return		the generated output
   */
  protected Object postProcessOutputAsync(String output) {
    return output;
  }

  /**
   * For post-processing the output (blocking mode).
   * <br>
   * Default implementation simply returns the input.
   *
   * @param output	the output
   * @return		the generated output
   */
  protected Object postProcessOutputBlocking(String output) {
    return output;
  }

  /**
   * Executes the command.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String execute() {
    String		result;
    Object		res;
    CommandResult 	cmdResult;

    m_Running  = false;
    m_Executed = false;
    m_Stopped  = false;
    m_Output.clear();

    result = check();

    if (result == null) {
      try {
	m_Running = true;
	if (isUsingBlocking()) {
	  res       = doBlockingExecute();
	  m_Running = false;
	}
	else {
	  res = doAsyncExecute();
	}
	if (res != null) {
	  if (res instanceof CommandResult) {
	    cmdResult = (CommandResult) res;
	    if ((cmdResult.stderr != null) && !cmdResult.stderr.isEmpty())
	      m_StdErrProcessing.processBlocking(cmdResult.stderr);
	    log(cmdResult);
	    if (cmdResult.exitCode == 0) {
	      if (cmdResult.stdout != null)
		m_Output.add(postProcessOutputBlocking(cmdResult.stdout));
	    }
	    else {
	      result = commandResultToError(cmdResult);
	    }
	  }
	  else if (res instanceof String) {
	    result = (String) res;
	  }
	  else {
	    throw new IllegalStateException(
	      "Received an object of type " + Utils.classToString(res) + " instead of "
		+ "a String or " + Utils.classToString(CommandResult.class) + " one!");
	  }
	}
      }
      catch (Exception e) {
	m_Running = false;
	result    = LoggingHelper.handleException(this, "Failed to execute command!", e);
      }
    }

    m_Executed = true;

    return result;
  }

  /**
   * Returns whether the command was executed.
   *
   * @return		true if executed
   */
  @Override
  public boolean isExecuted() {
    return m_Executed;
  }

  /**
   * Returns whether the command is currently running.
   *
   * @return		true if running
   */
  @Override
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns whether the command finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    return isExecuted() && !isRunning();
  }

  /**
   * Whether there is any pending output.
   *
   * @return		true if output pending
   */
  @Override
  public boolean hasOutput() {
    return isRunning() || (m_Output.size() > 0);
  }

  /**
   * Returns the next line in the output.
   *
   * @return		the line, null if none available
   */
  @Override
  public Object output() {
    Object  	result;
    int 	i;

    result = null;

    if (m_Output.size() > 0)
      result = m_Output.remove(0);

    // wait a bit for more data to come through before giving up
    if ((result == null) && !isFinished() && !isUsingBlocking()) {
      for (i = 0; i < 10; i++) {
	Utils.wait(this, this, 100, 50);
	if (m_Output.size() > 0) {
	  result = m_Output.remove(0);
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}
