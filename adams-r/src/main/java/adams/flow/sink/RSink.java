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
 * RSink.java
 * Copyright (C) 2012-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.RDataHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.RScript;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.Rserve;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Carries out an r command on the token passed in. The input can be accessed via 'X'.<br>
 * Variables are supported as well, e.g.: pow(X,&#64;{exp}) with '&#64;{exp}' being a variable available at execution time.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[][]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: RSink
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
 * &nbsp;&nbsp;&nbsp;Inline script to pass into r. The input value can be accessed via 'X'.
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
public class RSink
  extends AbstractSink
  implements EncodingSupporter {

  /** for serialization */
  private static final long serialVersionUID = 6150602242914328836L;

  /** the placeholder for the input value. */
  public final static String INPUT = "X";

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** Script to pass to r */
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
   * Adds to the internal list of options.
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
   * Sets the script.
   * 
   * @param val
   *          the script
   */
  public void setInlineScript(RScript val) {
    m_InlineScript = val;
    reset();
  }

  /**
   * Gets the script.
   * 
   * @return the script
   */
  public RScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Tool tip about the script.
   * 
   * @return tool tip message
   */
  public String inlineScriptTipText() {
    return "Inline script to pass into r. The input value can be accessed via '" + INPUT + "'.";
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
    List<String> 	options;

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
   * List of classes that can be used as input.
   */
  public Class[] accepts() {
    return new Class[] {
	Integer.class, 
	String.class, 
	Double.class,
	Double[].class, 
	Double[][].class,
	SpreadSheet.class
    };
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
   * Executes the flow, including reading the input and returning R's output.
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

    try {
      if (m_InputToken.getPayload() instanceof Integer)
	m_RConn.assign(INPUT, new int[] {((Integer) m_InputToken.getPayload())});
      else if (m_InputToken.getPayload() instanceof String)
	m_RConn.assign(INPUT, (String) m_InputToken.getPayload());
      else if (m_InputToken.getPayload() instanceof Double)
	m_RConn.assign(INPUT, new double[] {((Double) m_InputToken.getPayload())});
      else if (m_InputToken.getPayload() instanceof Double[])
	m_RConn.assign(INPUT, StatUtils.toDoubleArray((Double[]) m_InputToken.getPayload()));
      else if (m_InputToken.getPayload() instanceof Double[][]) {
	Double[][] temp = (Double[][]) m_InputToken.getPayload();
	double[][] dubMat = new double[temp.length][];
	for (int i = 0; i < temp.length; i++) {
	  dubMat[i] = StatUtils.toDoubleArray(temp[i]);
	}
	m_RConn.assign(INPUT, dubMat[0]);
	for (int i = 1; i < dubMat.length; i++) {
	  m_RConn.assign("tmp", dubMat[i]);
	  m_RConn.eval(INPUT + "<-rbind(" + INPUT + ",tmp)");
	}
      }
      else if (m_InputToken.getPayload() instanceof SpreadSheet) {
	m_RConn.assign(INPUT, RDataHelper.spreadsheetToDataframe((SpreadSheet) m_InputToken.getPayload()));
      }
      else {
	throw new IllegalStateException("Unhandled class: " + m_InputToken.getPayload().getClass());
      }

      if (m_LineByLine) {
	String[] lines = script.split("\r?\n");
	for (String line : lines) {
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
    }
    catch (Exception e) {
      return handleException("Error occurred calling Rserve:", e);
    }
    return null;
  }

  /**
   * Info about this flow.
   */
  @Override
  public String globalInfo() {
    return "Carries out an r command on the token passed in. "
	+ "The input can be accessed via '" + INPUT + "'.\n"
	+ "Variables are supported as well, e.g.: pow(X,@{exp}) with '@{exp}' "
	+ "being a variable available at execution time.";
  }

  /**
   * Closes the Rserve connection as the flow finishes.
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
