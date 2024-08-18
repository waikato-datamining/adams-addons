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
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.menu;

import adams.core.io.PlaceholderFile;
import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.GUIHelper;

/**
 * Launches the editor for Jep/Python scripts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepConsole
  extends AbstractParameterHandlingMenuItemDefinition {

  private static final long serialVersionUID = -5145792084134611658L;

  /**
   * Initializes the menu item with no owner.
   */
  public JepConsole() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public JepConsole(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return the title
   */
  @Override
  public String getTitle() {
    return "Jep/Python console";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "jep.png";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_TOOLS;
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    adams.gui.tools.jep.JepConsole panel = new adams.gui.tools.jep.JepConsole();
    createChildFrame(panel, GUIHelper.getDefaultDialogDimension());
    if (m_Parameters.length > 0)
      panel.open(new PlaceholderFile(m_Parameters[0]));
  }
}
