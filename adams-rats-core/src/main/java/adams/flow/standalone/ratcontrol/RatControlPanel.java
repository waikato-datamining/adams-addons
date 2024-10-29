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
 * RatControlPanel.java
 * Copyright (C) 2018-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.ratcontrol;

import adams.flow.core.RatState;
import adams.flow.standalone.Rat;
import adams.gui.core.BaseButton;
import adams.gui.core.ImageManager;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Control panel for {@link Rat} actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RatControlPanel
  extends AbstractControlPanel<Rat> {

  /** for serialization. */
  private static final long serialVersionUID = 4516229240505598425L;

  /** the button for stopping/starting. */
  protected BaseButton m_ButtonStopStart;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ButtonStopStart = new BaseButton(ImageManager.getIcon("run.gif"));
    m_ButtonStopStart.addActionListener((ActionEvent e) -> stopOrStart());
    add(m_ButtonStopStart);
  }

  /**
   * Stops the rat.
   */
  public void stop() {
    SwingWorker worker;

    if (m_Actor == null)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_ButtonStopStart.setEnabled(false);
	if (m_Actor.isRunnableActive())
	  m_Actor.stopRunnable();
	updateButtons();
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Starts the rat.
   */
  public void start() {
    SwingWorker	worker;

    if (m_Actor == null)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_ButtonStopStart.setEnabled(false);
	if (!m_Actor.isRunnableActive()) {
	  if (m_Actor.getInitialState() == RatState.PAUSED)
	    m_Actor.setInitialState(RatState.RUNNING);
	  m_Actor.startRunnable();
	}
	updateButtons();
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Stops/starts the rat.
   */
  public void stopOrStart() {
    if (m_Actor == null)
      return;

    if (m_Actor.isRunnableActive())
      stop();
    else
      start();
  }

  /**
   * Updates the state of the buttons.
   */
  public void updateButtons() {
    m_ButtonPauseResume.setEnabled(!m_WrappedUp);
    m_ButtonStopStart.setEnabled(!m_WrappedUp);

    if (m_Actor == null)
      return;

    m_ButtonPauseResume.setEnabled(m_Actor.isRunnableActive() && !m_WrappedUp);
    if (m_Actor.isRunnableActive() && m_Actor.isPaused())
      m_ButtonPauseResume.setIcon(ImageManager.getIcon("resume.gif"));
    else
      m_ButtonPauseResume.setIcon(ImageManager.getIcon("pause.gif"));

    m_ButtonStopStart.setEnabled(!m_WrappedUp);
    if (m_Actor.isRunnableActive())
      m_ButtonStopStart.setIcon(ImageManager.getIcon("stop_blue.gif"));
    else
      m_ButtonStopStart.setIcon(ImageManager.getIcon("run.gif"));
  }

  /**
   * Sets the "stoppable" state of the control panel.
   *
   * @param value	true if to enable
   */
  public void setStoppable(boolean value) {
    m_ButtonStopStart.setVisible(value);
  }

  /**
   * Returns whether the "stoppable" state of the control panel is enabled.
   *
   * @return		true if enabled
   */
  public boolean isStoppable() {
    return m_ButtonStopStart.isVisible();
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

    panels = getOwner().getControlPanels();
    for (AbstractControlPanel panel: panels) {
      if ((panel instanceof RatsControlPanel) && panel.getGroup().equals(getGroup())) {
	panel.setChecked(false, false);
      }
    }
  }
}
