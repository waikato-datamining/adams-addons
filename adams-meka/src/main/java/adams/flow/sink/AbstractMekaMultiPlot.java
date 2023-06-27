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
 * AbstractMekaMultiPlot.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.flow.container.MekaResultContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import meka.core.Result;
import weka.core.Instances;
import weka.gui.visualize.ThresholdVisualizePanel;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.util.logging.Level;

/**
 * Ancestor for plots that display multiple plots in a tabbed pane.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMekaMultiPlot
  extends AbstractMekaThresholdVisualizePanelPlot
  implements DisplayPanelProvider {

  private static final long serialVersionUID = -8227153847798098749L;

  /** the tabbed pane for the plots. */
  protected BaseTabbedPane m_TabbedPane;

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result = new BasePanel(new BorderLayout());
    m_TabbedPane = new BaseTabbedPane();
    result.add(m_TabbedPane, BorderLayout.CENTER);

    return result;
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_TabbedPane != null)
      m_TabbedPane.removeAll();
  }

  /**
   * Returns the name of the measurement to retrieve from the {@link Result}
   * data structure.
   *
   * @return		the name of the measurement
   */
  protected abstract String getMeasurementName();

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    Result		result;
    Instances[] 	curves;

    m_TabbedPane.removeAll();
    if (token == null)
      return;

    if (token.getPayload() instanceof Result)
      result = (Result) token.getPayload();
    else
      result = (Result) ((MekaResultContainer) token.getPayload()).getValue(MekaResultContainer.VALUE_RESULT);

    curves = (Instances[]) result.getMeasurement(getMeasurementName());
    for (int i = 0; i < curves.length; i++) {
      try {
	ThresholdVisualizePanel panel = createPanel(curves[i], "Label " + i);
	m_TabbedPane.addTab("" + i, panel);
      }
      catch (Exception ex) {
	getLogger().log(Level.SEVERE, "Failed to create plot for label " + i, ex);
      }
    }
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected BaseTabbedPane m_TabbedPane;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_TabbedPane = new BaseTabbedPane();
	add(m_TabbedPane, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_TabbedPane.removeAll();
	if (token == null)
	  return;
	Result result;
	if (token.getPayload() instanceof Result)
	  result = (Result) token.getPayload();
	else
	  result = (Result) ((MekaResultContainer) token.getPayload()).getValue(MekaResultContainer.VALUE_RESULT);
	Instances[] curves = (Instances[]) result.getMeasurement(getMeasurementName());
	for (int i = 0; i < curves.length; i++) {
	  try {
	    ThresholdVisualizePanel panel = createPanel(curves[i], "Label " + i);
	    m_TabbedPane.addTab("" + i, panel);
	  }
	  catch (Exception ex) {
	    getLogger().log(Level.SEVERE, "Failed to create plot for label " + i, ex);
	  }
	}
      }
      @Override
      public void clearPanel() {
	m_TabbedPane.removeAll();
      }
      @Override
      public void cleanUp() {
	m_TabbedPane.removeAll();
      }
      @Override
      public JComponent supplyComponent() {
	return m_TabbedPane;
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }
}
