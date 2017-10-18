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
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.io.lister.LocalDirectoryLister;
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
 * <pre>-script-contains-variables &lt;boolean&gt; (property: scriptContainsVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, any variables that might be present in the script file get expanded
 * &nbsp;&nbsp;&nbsp;first.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-type &lt;STDOUT|STDERR|BOTH&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;Determines the output type; if BOTH is selected then an array is output
 * &nbsp;&nbsp;&nbsp;with stdout as first element and stderr as second
 * &nbsp;&nbsp;&nbsp;default: STDERR
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
 * <pre>-delete-temp-models &lt;boolean&gt; (property: deleteTempModels)
 * &nbsp;&nbsp;&nbsp;If enabled, any temporary models get deleted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-delete-checkpoint-files &lt;boolean&gt; (property: deleteCheckPointFiles)
 * &nbsp;&nbsp;&nbsp;If enabled, any checkpoint files get deleted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-model-directory &lt;adams.core.io.PlaceholderDirectory&gt; (property: modelDirectory)
 * &nbsp;&nbsp;&nbsp;The directory containing the models, temp models and checkpoint files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-model-extension &lt;java.lang.String&gt; (property: modelExtension)
 * &nbsp;&nbsp;&nbsp;The file extension used by the models (incl dot).
 * &nbsp;&nbsp;&nbsp;default: .cmf
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

  /** the tmp brainscript to execute (if necessary). */
  protected PlaceholderFile m_TmpScript;

  /** whether to script contains variables that need expanding first. */
  protected boolean m_ScriptContainsVariables;

  /** whether to output stderr instead of stdout or both. */
  protected StreamingProcessOutputType m_OutputType;

  /** the stdout prefix. */
  protected String m_PrefixStdOut;

  /** the stderr prefix. */
  protected String m_PrefixStdErr;

  /** whether to delete tmp models. */
  protected boolean m_DeleteTempModels;

  /** whether to delete checkpoint file. */
  protected boolean m_DeleteCheckPointFiles;

  /** the directory containing the models. */
  protected PlaceholderDirectory m_ModelDirectory;

  /** the extension used by the models. */
  protected String m_ModelExtension;

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

  /** for identifying the temp and checkpoint files. */
  protected LocalDirectoryLister m_Lister;

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
      "script-contains-variables", "scriptContainsVariables",
      false);

    m_OptionManager.add(
      "output-type", "outputType",
      StreamingProcessOutputType.STDERR);

    m_OptionManager.add(
      "prefix-stdout", "prefixStdOut",
      "");

    m_OptionManager.add(
      "prefix-stderr", "prefixStdErr",
      "");

    m_OptionManager.add(
      "delete-temp-models", "deleteTempModels",
      false);

    m_OptionManager.add(
      "delete-checkpoint-files", "deleteCheckPointFiles",
      false);

    m_OptionManager.add(
      "model-directory", "modelDirectory",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "model-extension", "modelExtension",
      ".cmf");
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
   * Returns the script to run.
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
   * Sets whether the script contains variables that require expanding first.
   *
   * @param value	true if contains variables
   */
  public void setScriptContainsVariables(boolean value) {
    m_ScriptContainsVariables = value;
    reset();
  }

  /**
   * Returns whether the script contains variables that require expanding first.
   *
   * @return 		true if contains variables
   */
  public boolean getScriptContainsVariables() {
    return m_ScriptContainsVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String scriptContainsVariablesTipText() {
    return "If enabled, any variables that might be present in the script file get expanded first.";
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
   * Sets whether to delete temporary models.
   *
   * @param value	true if to delete
   */
  public void setDeleteTempModels(boolean value) {
    m_DeleteTempModels = value;
    reset();
  }

  /**
   * Returns whether to delete temporary models.
   *
   * @return 		true if to delete
   */
  public boolean getDeleteTempModels() {
    return m_DeleteTempModels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String deleteTempModelsTipText() {
    return "If enabled, any temporary models get deleted.";
  }

  /**
   * Sets whether to delete checkpoint files models.
   *
   * @param value	true if to delete
   */
  public void setDeleteCheckPointFiles(boolean value) {
    m_DeleteCheckPointFiles = value;
    reset();
  }

  /**
   * Returns whether to delete checkpoint files models.
   *
   * @return 		true if to delete
   */
  public boolean getDeleteCheckPointFiles() {
    return m_DeleteCheckPointFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String deleteCheckPointFilesTipText() {
    return "If enabled, any checkpoint files get deleted.";
  }

  /**
   * Sets the directory containing the models, temp models and checkpoint files.
   *
   * @param value	the directory
   */
  public void setModelDirectory(PlaceholderDirectory value) {
    m_ModelDirectory = value;
    reset();
  }

  /**
   * Returns the directory containing the models, temp models and checkpoint files.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getModelDirectory() {
    return m_ModelDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String modelDirectoryTipText() {
    return "The directory containing the models, temp models and checkpoint files.";
  }

  /**
   * Sets the extension that the models use (incl dot).
   *
   * @param value	the extension
   */
  public void setModelExtension(String value) {
    m_ModelExtension = value;
    reset();
  }

  /**
   * Returns the extension that the models use (incl dot).
   *
   * @return 		the extension
   */
  public String getModelExtension() {
    return m_ModelExtension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String modelExtensionTipText() {
    return "The file extension used by the models (incl dot).";
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
   * Preprocesses the script if necessary.
   *
   * @return		the script filename
   */
  protected String preprocessScript() {
    String		result;
    List<String>	lines;
    String		content;
    String		msg;

    result      = m_Script.getAbsolutePath();
    m_TmpScript = null;

    if (m_ScriptContainsVariables) {
      lines = FileUtils.loadFromFile(m_Script);
      if (lines != null) {
        result  = TempUtils.createTempFile("adams-cntk-bs-", ".bs").getAbsolutePath();
	content = Utils.flatten(lines, "\n");
	content = getVariables().expand(content);
	msg     = FileUtils.writeToFileMsg(result, content, false, null);
	if (msg != null)
	  throw new IllegalStateException("Failed to write expanded script!\n" + msg);
	m_TmpScript = new PlaceholderFile(result);
      }
    }

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
    String		script;

    m_Output.clear();

    // preprocess script
    script = preprocessScript();

    // preprocess command
    cmd = (m_Setup == null) ? CNTK.getBinary().getAbsolutePath() : m_Setup.getBinary().getAbsolutePath();
    cmd += " configFile=" + script;
    fCmd = cmd;
    if (isLoggingEnabled())
      getLogger().info("Command: " + cmd);

    // remove files?
    m_Lister = null;
    if (m_DeleteTempModels || m_DeleteCheckPointFiles) {
      m_Lister = new LocalDirectoryLister();
      m_Lister.setWatchDir(m_ModelDirectory.getAbsolutePath());
      if (m_DeleteTempModels && m_DeleteCheckPointFiles)
        m_Lister.setRegExp(new BaseRegExp(".*\\" + m_ModelExtension + "\\.([0-9]+|ckp)$"));
      else if (m_DeleteTempModels)
        m_Lister.setRegExp(new BaseRegExp(".*\\" + m_ModelExtension + "\\.[0-9]+$"));
      else if (m_DeleteCheckPointFiles)
        m_Lister.setRegExp(new BaseRegExp(".*\\" + m_ModelExtension+ "\\.ckp$"));
      m_Lister.setRecursive(false);
      m_Lister.setListDirs(false);
      m_Lister.setListFiles(true);
    }

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
	  if (m_ProcessOutput.getExitCode() != 0)
	    m_ExecutionFailure = new IllegalStateException("Exit code " + m_ProcessOutput.getExitCode() + " when executing: " + fCmd);
	}
	catch (Exception e) {
          m_ExecutionFailure = new IllegalStateException("Failed to execute: " + fCmd, e);
	}
	m_Monitor       = null;
        m_ProcessOutput = null;
        if (m_TmpScript != null)
	  m_TmpScript.delete();
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
    String[]	files;

    if (stdout)
      m_Output.add(m_PrefixStdOut + line);
    else
      m_Output.add(m_PrefixStdErr + line);

    // remove temp/checkpoint files
    if (m_Lister != null) {
      files = m_Lister.list();
      for (String file : files)
	FileUtils.delete(file);
    }
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token			result;
    IllegalStateException	exc;

    result = null;

    while ((m_Output.size() == 0) && !isStopped() && (m_Monitor != null)) {
      Utils.wait(this, this, 1000, 100);
    }

    if (m_ExecutionFailure != null) {
      exc                = m_ExecutionFailure;
      m_ExecutionFailure = null;
      throw exc;
    }

    if (!isStopped() && (m_Output.size() > 0)) {
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
