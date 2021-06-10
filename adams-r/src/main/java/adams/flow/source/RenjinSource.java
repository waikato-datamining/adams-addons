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
 * RenjinSource.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.RScript;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.RenjinContext;
import org.renjin.sexp.SEXP;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Executes the specified script and forwards the result.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.renjin.sexp.SEXP<br>
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
 * &nbsp;&nbsp;&nbsp;default: RenjinSource
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
 * <pre>-script-file &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-inline-script &lt;adams.core.scripting.RScript&gt; (property: inlineScript)
 * &nbsp;&nbsp;&nbsp;The inline script to execute (when no script file supplied).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-placeholder &lt;boolean&gt; (property: scriptContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion in the script.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variable &lt;boolean&gt; (property: scriptContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion in the script.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinSource
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 1958559911609186904L;

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** Script to pass to r */
  protected RScript m_InlineScript;

  /** whether the script contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ScriptContainsPlaceholder;

  /** whether the script contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ScriptContainsVariable;

  /** the context. */
  protected transient RenjinContext m_Context;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified script and forwards the result.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Context = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "script-file", "scriptFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "inline-script", "inlineScript",
      new RScript());

    m_OptionManager.add(
      "placeholder", "scriptContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "scriptContainsVariable",
      false);
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
    return "The script file to load and execute.";
  }

  /**
   * Sets the script to execute (when no script file supplied).
   *
   * @param value 	the script
   */
  public void setInlineScript(RScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Returns to script to execute (when no script file supplied).
   *
   * @return 		the script
   */
  public RScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inlineScriptTipText() {
    return "The inline script to execute (when no script file supplied).";
  }

  /**
   * Sets whether the script contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if script contains a placeholder
   */
  public void setScriptContainsPlaceholder(boolean value) {
    m_ScriptContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the scripts contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if script contains a placeholder
   */
  public boolean getScriptContainsPlaceholder() {
    return m_ScriptContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scriptContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion in the script.";
  }

  /**
   * Sets whether the script contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if script contains a variable
   */
  public void setScriptContainsVariable(boolean value) {
    m_ScriptContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the script contains a variable which needs to be
   * expanded first.
   *
   * @return		true if script contains a variable
   */
  public boolean getScriptContainsVariable() {
    return m_ScriptContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scriptContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion in the script.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SEXP.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    if (QuickInfoHelper.hasVariable(this, "scriptFile") || !m_ScriptFile.isDirectory())
      result = QuickInfoHelper.toString(this, "scriptFile", m_ScriptFile, "file: ");
    else
      result = QuickInfoHelper.toString(this, "inlineScript", Shortening.shortenEnd(m_InlineScript.stringValue(), 50), "inline: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "scriptContainsPlaceholder", m_ScriptContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "scriptContainsVariable", m_ScriptContainsVariable, "Var"));
    result += QuickInfoHelper.flatten(options);

    return result;
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
      m_Context = (RenjinContext) ActorUtils.findClosestType(this, RenjinContext.class);
      if (m_Context == null)
        result = "Failed to locate instance of " + Utils.classToString(RenjinContext.class) + "!";
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
    String	script;
    SEXP	output;

    result = null;

    if (m_ScriptFile.isDirectory() || !m_ScriptFile.exists())
      script = m_InlineScript.getValue();
    else
      script = Utils.flatten(FileUtils.loadFromFile(m_ScriptFile), "\n");

    if (m_ScriptContainsVariable)
      script = getVariables().expand(script);
    if (m_ScriptContainsPlaceholder)
      script = Placeholders.getSingleton().expand(script);

    try {
      output = (SEXP) m_Context.getEngine().eval(script);
      m_OutputToken = new Token(output);
    }
    catch (Exception e) {
      result = handleException("Failed to evaluate script:\n" + script, e);
    }

    return result;
  }
}
