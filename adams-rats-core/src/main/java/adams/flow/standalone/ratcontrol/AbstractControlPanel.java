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
 * AbstractControlPanel.java
 * Copyright (C) 2018-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.ratcontrol;

import adams.core.Pausable;
import adams.flow.core.Actor;
import adams.flow.standalone.RatControl;
import adams.flow.standalone.Rats;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;

import javax.swing.SwingWorker;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Ancestor for control panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractControlPanel<T extends Actor & Pausable>
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -5965060223206287867L;

  /** the owner. */
  protected RatControl m_Owner;

  /** the rats group this belongs to. */
  protected Rats m_Group;

  /** the actor to manage. */
  protected T m_Actor;

  /** the checkbox for bulk actions. */
  protected BaseCheckBox m_CheckBoxBulkAction;

  /** the button for pausing/resuming. */
  protected BaseButton m_ButtonPauseResume;

  /** whether to skip bulk triggers. */
  protected boolean m_SkipBulkActionTrigger;

  /** whether the flow has wrapped up. */
  protected boolean m_WrappedUp;

  /**
   * Initializes the member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_WrappedUp = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

    m_CheckBoxBulkAction = new BaseCheckBox();
    m_CheckBoxBulkAction.setVisible(false);
    m_CheckBoxBulkAction.addActionListener((ActionEvent e) -> {
      if (!m_SkipBulkActionTrigger)
        checkBoxBulkActionTrigger(m_CheckBoxBulkAction.isSelected());
    });
    m_CheckBoxBulkAction.setToolTipText(getCheckBoxBulkActionToolTipText());
    add(m_CheckBoxBulkAction);

    m_ButtonPauseResume = new BaseButton(ImageManager.getIcon("pause.gif"));
    m_ButtonPauseResume.addActionListener((ActionEvent e) -> pauseOrResume());
    add(m_ButtonPauseResume);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Sets the RatControl actor this control belongs to.
   *
   * @param value	the owner
   */
  public void setOwner(RatControl value) {
    m_Owner = value;
  }

  /**
   * Returns the RatControl actor this control belongs to.
   *
   * @return		the owner
   */
  public RatControl getOwner() {
    return m_Owner;
  }

  /**
   * Sets the Rats groups this control belongs to.
   *
   * @param value	the group
   */
  public void setGroup(Rats value) {
    m_Group = value;
  }

  /**
   * Returns the Rats group this control belongs to.
   *
   * @return		the group
   */
  public Rats getGroup() {
    return m_Group;
  }

  /**
   * Sets the actor to manage.
   *
   * @param value	the actor
   */
  public void setActor(T value) {
    m_Actor = value;
    updateButtons();
  }

  /**
   * Returns the actor in use.
   *
   * @return		the actor
   */
  public T getActor() {
    return m_Actor;
  }

  /**
   * Returns the tool tip for the bulk action checkbox.
   * <br>
   * Default implementation is null.
   *
   * @return		the tip text
   */
  protected String getCheckBoxBulkActionToolTipText() {
    return null;
  }

  /**
   * For custom actions when the bulk action checkbox is selected/unselected.
   * <br>
   * Default implementation does nothing.
   *
   * @param value	whether to check or uncheck
   */
  protected void checkBoxBulkActionTrigger(boolean value) {
  }

  /**
   * Checks whether bulk action is enabled.
   *
   * @return		true if enabled
   */
  public boolean isBulkActionEnabled() {
    return m_CheckBoxBulkAction.isVisible();
  }

  /**
   * Sets whether bulk action is enabled.
   *
   * @param value	true if enabled
   */
  public void setBulkActionEnabled(boolean value) {
    m_CheckBoxBulkAction.setVisible(value);
  }

  /**
   * Checks whether bulk action checkbox is selected.
   * Only works if {@link #isBulkActionEnabled()}.
   *
   * @return		true if checked
   * @see		#isBulkActionEnabled()
   */
  public boolean isChecked() {
    return isBulkActionEnabled() && m_CheckBoxBulkAction.isSelected();
  }

  /**
   * Sets the selected state of the bulk action checkbox.
   * Only works if {@link #isBulkActionEnabled()}.
   *
   * @param value 	true if to check
   * @see		#isBulkActionEnabled()
   */
  public void setChecked(boolean value) {
    setChecked(value, true);
  }

  /**
   * Sets the selected state of the bulk action checkbox.
   * Only works if {@link #isBulkActionEnabled()}.
   *
   * @param value 	true if to check
   * @see		#isBulkActionEnabled()
   */
  public void setChecked(boolean value, boolean trigger) {
    if (isBulkActionEnabled()) {
      m_SkipBulkActionTrigger = !trigger;
      m_CheckBoxBulkAction.setSelected(value);
      m_SkipBulkActionTrigger = false;
    }
  }

  /**
   * Pauses the rat.
   */
  public void pause() {
    SwingWorker worker;

    if (m_Actor == null)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_Actor.pauseExecution();
	updateButtons();
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Resumes the rat.
   */
  public void resume() {
    SwingWorker	worker;

    if (m_Actor == null)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_Actor.resumeExecution();
	updateButtons();
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Pauses/resumes the rat.
   */
  public void pauseOrResume() {
    if (m_Actor == null)
      return;

    if (m_Actor.isPaused())
      resume();
    else
      pause();
  }

  /**
   * Updates the state of the buttons.
   */
  public void updateButtons() {
    m_ButtonPauseResume.setEnabled(!m_WrappedUp);

    if (m_Actor == null)
      return;

    if (m_Actor.isPaused())
      m_ButtonPauseResume.setIcon(ImageManager.getIcon("resume.gif"));
    else
      m_ButtonPauseResume.setIcon(ImageManager.getIcon("pause.gif"));
  }

  /**
   * Sets the "pauseable" state of the control panel.
   *
   * @param value	true if to enable
   */
  public void setPausable(boolean value) {
    m_ButtonPauseResume.setVisible(value);
  }

  /**
   * Returns whether the control panel is enabled.
   *
   * @return		true if enabled
   */
  public boolean isPausable() {
    return m_ButtonPauseResume.isVisible();
  }

  /**
   * Disables the button.
   */
  public void wrapUp() {
    SwingWorker	worker;

    m_WrappedUp = true;
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	updateButtons();
	return null;
      }
    };
    worker.execute();
  }
}
