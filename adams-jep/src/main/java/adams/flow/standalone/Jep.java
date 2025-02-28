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
 * Jep.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.FlowJepScriptlet;
import adams.core.scripting.JepScript;
import adams.core.scripting.JepScriptingEngine;
import adams.core.scripting.JepUtils;
import adams.flow.control.VariableNameStorageNamePair;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Executes the Python script using Jep.<br>
 * The 'inputs' are items to retrieve from storage and store globally in the interpreter before executing the script.<br>
 * The 'outputs' are variables to put back into storeage.<br>
 * For more information on Jep see:<br>
 * https:&#47;&#47;github.com&#47;ninia&#47;jep&#47;<br>
 * <br>
 * By default, a global Jep scripting engine is used for executing scripts sequentially. Using a adams.flow.standalone.JepEngine actor in the flow allows to avoid this bottleneck, but the user needs to make sure that variables are unique across the scripts run in parallel.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Jep
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-inline-script &lt;adams.core.scripting.JepScript&gt; (property: inlineScript)
 * &nbsp;&nbsp;&nbsp;The inline script, if not using an external script file.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-expand-vars &lt;boolean&gt; (property: expandVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, all ADAMS variables get expanded before executing the script.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-inputs &lt;adams.flow.control.VariableNameStorageNamePair&gt; [-inputs ...] (property: inputs)
 * &nbsp;&nbsp;&nbsp;The storage items to add to the interpreter with the specified variable
 * &nbsp;&nbsp;&nbsp;names before executing the script.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-outputs &lt;adams.flow.control.VariableNameStorageNamePair&gt; [-outputs ...] (property: outputs)
 * &nbsp;&nbsp;&nbsp;The Python variables to put back into storage after executing the script.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Jep
  extends AbstractStandalone {

  private static final long serialVersionUID = -127963238102156771L;

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** the inline script. */
  protected JepScript m_InlineScript;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /** the input values. */
  protected VariableNameStorageNamePair[] m_Inputs;

  /** the output values. */
  protected VariableNameStorageNamePair[] m_Outputs;

  /** the engine in use. */
  protected transient JepEngine m_Engine;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the Python script using Jep.\n"
	     + "The 'inputs' are items to retrieve from storage and store globally in the interpreter "
	     + "before executing the script.\n"
	     + "The 'outputs' are variables to put back into storeage.\n"
	     + "For more information on Jep see:\n"
	     + JepUtils.projectURL() + "\n\n"
	     + "By default, a global Jep scripting engine is used for executing scripts sequentially. "
	     + "Using a " + Utils.classToString(JepEngine.class) + " actor in the flow allows to avoid "
	     + "this bottleneck, but the user needs to make sure that variables are unique across "
	     + "the scripts run in parallel.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "script", "scriptFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "inline-script", "inlineScript",
      getDefaultInlineScript());

    m_OptionManager.add(
      "expand-vars", "expandVariables",
      false);

    m_OptionManager.add(
      "inputs", "inputs",
      new VariableNameStorageNamePair[0]);

    m_OptionManager.add(
      "outputs", "outputs",
      new VariableNameStorageNamePair[0]);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (QuickInfoHelper.hasVariable(this, "scriptFile") || !m_ScriptFile.isDirectory())
      result = QuickInfoHelper.toString(this, "scriptFile", m_ScriptFile);
    else
      result = QuickInfoHelper.toString(this, "inlineScript", Shortening.shortenEnd(m_InlineScript.stringValue(), 50));
    if (result == null)
      result = "-no script-";
    result += QuickInfoHelper.toString(this, "expandVariables", m_ExpandVariables, "expand vars", ", ");
    result += QuickInfoHelper.toString(this, "inputs", m_Inputs, ", inputs: ");
    result += QuickInfoHelper.toString(this, "outputs", m_Outputs, ", outputs: ");

    return result;
  }

  /**
   * Sets the script file.
   *
   * @param value 	the script
   */
  public void setScriptFile(PlaceholderFile value) {
    m_ScriptFile = value;
    reset();
  }

  /**
   * Gets the script file.
   *
   * @return 		the script
   */
  public PlaceholderFile getScriptFile() {
    return m_ScriptFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String scriptFileTipText() {
    return "The script file to execute.";
  }

  /**
   * Returns the default inline script.
   *
   * @return		the default script
   */
  protected JepScript getDefaultInlineScript() {
    return new JepScript();
  }

  /**
   * Sets the inline script to use instead of the external script file.
   *
   * @param value 	the inline script
   */
  public void setInlineScript(JepScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Gets the inline script to use instead of the external script file.
   *
   * @return 		the inline script
   */
  public JepScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inlineScriptTipText() {
    return "The inline script, if not using an external script file.";
  }

  /**
   * Sets whether to expand ADAMS variables.
   *
   * @param value 	true if to expand
   */
  public void setExpandVariables(boolean value) {
    m_ExpandVariables = value;
    reset();
  }

  /**
   * Returns whether to expand ADAMS variables.
   *
   * @return 		true if to expand
   */
  public boolean getExpandVariables() {
    return m_ExpandVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String expandVariablesTipText() {
    return "If enabled, all ADAMS variables get expanded before executing the script.";
  }

  /**
   * Sets the input values, i.e., storage items that get added to the interpreter with the specified variable names.
   *
   * @param value 	the input values
   */
  public void setInputs(VariableNameStorageNamePair[] value) {
    m_Inputs = value;
    reset();
  }

  /**
   * Gets the input values, i.e., storage items that get added to the interpreter with the specified variable names.
   *
   * @return 		the input values
   */
  public VariableNameStorageNamePair[] getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inputsTipText() {
    return "The storage items to add to the interpreter with the specified variable names before executing the script.";
  }

  /**
   * Sets the output values, i.e., the variables to put back into storage after executing the script.
   *
   * @param value 	the output values
   */
  public void setOutputs(VariableNameStorageNamePair[] value) {
    m_Outputs = value;
    reset();
  }

  /**
   * Gets the output values, i.e., the variables to put back into storage after executing the script.
   *
   * @return 		the output values
   */
  public VariableNameStorageNamePair[] getOutputs() {
    return m_Outputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputsTipText() {
    return "The Python variables to put back into storage after executing the script.";
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
      m_Engine = (JepEngine) ActorUtils.findClosestType(this, JepEngine.class, true);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    FlowJepScriptlet 	scriptlet;

    result = null;

    if (!m_ScriptFile.exists() || m_ScriptFile.isDirectory())
      scriptlet = new FlowJepScriptlet(getFullName(), m_InlineScript.getValue(), m_Inputs, m_Outputs, null, m_ExpandVariables);
    else
      scriptlet = new FlowJepScriptlet(getFullName(), m_ScriptFile, m_Inputs, m_Outputs, null, m_ExpandVariables);
    scriptlet.setFlowContext(this);
    scriptlet.setLoggingLevel(getLoggingLevel());

    if (m_Engine == null)
      JepScriptingEngine.getSingleton().add(scriptlet);
    else
      m_Engine.getEngine().add(scriptlet);

    while (!isStopped() && !scriptlet.hasFinished())
      Utils.wait(this, 1000, 100);

    if (!isStopped())
      result = scriptlet.getLastError();

    return result;
  }
}
