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
 * HeatmapPanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.HeatmapToBufferedImage;
import adams.data.conversion.HeatmapToSpreadSheet;
import adams.data.heatmap.Heatmap;
import adams.data.heatmap.HeatmapStatistic;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.AbstractHeatmapReader;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.InformativeStatistic;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.container.NotesFactory;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for displaying a single heatmap.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 1897625268125110563L;

  /** the setup for the panel. */
  protected static Properties m_Properties;

  /** the heatmap on display. */
  protected Heatmap m_Heatmap;

  /** the panel for displaying the heatmap as image. */
  protected ImagePanel m_HeatmapImage;

  /** the table with the heatmap as spreadsheet. */
  protected SpreadSheetTable m_HeatmapTable;

  /** the report of the heatmap. */
  protected ReportFactory.Table m_ReportTable;

  /** the search panel for the heatmap report. */
  protected SearchPanel m_SearchPanel;

  /** the split pane for image/spreadsheet and report. */
  protected BaseSplitPane m_SplitPane;

  /** the tabbed pane for the image and spreadsheet view. */
  protected BaseTabbedPane m_TabbedPane;

  /** the owner. */
  protected HeatmapViewerPanel m_Owner;

  /** the reader that was used for reading in the heatmap. */
  protected AbstractHeatmapReader m_Reader;

  /** the centroid image overlay. */
  protected CentroidOverlay m_CentroidOverlay;
  
  /** the color generator to use. */
  protected AbstractColorGradientGenerator m_ColorGenerator;

  /** the last ArrayHistogram setup that was used. */
  protected ArrayHistogram m_LastArrayHistogram;

  /**
   * Initializes the panel.
   *
   * @param owner	the owner of this panel
   */
  public HeatmapPanel(HeatmapViewerPanel owner) {
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

    props                = getProperties();
    m_Owner              = null;
    m_Heatmap            = new Heatmap(0, 0);
    m_Reader             = null;
    m_ColorGenerator     = AbstractColorGradientGenerator.forCommandLine(props.getProperty("Image.GradientColorGenerator", new BiColorGenerator().toCommandLine()));
    m_LastArrayHistogram = null;
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

    m_SplitPane = new BaseSplitPane();
    m_SplitPane.setDividerLocation(props.getInteger("Panel.DividerLocation", 600));
    add(m_SplitPane, BorderLayout.CENTER);

    m_HeatmapImage = new ImagePanel();
    m_HeatmapTable = null;

    if (props.getBoolean("SpreadSheet.Show", true)) {
      m_TabbedPane = new BaseTabbedPane();
      m_SplitPane.setLeftComponent(m_TabbedPane);

      m_TabbedPane.addTab("Image", m_HeatmapImage);

      m_HeatmapTable = new SpreadSheetTable(new SpreadSheet());
      m_HeatmapTable.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));
      m_TabbedPane.addTab("Raw", new BaseScrollPane(m_HeatmapTable));
    }
    else {
      m_SplitPane.setLeftComponent(m_HeatmapImage);
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

    m_CentroidOverlay = new CentroidOverlay();
    m_CentroidOverlay.setHeatmapPanel(this);
    m_CentroidOverlay.setEnabled(false);
    m_HeatmapImage.addImageOverlay(m_CentroidOverlay);
  }

  /**
   * Returns the owner of this panel.
   *
   * @return		the owner, null if none set
   */
  public HeatmapViewerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Sets the heatmap to display.
   *
   * @param value	the heatmap to display
   */
  public void setHeatmap(Heatmap value) {
    String			result;
    Properties			props;
    HeatmapToBufferedImage	hm2bi;
    HeatmapToSpreadSheet	hm2ss;
    StringBuilder		errors;
    String			error;

    m_Heatmap = value.getClone();
    props     = getProperties();
    errors    = new StringBuilder();

    // image
    hm2bi = new HeatmapToBufferedImage();
    hm2bi.setInput(m_Heatmap);
    hm2bi.setGenerator(m_ColorGenerator);
    result = hm2bi.convert();
    if (result != null) {
      error = "Failed to generate image: " + result;
      if (errors.length() > 0)
	errors.append("\n");
      errors.append(error);
      System.err.println(error);
      m_HeatmapImage.setCurrentImage((BufferedImage) null);
    }
    else {
      m_HeatmapImage.setCurrentImage(((AbstractImageContainer) hm2bi.getOutput()).toBufferedImage());
      m_HeatmapImage.setScale(props.getDouble("Image.Scale", -1.0));
    }

    // spreadsheet
    if (m_HeatmapTable != null) {
      hm2ss = new HeatmapToSpreadSheet();
      hm2ss.setInput(m_Heatmap);
      result = hm2ss.convert();
      if (result != null) {
	error = "Failed to generate spreadsheet: " + result;
	if (errors.length() > 0)
	  errors.append("\n");
	errors.append(error);
	System.err.println(error);
	m_HeatmapTable.setModel(new SpreadSheetTableModel());
      }
      else {
	m_HeatmapTable.setModel(new SpreadSheetTableModel((SpreadSheet) hm2ss.getOutput()));
      }
      m_HeatmapTable.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));
    }

    // report
    m_ReportTable.setModel(new ReportFactory.Model(m_Heatmap.getReport()));

    // centroid overlay
    m_CentroidOverlay.setHeatmapPanel(this);

    // display errors in owner's statusbar
    if ((errors.length() > 0) && (m_Owner != null))
      m_Owner.showStatus(errors.toString());
  }

  /**
   * The current heatmap on display.
   *
   * @return		the heatmap on display
   */
  public Heatmap getHeatmap() {
    return m_Heatmap;
  }

  /**
   * Sets the heatmap reader that was used for loading this heatmap.
   * Used for reloading.
   *
   * @param value	the reader
   */
  public void setReader(AbstractHeatmapReader value) {
    m_Reader = (AbstractHeatmapReader) value.shallowCopy(true);
  }

  /**
   * Returns the heatmap reader that was used for loading this heatmap.
   *
   * @return		the reader, null if loaded from database
   */
  public AbstractHeatmapReader getReader() {
    return m_Reader;
  }

  /**
   * Sets the generator for the color gradient.
   * 
   * @param value	the generator
   */
  public void setColorGenerator(AbstractColorGradientGenerator value) {
    m_ColorGenerator = value;
    reload();
  }
  
  /**
   * Returns the generator for the color gradient.
   * 
   * @return		the generator
   */
  public AbstractColorGradientGenerator getColorGenerator() {
    return m_ColorGenerator;
  }
  
  /**
   * Returns the database ID or filename as title.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Heatmap.getID();
  }

  /**
   * Returns the underlying image panel
   *
   * @return		the panel
   */
  public ImagePanel getImagePanel() {
    return m_HeatmapImage;
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
    report = m_Heatmap.getReport();

    // filename?
    if (!result && report.hasValue(Heatmap.FIELD_FILENAME)) {
      file   = new PlaceholderFile(report.getStringValue(Heatmap.FIELD_FILENAME));
      result = file.exists() && (m_Reader != null);
    }

    return result;
  }

  /**
   * Reloads the data, if possible.
   *
   * @return		true if successfully reloaded
   */
  public boolean reload() {
    Report			report;
    PlaceholderFile		file;
    AbstractHeatmapReader	reader;
    List<Heatmap>		maps;
    double			scale;

    report = m_Heatmap.getReport();

    // filename?
    if ((getOwner() != null) && report.hasValue(Heatmap.FIELD_FILENAME) && (m_Reader != null)) {
      file   = new PlaceholderFile(report.getStringValue(Heatmap.FIELD_FILENAME));
      reader = (AbstractHeatmapReader) m_Reader.shallowCopy(true);
      scale  = getImagePanel().getScale();
      reader.setInput(file);
      maps   = reader.read();
      if (maps.size() >= 1) {
	setHeatmap(maps.get(0));
	getImagePanel().setScale(scale);
	reader.cleanUp();
	return true;
      }
      reader.cleanUp();
    }

    return false;
  }

  /**
   * Displays the heatmap as spreadsheet.
   */
  public void showSpreadsheet() {
    BaseDialog			dialog;
    SpreadSheetTable		table;
    HeatmapToSpreadSheet	convert;
    String			result;
    Properties			props;

    table   = new SpreadSheetTable(new SpreadSheetTableModel());
    convert = new HeatmapToSpreadSheet();
    convert.setInput(getHeatmap());
    result  = convert.convert();
    if (result != null) {
      GUIHelper.showErrorMessage(this, "Failed to generate spreadsheet: " + result);
      return;
    }
    props = getProperties();
    table.setModel(new SpreadSheetTableModel((SpreadSheet) convert.getOutput()));
    table.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));

    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new BaseDialog(getParentFrame(), false);
    dialog.setTitle("Heatmap #" + getHeatmap().getID());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(new BaseScrollPane(table), BorderLayout.CENTER);
    dialog.setSize(
	props.getInteger("View.SpreadSheet.Width", 800),
	props.getInteger("View.SpreadSheet.Height", 600));
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays the heatmap as histogram.
   */
  public void showHistogram() {
    BaseDialog			dialog;
    HistogramPanel		panel;
    GenericObjectEditorDialog	goe;

    // display options dialog for histogram
    if (m_LastArrayHistogram == null)
      m_LastArrayHistogram = new ArrayHistogram();

    if (getParentDialog() != null)
      goe = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      goe = new GenericObjectEditorDialog(getParentFrame(), true);
    goe.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    goe.getGOEEditor().setClassType(ArrayHistogram.class);
    goe.getGOEEditor().setCanChangeClassInDialog(false);
    goe.getGOEEditor().setValue(m_LastArrayHistogram);
    goe.setLocationRelativeTo(this);
    goe.setVisible(true);
    if (goe.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_LastArrayHistogram = (ArrayHistogram) goe.getCurrent();

    panel = new HistogramPanel();
    panel.setData(getHeatmap());
    panel.setArrayHistogram((ArrayHistogram) m_LastArrayHistogram.shallowCopy());

    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new BaseDialog(getParentFrame(), false);
    dialog.setTitle("Heatmap #" + getHeatmap().getID());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(600, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays somes stats on the heatmap.
   */
  public void showStatistics() {
    HeatmapStatistic			stats;
    InformativeStatisticFactory.Dialog	dialog;
    List<InformativeStatistic>	statsList;

    stats     = new HeatmapStatistic(getHeatmap());
    statsList = new ArrayList<InformativeStatistic>();
    statsList.add(stats);

    if (getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = InformativeStatisticFactory.getDialog(getParentFrame(), false);
    dialog.setStatistics(statsList);
    dialog.setTitle("Heatmap statistics");
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays the notes of the heatmap.
   */
  public void showNotes() {
    NotesFactory.Dialog		dialog;
    List<HeatmapContainer>	data;

    if (getParentDialog() != null)
      dialog = NotesFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = NotesFactory.getDialog(getParentFrame(), false);
    data = new ArrayList<HeatmapContainer>();
    data.add(new HeatmapContainer(null, getHeatmap()));
    dialog.setData(data);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
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
   * Sets whether the centroid overlay is painted as well.
   *
   * @param value	if true the centroid is painted
   */
  public void setShowCentroid(boolean value) {
    m_CentroidOverlay.setEnabled(value);
    m_HeatmapImage.repaint();
  }

  /**
   * Returns whether the centroid overlay is painted as well.
   *
   * @return		true if the centroid is painted
   */
  public boolean getShowCentroid() {
    return m_CentroidOverlay.isEnabled();
  }

  /**
   * Returns the properties for this panel.
   *
   * @return		the properties file for this panel
   */
  public synchronized Properties getProperties() {
    String 	props;

    if (m_Properties == null) {
      try {
	props = getClass().getName().replaceAll("\\.", "/") + ".props";
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
    m_HeatmapImage.setScale(zoom);
  }
  
  /**
   * Returns the zoom factor (0-16).
   * 
   * @return		the zoom factor
   */
  public double getZoom() {
    return m_HeatmapImage.getScale();
  }
}
