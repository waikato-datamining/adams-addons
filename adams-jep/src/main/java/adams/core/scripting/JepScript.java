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
 * JepScript.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.gui.core.AbstractAdvancedScript;
import adams.gui.core.AbstractTextAreaPanelWithAdvancedSyntaxHighlighting;
import adams.gui.core.JepSyntaxEditorPanel;

/**
 * Wrapper for a Jep scripts to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepScript
  extends AbstractAdvancedScript {

  /** for serialization. */
  private static final long serialVersionUID = 4309078655122480376L;

  /**
   * Initializes the string with length 0.
   */
  public JepScript() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public JepScript(String s) {
    super(s);
  }

  /**
   * Returns the tip text for the script.
   *
   * @return		the tool tip
   */
  @Override
  protected String getScriptTipText() {
    return "Jep script";
  }

  /**
   * Returns the configured text editor panel to use in the GOE.
   *
   * @return		the text editor panel
   */
  @Override
  public AbstractTextAreaPanelWithAdvancedSyntaxHighlighting getTextAreaPanel() {
    return new JepSyntaxEditorPanel();
  }
  
  /**
   * Returns whether inline editing in the GOE is allowed.
   * 
   * @return		true if inline editing is allowed
   */
  @Override
  public boolean allowsInlineEditing() {
    return false;
  }
}
