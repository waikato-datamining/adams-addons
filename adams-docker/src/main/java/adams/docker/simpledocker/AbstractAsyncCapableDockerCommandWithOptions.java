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
 * AbstractAsyncCapableDockerCommandWithOptions.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.docker.SimpleDockerHelper;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;

import java.util.List;

/**
 * Ancestor for docker commands that can be run in blocking or async mode, supporting additional options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAsyncCapableDockerCommandWithOptions
  extends AbstractDockerCommandWithOptions
  implements AsyncCapableDockerCommand {

  private static final long serialVersionUID = -4848388052660380382L;

  /** whether to use blocking or async mode. */
  protected boolean m_Blocking;

  /** for capturing the command output. */
  protected transient StreamingProcessOutput m_ProcessOutput;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "blocking", "blocking",
      true);
  }

  /**
   * Sets whether to execute in blocking or async fashion.
   *
   * @param value	true for blocking
   */
  @Override
  public void setBlocking(boolean value) {
    m_Blocking = value;
    reset();
  }

  /**
   * Returns whether to execute in blocking or async fashion.
   *
   * @return		true for blocking
   */
  @Override
  public boolean getBlocking() {
    return m_Blocking;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String blockingTipText() {
    return "If enabled, the command is executed in blocking fashion rather than asynchronous.";
  }

  /**
   * Returns the class of the output the command generates.
   *
   * @return		the type
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Adds the line received on stdout from the command.
   *
   * @param line	the line to add
   */
  @Override
  public void addStdOut(String line) {
    m_Output.add(postProcessOutputAsync(line));
  }

  /**
   * Adds the line received on stderr from the command.
   *
   * @param line	the line to add
   */
  @Override
  public void addStdErr(String line) {
    m_StdErrProcessing.processAsync(line);
  }

  /**
   * Whether the command is used in a blocking or async fashion.
   *
   * @return		true if blocking, false if async
   */
  @Override
  public boolean isUsingBlocking() {
    return m_Blocking;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "blocking", (m_Blocking ? "blocking" : "async"), "mode: ");
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Assembles the command to run. Docker executable gets added separately.
   *
   * @return		the command
   */
  protected abstract List<String> buildCommand();

  /**
   * Executes the command.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  @Override
  protected Object doAsyncExecute() {
    List<String>	cmd;
    ProcessBuilder 	builder;
    Runnable		runnable;
    Thread		thread;

    cmd = buildCommand();
    cmd.add(0, m_Connection.getAcualBinary());
    log(cmd);

    builder = new ProcessBuilder();
    builder.command(cmd);
    m_ProcessOutput = new StreamingProcessOutput(new AsyncCapableDockerCommandOutputProcessor(this));
    runnable = new Runnable() {
      @Override
      public void run() {
        try {
          m_ProcessOutput.monitor(builder);
        }
        catch (Exception e) {
          LoggingHelper.handleException(
            AbstractAsyncCapableDockerCommandWithOptions.this,
            "Failed to execute command: " + Utils.flatten(cmd, " "), e);
        }
        m_ProcessOutput = null;
        m_Running       = false;
      }
    };
    thread = new Thread(runnable);
    thread.start();
    return null;
  }

  /**
   * Executes the command.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  @Override
  protected Object doBlockingExecute() {
    List<String>	cmd;

    cmd = buildCommand();
    log(cmd);
    return SimpleDockerHelper.command(m_Connection.getAcualBinary(), cmd);
  }

  /**
   * Returns whether the command finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    return super.isFinished() && (m_ProcessOutput == null);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_ProcessOutput != null)
      m_ProcessOutput.destroy();
    super.stopExecution();
  }
}
