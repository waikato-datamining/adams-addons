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
 * GitSubMenu.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.git.GitSession;
import adams.core.git.GitSettingsHelper;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.ImageManager;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.menu.git.AbstractFlowEditorGitMenuItem;
import adams.gui.flow.menu.git.ResetSession;
import org.eclipse.jgit.api.Git;

import javax.swing.JMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Sub-menu for git actions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitSubMenu
  extends AbstractFlowEditorMenuItem {

  private static final long serialVersionUID = 8782622954036381820L;

  /** the sub-menu items. */
  protected List<AbstractFlowEditorGitMenuItem> m_MenuItems;

  /**
   * Returns whether the menu item is based on an action.
   *
   * @return		true if action-based
   */
  @Override
  public boolean hasAction() {
    return false;
  }

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return null;
  }

  /**
   * Returns whether the menu item is based on a submenu.
   *
   * @return		true if submenu-based
   */
  @Override
  public boolean hasSubMenu() {
    return GitSettingsHelper.getSingleton().getFlowEditorSupport();
  }

  /**
   * Adds the menuitem to the sub-menu and the list of managed menuitems.
   *
   * @param menuitem	the menuitem to add
   */
  protected void addMenuItem(AbstractFlowEditorGitMenuItem menuitem) {
    m_MenuItems.add(menuitem);
    if (menuitem.hasAction())
      m_SubMenu.add(menuitem.getAction());
    else if (menuitem.hasMenuItem())
      m_SubMenu.add(menuitem.getMenuItem());
    else if (menuitem.hasSubMenu())
      m_SubMenu.add(menuitem.getSubMenu());
  }

  /**
   * Creates the submenu to use.
   *
   * @return		the submenu
   */
  @Override
  protected JMenu newSubMenu() {
    AbstractFlowEditorGitMenuItem	menuitem;

    if (!GitSettingsHelper.getSingleton().getFlowEditorSupport())
      return null;

    m_SubMenu = new JMenu("Git");
    m_SubMenu.setIcon(ImageManager.getIcon("git"));
    m_MenuItems = new ArrayList<>();
    for (Class cls: ClassLister.getSingleton().getClasses(AbstractFlowEditorGitMenuItem.class)) {
      if (cls.equals(ResetSession.class))
	continue;
      try {
	menuitem = (AbstractFlowEditorGitMenuItem) cls.getDeclaredConstructor().newInstance();
	addMenuItem(menuitem);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate: " + Utils.classToString(cls), e);
      }
    }

    m_SubMenu.addSeparator();
    addMenuItem(new ResetSession());


    return m_SubMenu;
  }

  /**
   * Returns the name of the menu to list this item under.
   *
   * @return		the name of the menu
   */
  @Override
  public String getMenu() {
    return FlowEditorPanel.MENU_FILE;
  }

  /**
   * Updating the action/menuitem/submenu, based on the current status of the owner.
   */
  @Override
  public void update() {
    Git		git;

    if (!GitSettingsHelper.getSingleton().getFlowEditorSupport())
      return;

    m_SubMenu.setEnabled(
      (m_Owner != null)
	&& m_Owner.hasCurrentPanel());

    if ((m_Owner.getCurrentFile() != null)
	  && m_Owner.getCurrentFile().exists()
	  && !m_Owner.getCurrentFile().isDirectory()) {
      git = null;
      if (GitSession.getSingleton().isWithinRepo(m_Owner.getCurrentFile())) {
	git = GitSession.getSingleton().repoFor(m_Owner.getCurrentFile());
	if (git != null)
	  getLogger().info("git repo dir: " + git.getRepository().getWorkTree());
      }

      for (AbstractFlowEditorGitMenuItem menuitem : m_MenuItems) {
	menuitem.setOwner(m_Owner);
	menuitem.update(git);
      }
    }
  }
}
