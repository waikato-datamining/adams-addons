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
 * CustomScriptlet.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;

/**
 <!-- globalinfo-start -->
 * Allows the user to enter a custom Gnuplot script snippet. Variables get expanded if an owner is set.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-script &lt;adams.core.base.BaseText&gt; (property: script)
 * &nbsp;&nbsp;&nbsp;The custom script code.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomScriptlet
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -3540923217777778401L;

  /** the custom script code. */
  protected BaseText m_Script;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Allows the user to enter a custom Gnuplot script snippet. "
	+ "Variables get expanded if an owner is set.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "script", "script",
	    new BaseText());
  }

  /**
   * Sets the script code.
   *
   * @param value	the code
   */
  public void setScript(BaseText value) {
    m_Script = value;
    reset();
  }

  /**
   * Returns the script code.
   *
   * @return		the code
   */
  public BaseText getScript() {
    return m_Script;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String scriptTipText() {
    return "The custom script code.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "script", m_Script, "script: ");
  }

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected String doGenerate() {
    if (getOwner() != null)
      return getOwner().getVariables().expand(m_Script.getValue());
    else
      return m_Script.getValue();
  }
}
