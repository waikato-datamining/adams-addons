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
 * AbstractCurrentTrailFilterWithGOE.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.trail.plugins;

import adams.data.trail.Trail;
import adams.gui.goe.GenericObjectEditorDialog;

import java.awt.Dialog.ModalityType;

/**
 * Ancestor for trail filters that process the current trail with a setup
 * obtained from a {@link GenericObjectEditorDialog}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCurrentTrailFilterWithGOE
  extends AbstractCurrentTrailFilter {
  
  /** for serialization. */
  private static final long serialVersionUID = 5533833826590494572L;

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  protected abstract Class getEditorType();
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  protected abstract Object getDefaultValue();

  /**
   * Returns whether the class can be changed in the GOE.
   * 
   * @return		true if class can be changed by the user
   */
  protected boolean getCanChangeClassInDialog() {
    return true;
  }
  
  /**
   * Performs the actual filtering of the trail.
   *
   * @param trail	the trail to filter
   * @return		the processed trail
   */
  protected abstract Trail doFilter(Trail trail);
  
  /**
   * Filters the trail.
   *
   * @param trail	the trail to filter
   * @return		the processed trail
   */
  @Override
  protected Trail filter(Trail trail) {
    Trail			result;
    GenericObjectEditorDialog	dialog;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.setTitle(getCaption());
    dialog.getGOEEditor().setClassType(getEditorType());
    dialog.getGOEEditor().setCanChangeClassInDialog(getCanChangeClassInDialog());
    if (hasLastSetup())
      dialog.setCurrent(getLastSetup());
    else
      dialog.setCurrent(getDefaultValue());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      m_CanceledByUser = true;
      return result;
    }

    setLastSetup(dialog.getCurrent());
    
    result = doFilter(trail);

    return result;
  }

}
