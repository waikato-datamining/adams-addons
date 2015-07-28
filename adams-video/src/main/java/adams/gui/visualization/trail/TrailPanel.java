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
 * TrailPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.trail;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.TrailToSpreadSheet;
import adams.data.io.input.AbstractTrailReader;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Trail;
import adams.gui.core.BaseLogPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.trail.overlay.AbstractTrailOverlay;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

/**
 * Panel for displaying a single trail.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrailPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 1897625268125110563L;

  /** the setup for the panel. */
  protected static Properties m_Properties;

  /** the trail on display. */
  protected Trail m_Trail;

  /** the panel for displaying the trail as image. */
  protected ImagePanel m_TrailImage;

  /** the table with the trail as spreadsheet. */
  protected SpreadSheetTable m_TrailTable;

  /** the report of the trail. */
  protected ReportFactory.Table m_ReportTable;

  /** the search panel for the trail report. */
  protected SearchPanel m_SearchPanel;

  /** the tabbed pane for image/report and log. */
  protected BaseTabbedPane m_LogTabbedPane;

  /** the split pane for image/spreadsheet and report. */
  protected BaseSplitPane m_SplitPane;

  /** the tabbed pane for the image and spreadsheet view. */
  protected BaseTabbedPane m_TabbedPane;

  /** the owner. */
  protected TrailViewerPanel m_Owner;

  /** the reader that was used for reading in the trail. */
  protected AbstractTrailReader m_Reader;

  /** the log panel. */
  protected BaseLogPanel m_PanelLog;

  /**
   * Initializes the panel.
   *
   * @param owner	the owner of this panel
   */
  public TrailPanel(TrailViewerPanel owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Properties	props;

    super.initialize();

    props    = getProperties();
    m_Owner  = null;
    m_Trail  = new Trail();
    m_Reader = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties	props;
    JPanel	panel;

    super.initGUI();

    props = getProperties();

    setLayout(new BorderLayout());

    m_LogTabbedPane = new BaseTabbedPane();
    m_LogTabbedPane.setTabPlacement(BaseTabbedPane.BOTTOM);
    add(m_LogTabbedPane, BorderLayout.CENTER);

    m_SplitPane = new BaseSplitPane();
    m_SplitPane.setDividerLocation(props.getInteger("Panel.DividerLocation", 600));
    m_LogTabbedPane.addTab("Data", m_SplitPane);

    m_TrailImage = new ImagePanel();
    m_TrailTable = null;

    if (props.getBoolean("SpreadSheet.Show", true)) {
      m_TabbedPane = new BaseTabbedPane();
      m_SplitPane.setLeftComponent(m_TabbedPane);

      m_TabbedPane.addTab("Image", m_TrailImage);

      m_TrailTable = new SpreadSheetTable(new SpreadSheet());
      m_TrailTable.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));
      m_TabbedPane.addTab("Raw", new BaseScrollPane(m_TrailTable));
    }
    else {
      m_SplitPane.setLeftComponent(m_TrailImage);
    }

    m_ReportTable = new ReportFactory.Table();

    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true, "_Search", true, null);
    m_SearchPanel.setMinimumChars(2);
    m_SearchPanel.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	search(m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
      }
    });

    panel = new JPanel(new BorderLayout());
    panel.add(new BaseScrollPane(m_ReportTable), BorderLayout.CENTER);
    panel.add(m_SearchPanel, BorderLayout.SOUTH);
    m_SplitPane.setRightComponent(panel);

    m_PanelLog = new BaseLogPanel();
    m_PanelLog.setRows(5);
    m_PanelLog.setColumns(80);
    m_LogTabbedPane.addTab("Log", m_PanelLog);
  }

  /**
   * Returns the owner of this panel.
   *
   * @return		the owner, null if none set
   */
  public TrailViewerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Regenerates the image of the current trail and redisplays it.
   *
   * @return		null if everything OK, otherwiser error message
   */
  protected String refresh() {
    if (m_Trail.getWidth() == 0)
      return null;

    // TODO

    return null;
  }

  /**
   * Sets the trail to display.
   *
   * @param value	the trail to display
   */
  public void setTrail(Trail value) {
    String			result;
    Properties			props;
    TrailToSpreadSheet	hm2ss;
    StringBuilder		errors;
    String			error;

    if (value == null)
      return;

    m_Trail = (Trail) value.getClone();
    props   = getProperties();
    errors  = new StringBuilder();

    // image
    error = refresh();
    if (error != null)
      errors.append(error);

    // spreadsheet
    if (m_TrailTable != null) {
      hm2ss = new TrailToSpreadSheet();
      hm2ss.setInput(m_Trail);
      result = hm2ss.convert();
      if (result != null) {
	error = "Failed to generate spreadsheet: " + result;
	if (errors.length() > 0)
	  errors.append("\n");
	errors.append(error);
	System.err.println(error);
	m_TrailTable.setModel(new SpreadSheetTableModel());
      }
      else {
	m_TrailTable.setModel(new SpreadSheetTableModel((SpreadSheet) hm2ss.getOutput()));
      }
      m_TrailTable.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));
    }

    // report
    m_ReportTable.setModel(new ReportFactory.Model(m_Trail.getReport()));

    // display errors in owner's statusbar
    if ((errors.length() > 0) && (m_Owner != null))
      m_Owner.showStatus(errors.toString());
  }

  /**
   * The current trail on display.
   *
   * @return		the trail on display
   */
  public Trail getTrail() {
    return m_Trail;
  }

  /**
   * Sets the trail reader that was used for loading this trail.
   * Used for reloading.
   *
   * @param value	the reader
   */
  public void setReader(AbstractTrailReader value) {
    m_Reader = (AbstractTrailReader) value.shallowCopy(true);
  }

  /**
   * Returns the trail reader that was used for loading this trail.
   *
   * @return		the reader, null if loaded from database
   */
  public AbstractTrailReader getReader() {
    return m_Reader;
  }

  /**
   * Adds the trail overlay.
   *
   * @param overlay     the overlay to add
   */
  public void addOverlay(AbstractTrailOverlay overlay) {
    overlay = (AbstractTrailOverlay) overlay.shallowCopy();
    overlay.setTrailPanel(this);
    getImagePanel().addImageOverlay(overlay);
  }

  /**
   * Removes all overlays.
   */
  public void removeOverlays() {
    getImagePanel().clearImageOverlays();
  }

  /**
   * Returns the database ID or filename as title.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Trail.getID();
  }

  /**
   * Returns the underlying image panel
   *
   * @return		the panel
   */
  public ImagePanel getImagePanel() {
    return m_TrailImage;
  }

  /**
   * Checks whether this panel can be reloaded.
   *
   * @return		true if reload is possible
   */
  public boolean canReload() {
    boolean	result;
    Report	report;
    File	file;

    result = false;
    report = m_Trail.getReport();

    // filename?
    // TODO

    return result;
  }

  /**
   * Reloads the data, if possible.
   *
   * @return		true if successfully reloaded
   */
  public boolean reload() {
    Report		report;
    PlaceholderFile	file;
    AbstractTrailReader	reader;
    List<Trail>		maps;
    double		scale;

    report = m_Trail.getReport();

    // filename?
    // TODO

    return false;
  }

  /**
   * Performs a search.
   *
   * @param text	the search text
   * @param isRegExp	whether the search text is a regular expression
   */
  public void search(String text, boolean isRegExp) {
    m_ReportTable.search(text, isRegExp);
  }

  /**
   * Sets whether to display the search panel or not.
   *
   * @param value	if true then the search panel is displayed
   */
  public void setSearchPanelVisible(boolean value) {
    m_SearchPanel.setVisible(value);
  }

  /**
   * Returns whether the search panel is visible.
   *
   * @return		true if the search panel is visible
   */
  public boolean isSearchPanelVisible() {
    return m_SearchPanel.isVisible();
  }

  /**
   * Returns the properties for this panel.
   *
   * @return		the properties file for this panel
   */
  public static synchronized Properties getProperties() {
    String 	props;

    if (m_Properties == null) {
      try {
	props = TrailPanel.class.getName().replaceAll("\\.", "/") + ".props";
	m_Properties = Properties.read(props);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
  
  /**
   * Sets the zoom factor (0-16). Use -1 to fit inside panel.
   *
   * @param zoom	the zoom factor
   */
  public void setZoom(double zoom) {
    m_TrailImage.setScale(zoom);
  }
  
  /**
   * Returns the zoom factor (0-16).
   * 
   * @return		the zoom factor
   */
  public double getZoom() {
    return m_TrailImage.getScale();
  }

  /**
   * Logs the message in the log panel.
   *
   * @param msg		the log message
   */
  public void log(String msg) {
    m_PanelLog.append(msg);
  }
}
