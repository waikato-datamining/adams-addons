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
 * Exec.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.management.ProcessUtils;
import adams.core.option.OptionUtils;
import adams.flow.core.Unknown;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

/**
 <!-- globalinfo-start -->
 * Executes the specified command when the rat finishes execution.<br>
 * Ignores any input.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-cmd &lt;java.lang.String&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The external command to run.
 * &nbsp;&nbsp;&nbsp;default: ls -l .
 * </pre>
 * 
 * <pre>-placeholder &lt;boolean&gt; (property: commandContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the command 
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-variable &lt;boolean&gt; (property: commandContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the command 
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Exec
  extends AbstractRatOutput {

  private static final long serialVersionUID = 2950260223110007595L;

  /** the command to run. */
  protected String m_Command;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Executes the specified command when the rat finishes execution.\n"
	+ "Ignores any input.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "cmd", "command",
      "ls -l .");

    m_OptionManager.add(
      "placeholder", "commandContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "commandContainsVariable",
      false);
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
  public void setCommand(String value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return 		the command
   */
  public String getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The external command to run.";
  }

  /**
   * Sets whether the command string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if command string contains a placeholder
   */
  public void setCommandContainsPlaceholder(boolean value) {
    m_CommandContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the command string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if command string contains a placeholder
   */
  public boolean getCommandContainsPlaceholder() {
    return m_CommandContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the command string.";
  }

  /**
   * Sets whether the command string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if command string contains a variable
   */
  public void setCommandContainsVariable(boolean value) {
    m_CommandContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the command string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if command string contains a variable
   */
  public boolean getCommandContainsVariable() {
    return m_CommandContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the command string.";
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual transmission.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String		result;
    String		cmd;
    CollectingProcessOutput proc;

    result = null;

    cmd = m_Command;
    if (m_CommandContainsVariable)
      cmd = getOwner().getVariables().expand(cmd);
    if (m_CommandContainsPlaceholder)
      cmd = Placeholders.getSingleton().expand(cmd).replace("\\", "/");

    try {
      proc = ProcessUtils.execute(OptionUtils.splitOptions(cmd));
      if (!proc.hasSucceeded())
	result = ProcessUtils.toErrorOutput(proc);
    }
    catch (Exception e) {
      result = handleException("Failed to execute command: " + cmd, e);
    }

    return result;
  }
}
