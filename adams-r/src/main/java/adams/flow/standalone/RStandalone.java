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
 * RStandalone.java
 * Copyright (C) 2012-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.RScript;
import adams.flow.core.ActorUtils;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Carries out an R function on the input script.
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
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RStandalone
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
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when loading the script file.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-script &lt;adams.core.scripting.RScript&gt; (property: inlineScript)
 * &nbsp;&nbsp;&nbsp;Script to pass into R.
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
 * <pre>-line-by-line &lt;boolean&gt; (property: lineByLine)
 * &nbsp;&nbsp;&nbsp;If enabled, the script gets split into lines and evaluated one line at a
 * &nbsp;&nbsp;&nbsp;time (useful for debugging).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author rsmith
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RStandalone
  extends AbstractStandalone
  implements EncodingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7114485978382420994L;

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** The R script. */
  protected RScript m_InlineScript;

  /** whether the script contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ScriptContainsPlaceholder;

  /** whether the script contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ScriptContainsVariable;

  /** whether to evaluate line by line. */
  protected boolean m_LineByLine;

  /** Connection to Rserve */
  protected RConnection m_RConn;
  
  /** the Rserve actor. */
  protected Rserve m_Rserve;

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
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "script", "inlineScript",
      new RScript());

    m_OptionManager.add(
      "placeholder", "scriptContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "scriptContainsVariable",
      false);

    m_OptionManager.add(
      "line-by-line", "lineByLine",
      true);
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
   * Sets the encoding to use for the script file.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  @Override
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use for the script file.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  @Override
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String encodingTipText() {
    return "The type of encoding to use when loading the script file.";
  }

  /**
   * Returns the script.
   * 
   * @return the script
   */
  public RScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Sets the script to be fed into R.
   * 
   * @param inlineScript
   *          the script to be fed to R
   */
  public void setInlineScript(RScript inlineScript) {
    m_InlineScript = inlineScript;
    reset();
  }

  /**
   * Description of the script.
   * 
   * @return string version of the description
   */
  public String inlineScriptTipText() {
    return "Script to pass into R.";
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
   * Sets whether to evaluate the script line by line.
   *
   * @param value	true if line-by-line
   */
  public void setLineByLine(boolean value) {
    m_LineByLine = value;
    reset();
  }

  /**
   * Returns whether to evaluate the script line by line.
   *
   * @return		true if line-by-line
   */
  public boolean getLineByLine() {
    return m_LineByLine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lineByLineTipText() {
    return "If enabled, the script gets split into lines and evaluated one line at a time (useful for debugging).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> options;

    if (QuickInfoHelper.hasVariable(this, "scriptFile") || !m_ScriptFile.isDirectory())
      result = QuickInfoHelper.toString(this, "scriptFile", m_ScriptFile, "file: ");
    else
      result = QuickInfoHelper.toString(this, "inlineScript", Shortening.shortenEnd(m_InlineScript.isEmpty() ? "-none-" : m_InlineScript.stringValue(), 50), "script: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "scriptContainsPlaceholder", m_ScriptContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "scriptContainsVariable", m_ScriptContainsVariable, "Var"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "lineByLine", !m_LineByLine, "Block"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets up the connection to Rserve.
   */
  @Override
  public String setUp() {
    String 	result;
    
    result = super.setUp();

    if (result == null) {
      m_Rserve = (Rserve) ActorUtils.findClosestType(this, Rserve.class, true);
      if (m_Rserve == null)
	result = "Failed to find " + Rserve.class.getName() + " standalone with Rserve configuration!";
    }

    return result;
  }

  /**
   * Connects to Rserve and feeds it the script.
   */
  @Override
  protected String doExecute() {
    String	script;

    if (m_RConn == null) {
      m_RConn = m_Rserve.newConnection();
      if (m_RConn == null)
	return "Could not connect to Rserve!";
    }

    if (m_ScriptFile.isDirectory() || !m_ScriptFile.exists())
      script = m_InlineScript.getValue();
    else
      script = Utils.flatten(FileUtils.loadFromFile(m_ScriptFile, m_Encoding.getValue()), "\n");

    if (m_ScriptContainsVariable)
      script = getVariables().expand(script);
    if (m_ScriptContainsPlaceholder)
      script = Placeholders.getSingleton().expand(script);

    if (m_LineByLine) {
      String[] lines = script.split("\r?\n");
      for (String line: lines) {
	if (isLoggingEnabled())
	  getLogger().info("Evaluating: " + line);
	try {
	  m_RConn.eval(line);
	}
	catch (Exception ex) {
	  return handleException("Error occurred evaluating: " + line, ex);
	}
      }
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Evaluating:\n" + script);
      try {
	m_RConn.eval(script);
      }
      catch (Exception ex) {
	return handleException("Error occurred evaluating: " + script, ex);
      }
    }

    return null;
  }

  /**
   * Overall description of this flow.
   */
  @Override
  public String globalInfo() {
    return "Carries out an R function on the input script.";
  }

  /**
   * Closes the Rserve connection as the panel closes.
   */
  @Override
  public void wrapUp() {
    if (m_Rserve != null) {
      m_Rserve.closeConnection(m_RConn);
      m_RConn  = null;
      m_Rserve = null;
    }

    super.wrapUp();
  }
}
