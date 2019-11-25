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
 * GroovyPlugin.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.core.scripting.FileBasedScriptingWithOptions;
import adams.core.scripting.Groovy;
import adams.flow.core.Actor;
import adams.flow.core.AdditionalOptionsHandler;
import adams.flow.core.AdditionalOptionsHandlerUtils;

/**
 * Wrapper for a REST plugin written in Groovy.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GroovyPlugin
  extends AbstractOptionHandler
  implements FileBasedScriptingWithOptions, QuickInfoSupporter {

  private static final long serialVersionUID = -3914295307329929706L;

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** the options for the script. */
  protected String m_ScriptOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Wrapper for a REST plugin written in Groovy.\n"
      + "Loads the script file and applies the supplied options.\n"
      + "Requires the class in the Groovy script to implement the "
      + Utils.classToString(AdditionalOptionsHandler.class) + " interface.";
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
      "options", "scriptOptions",
      new BaseText());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "scriptFile", (m_ScriptFile + " " + m_ScriptOptions).trim(), null);
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
   * Sets the script options.
   *
   * @param value 	the options
   */
  public void setScriptOptions(BaseText value) {
    m_ScriptOptions = value.getValue();
    reset();
  }

  /**
   * Gets the script options.
   *
   * @return 		the options
   */
  public BaseText getScriptOptions() {
    return new BaseText(m_ScriptOptions);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String scriptOptionsTipText() {
    return "The options for the script.";
  }

  /**
   * Loads the Groovy REST plugin.
   *
   * @param context 	the flow context
   * @return		the plugin
   * @throws Exception	if loading failed or not implementing {@link AdditionalOptionsHandler}
   */
  public Object loadPlugin(Actor context) throws Exception {
    Object			result;

    result = Groovy.getSingleton().newInstance(m_ScriptFile, Object.class);
    if (!(result instanceof AdditionalOptionsHandler))
      throw new Exception("Script has to implement " + Utils.classToString(AdditionalOptionsHandler.class) + " in order to set the options!");

    AdditionalOptionsHandlerUtils.setOptions(result, m_ScriptOptions, context.getVariables());

    return result;
  }
}
