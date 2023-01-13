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
 * AbstractDockerOptionsWithOptions.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;

import java.util.logging.Level;

/**
 * Ancestor for commands that take options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDockerCommandWithOptions
  extends AbstractDockerCommand {

  private static final long serialVersionUID = 7898785828472200774L;

  /** the options for the command. */
  protected BaseString[] m_Options;

  /** the options as single string. */
  protected String m_OptionsString;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "option", "options",
      new BaseString[0]);

    m_OptionManager.add(
      "options-string", "optionsString",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (!m_OptionsString.isEmpty() || getOptionManager().hasVariableForProperty("optionsString"))
      return QuickInfoHelper.toString(this, "optionsString", (m_OptionsString.isEmpty() ? "-none-" : m_OptionsString), "options string: ");
    else
      return QuickInfoHelper.toString(this, "options", m_Options, "options: ");
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(BaseString[] value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the options for the command.
   *
   * @return		the options
   */
  public BaseString[] getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsTipText() {
    return "The options for the command.";
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptionsString(String value) {
    m_OptionsString = value;
    reset();
  }

  /**
   * Returns the options for the command as single string.
   *
   * @return		the options
   */
  public String getOptionsString() {
    return m_OptionsString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsStringTipText() {
    return "The options for the command as a single string; overrides the options array.";
  }

  /**
   * Returns the actual options to use. The options string takes precendence over the array.
   *
   * @return		the options
   */
  protected String[] getActualOptions() {
    try {
      if (!m_OptionsString.isEmpty())
	return OptionUtils.splitOptions(m_OptionsString);
      else
	return BaseObject.toStringArray(m_Options);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse options!", e);
      return new String[0];
    }
  }
}
