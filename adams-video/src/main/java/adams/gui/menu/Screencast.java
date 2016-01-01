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
 * Screencast.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.MenuBarProvider;

import javax.swing.JMenuBar;

/**
 * Allows recording of screencasts.
 *
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11391 $
 */
public class Screencast
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = -5728838990494794944L;

  /**
   * Derived ScreencastPanel, exposes the menubar to ADAMS.
   *
   * @author  FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 11391 $
   */
  public static class ScreencastPanel
    extends com.github.fracpete.screencast4j.gui.ScreencastPanel
    implements MenuBarProvider {

    private static final long serialVersionUID = -3667175268107637920L;

    /**
     * Creates a menu bar (singleton per panel object). Can be used in frames.
     *
     * @return		the menu bar
     */
    @Override
    public JMenuBar getMenuBar() {
      return super.getMenuBar();
    }
  }

  /**
   * Initializes the menu item with no owner.
   */
  public Screencast() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Screencast(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "screencast.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    ScreencastPanel panel = new ScreencastPanel();
    createChildFrame(panel, 600, 400);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Screencast";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_TOOLS;
  }
}