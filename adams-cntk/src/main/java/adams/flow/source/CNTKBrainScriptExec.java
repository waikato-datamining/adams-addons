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
 * CNTKBrainScriptExec.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ActorUtils;
import adams.flow.core.RunnableWithLogging;
import adams.flow.core.Token;
import adams.flow.standalone.CNTKSetup;
import adams.ml.cntk.CNTK;
import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Executes the specified BrainScript and forwards the specified process output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: CNTKBrainScriptExec
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
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: script)
 * &nbsp;&nbsp;&nbsp;The BrainScript to run.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-output-type &lt;STDOUT|STDERR|BOTH&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;Determines the output type; if BOTH is selected then an array is output
 * &nbsp;&nbsp;&nbsp;with stdout as first element and stderr as second
 * &nbsp;&nbsp;&nbsp;default: STDOUT
 * </pre>
 *
 * <pre>-prefix-stdout &lt;java.lang.String&gt; (property: prefixStdOut)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for output from stdout.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-prefix-stderr &lt;java.lang.String&gt; (property: prefixStdErr)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for output from stderr.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKBrainScriptExec
  extends AbstractSource
  implements StreamingProcessOwner {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the brainscript to execute. */
  protected PlaceholderFile m_Script;

  /** whether to output stderr instead of stdout or both. */
  protected StreamingProcessOutputType m_OutputType;

  /** the stdout prefix. */
  protected String m_PrefixStdOut;

  /** the stderr prefix. */
  protected String m_PrefixStdErr;

  /** the tokens to forward. */
  protected List m_Output;

  /** the process monitor. */
  protected StreamingProcessOutput m_ProcessOutput;

  /** the runnable executing the command. */
  protected RunnableWithLogging m_Monitor;

  /** in case an exception occurred executing the command (gets rethrown). */
  protected IllegalStateException m_ExecutionFailure;

  /** the (optional) setup. */
  protected CNTKSetup m_Setup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified BrainScript and forwards the specified process output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "script", "script",
      new PlaceholderFile());

    m_OptionManager.add(
      "output-type", "outputType",
      StreamingProcessOutputType.STDOUT);

    m_OptionManager.add(
      "prefix-stdout", "prefixStdOut",
      "");

    m_OptionManager.add(
      "prefix-stderr", "prefixStdErr",
      "");
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "script", m_Script);
  }

  /**
   * Sets the script to run.
   *
   * @param value	the script
   */
  public void setScript(PlaceholderFile value) {
    m_Script = value;
    reset();
  }

  /**
   * Returns the sript to run.
   *
   * @return 		the script
   */
  public PlaceholderFile getScript() {
    return m_Script;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String scriptTipText() {
    return "The BrainScript to run.";
  }

  /**
   * Sets what output from the process to forward.
   *
   * @param value	the output type
   */
  public void setOutputType(StreamingProcessOutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return
      "Determines the output type; if " + StreamingProcessOutputType.BOTH + " is selected "
	+ "then an array is output with stdout as first element and stderr as "
	+ "second";
  }

  /**
   * Sets the (optional) prefix to use for output from stdout.
   *
   * @param value	the prefix
   */
  public void setPrefixStdOut(String value) {
    m_PrefixStdOut = value;
    reset();
  }

  /**
   * Returns the (optional) prefix to use for output from stdout.
   *
   * @return 		the prefix
   */
  public String getPrefixStdOut() {
    return m_PrefixStdOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String prefixStdOutTipText() {
    return "The (optional) prefix to use for output from stdout.";
  }

  /**
   * Sets the (optional) prefix to use for output from stderr.
   *
   * @param value	the prefix
   */
  public void setPrefixStdErr(String value) {
    m_PrefixStdErr = value;
    reset();
  }

  /**
   * Returns the (optional) prefix to use for output from stderr.
   *
   * @return 		the prefix
   */
  public String getPrefixStdErr() {
    return m_PrefixStdErr;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String prefixStdErrTipText() {
    return "The (optional) prefix to use for output from stderr.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Determines the (optional) CNTK setup standalone.
   *
   * @return		the setup to use, null if none found
   */
  protected CNTKSetup getConnection() {
    return (CNTKSetup) ActorUtils.findClosestType(this, CNTKSetup.class, true);
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

    if (result == null)
      m_Setup = getConnection();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		cmd;
    final String	fCmd;

    m_Output.clear();

    // preprocess command
    cmd = (m_Setup == null) ? CNTK.getBinary().getAbsolutePath() : m_Setup.getBinary().getAbsolutePath();
    cmd += " configFile=" + m_Script.getAbsolutePath();
    fCmd = cmd;
    if (isLoggingEnabled())
      getLogger().info("Command: " + cmd);

    // setup thread
    m_ExecutionFailure = null;
    m_ProcessOutput = new StreamingProcessOutput(this);
    m_Monitor = new RunnableWithLogging() {
      private static final long serialVersionUID = -4475355379511760429L;
      protected Process m_Process;
      @Override
      protected void doRun() {
        try {
	  m_Process = Runtime.getRuntime().exec(fCmd, null, null);
	  m_ProcessOutput.monitor(fCmd, null, m_Process);
	  m_Process.destroy();
	}
	catch (Exception e) {
          m_ExecutionFailure = new IllegalStateException("Failed to execute: " + fCmd, e);
	}
	m_Monitor       = null;
        m_ProcessOutput = null;
      }
      @Override
      public void stopExecution() {
        if (m_Process != null)
          m_Process.destroy();
	super.stopExecution();
      }
    };
    new Thread(m_Monitor).start();

    return null;
  }

  /**
   * Adds the line from the output to the internal list of lines to output.
   *
   * @param line	the line to add
   * @param stdout	whether stdout or stderr
   */
  public void processOutput(String line, boolean stdout) {
    if (stdout)
      m_Output.add(m_PrefixStdOut + line);
    else
      m_Output.add(m_PrefixStdErr + line);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result = null;

    while ((m_Output.size() == 0) && !isStopped() && (m_Monitor != null)) {
      Utils.wait(this, this, 1000, 100);
    }

    if (m_ExecutionFailure != null)
      throw m_ExecutionFailure;

    if (!isStopped() && (m_Monitor != null)) {
      result = new Token(m_Output.get(0));
      m_Output.remove(0);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Output.size() > 0) || (m_Monitor != null);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Monitor != null)
      m_Monitor.stopExecution();
    super.stopExecution();
  }
}
