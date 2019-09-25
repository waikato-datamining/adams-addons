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
 * RatsControlPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.ratcontrol;

import adams.flow.standalone.Rats;

import java.util.List;

/**
 * Control panel for {@link Rats} actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RatsControlPanel
  extends AbstractControlPanel<Rats> {

  /** for serialization. */
  private static final long serialVersionUID = 4516229240505598425L;

  /**
   * Returns the tool tip for the bulk action checkbox.
   *
   * @return		the tip text
   */
  protected String getCheckBoxBulkActionToolTipText() {
    return "Checks/unchecks all rat actors in this group";
  }

  /**
   * For custom actions when the bulk action checkbox is selected/unselected.
   */
  @Override
  protected void checkBoxBulkActionTrigger(boolean value) {
    List<AbstractControlPanel> panels;

    if (getOwner() == null)
      return;
    if (getGroup() == null)
      return;

    panels = getOwner().getControlPanelsPerRats().get(getGroup());
    for (AbstractControlPanel panel: panels) {
      if (panel instanceof RatControlPanel)
	panel.setChecked(value);
    }
  }
}
