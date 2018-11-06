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
 * CNTKFasterRcnnRois.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.io.FileUtils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchableBaseList;
import adams.gui.event.SearchEvent;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Displays the ROIs in images for CNTK FastRCNN datasets.<br>
 * Requires a pair of files '.*_img_file.*' and '.*_roi_file.*'.<br>
 * The images listed in '.*_img_file.*' must be present as well.<br>
 * The file with the class mapping ('class_map.txt') is optional, but helps when displaying the classes in the overlay.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKFasterRcnnRois
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /**
   * Panel for displaying ROIs.
   */
  public static class RoiPanel
    extends BasePanel
    implements LoggingSupporter  {

    private static final long serialVersionUID = 5600226296229769527L;

    /** the logger. */
    protected Logger m_Logger;

    /** the splitpane. */
    protected BaseSplitPane m_SplitPane;

    /** the list with the image indices. */
    protected SearchableBaseList m_ListIndices;

    /** the list model with the indices. */
    protected DefaultListModel<String> m_ModelIndices;

    /** the search panel. */
    protected SearchPanel m_PanelSearch;

    /** the panel with the overlayed image. */
    protected ImagePanel m_PanelImage;

    /** the lookup table for the images. */
    protected Map<String,File> m_MapImages;

    /** the lookup table for the rois. */
    protected Map<String,Report> m_MapROIs;

    /**
     * Initializes the panel.
     *
     * @param owner	for obtaining the logger
     */
    public RoiPanel(CNTKFasterRcnnRois owner) {
      super();
      m_Logger = owner.getLogger();
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_MapImages = new HashMap<>();
      m_MapROIs   = new HashMap<>();
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      ObjectLocationsOverlayFromReport	overlay;
      JPanel 				panelIndices;

      super.initGUI();

      setLayout(new BorderLayout());

      m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
      m_SplitPane.setResizeWeight(0.0);
      m_SplitPane.setDividerLocation(100);
      add(m_SplitPane, BorderLayout.CENTER);

      panelIndices = new JPanel(new BorderLayout());
      m_SplitPane.setLeftComponent(panelIndices);

      m_ModelIndices = new DefaultListModel<>();
      m_ListIndices  = new SearchableBaseList(m_ModelIndices);
      m_ListIndices.addListSelectionListener((ListSelectionEvent e) -> updateImage());
      panelIndices.add(new BaseScrollPane(m_ListIndices), BorderLayout.CENTER);

      m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false, "", true, "");
      m_PanelSearch.addSearchListener((SearchEvent e) -> search(e.getParameters().getSearchString()));
      panelIndices.add(m_PanelSearch, BorderLayout.SOUTH);

      overlay = new ObjectLocationsOverlayFromReport();
      overlay.setLabelFormat("#. $");
      overlay.setUseColorsPerType(true);
      m_PanelImage = new ImagePanel();
      m_PanelImage.addImageOverlay(overlay);
      m_PanelImage.setScale(-1);
      if (m_PanelImage.isUndoSupported())
        m_PanelImage.getUndo().setEnabled(false);
      m_SplitPane.setRightComponent(m_PanelImage);
    }

    /**
     * Returns the logger in use.
     *
     * @return		the logger
     */
    public synchronized Logger getLogger() {
      return m_Logger;
    }

    /**
     * Returns whether logging is enabled.
     *
     * @return		true if at least {@link Level#INFO}
     */
    public boolean isLoggingEnabled() {
      return LoggingHelper.isAtLeast(getLogger(), Level.INFO);
    }

    /**
     * Performs a search with the given string.
     *
     * @param s		the string to use
     */
    protected void search(String s) {
      m_ListIndices.search(s, false);
    }

    /**
     * Updates the selected image.
     */
    protected void updateImage() {
      String	key;

      m_PanelImage.clear();
      m_PanelImage.setAdditionalProperties(null);
      if (m_ListIndices.getSelectedIndex() == -1)
        return;

      key = m_ListIndices.getSelectedValue().toString().split(" - ")[0];
      if (!m_MapImages.containsKey(key) || !m_MapROIs.containsKey(key))
        return;

      m_PanelImage.load(m_MapImages.get(key));
      m_PanelImage.setAdditionalProperties(m_MapROIs.get(key));
    }

    /**
     * Reads the specified files and updates the display.
     *
     * @param imgFile	the file with the image paths
     * @param roiFile	the file with ROIs per image
     * @param classMapFile 	the file with the classes (optional)
     */
    public void display(File imgFile, File roiFile, File classMapFile) {
      List<String> 		lines;
      List<String>		keys;
      List<String>		keysLong;
      String[]			parts;
      String[]			rois;
      int			i;
      LocatedObject		obj;
      LocatedObjects		objs;
      int			x0;
      int			y0;
      int			x1;
      int			y1;
      Map<String,String>	classes;

      m_MapImages.clear();
      m_MapROIs.clear();

      // classes?
      classes = new HashMap<>();
      if (classMapFile != null) {
        lines = FileUtils.loadFromFile(classMapFile);
	if (lines == null) {
	  getLogger().severe("Failed to read: " + classMapFile);
	  return;
	}
	for (String line: lines) {
	  if (line.trim().length() == 0)
	    continue;
	  parts = line.split("\t");
	  if (parts.length == 2)
	    classes.put(parts[1].trim(), parts[0].trim());
	}
      }

      // read images
      if (isLoggingEnabled())
        getLogger().info("Reading img file: " + imgFile);
      keys     = new ArrayList<>();
      keysLong = new ArrayList<>();
      lines    = FileUtils.loadFromFile(imgFile);
      if (lines == null) {
	getLogger().severe("Failed to read: " + imgFile);
	return;
      }
      for (String line: lines) {
        parts = line.split("\t");
        if (parts.length == 3) {
          keys.add(parts[0].trim());
          keysLong.add(parts[0].trim() + " - " + parts[1].trim());
	  m_MapImages.put(parts[0].trim(), new File(imgFile.getParentFile().getAbsolutePath() + File.separator + parts[1]));
	}
      }

      // read rois
      if (isLoggingEnabled())
        getLogger().info("Reading roi file: " + roiFile);
      lines = FileUtils.loadFromFile(roiFile);
      if (lines == null) {
	getLogger().severe("Failed to read: " + roiFile);
	return;
      }
      for (String line: lines) {
	parts = line.split("\\|");
	if (parts.length == 2) {
	  objs = new LocatedObjects();
	  rois = parts[1].replace("roiAndLabel", "").trim().split(" ");
	  i    = 0;
	  while (i+3 < rois.length) {
	    try {
	      x0 = Integer.parseInt(rois[i]);
	      y0 = Integer.parseInt(rois[i+1]);
	      x1 = Integer.parseInt(rois[i+2]);
	      y1 = Integer.parseInt(rois[i+3]);
	      obj = new LocatedObject(null, x0, y0, x1 - x0 + 1, y1 - y0 + 1);
	      obj.getMetaData().put("image index", parts[0].trim());
	      obj.getMetaData().put("type", classes.getOrDefault(rois[i+4], rois[i+4]));
	      objs.add(obj);
	    }
	    catch (Exception e) {
	      getLogger().log(Level.SEVERE, "Failed to parse rois starting at position #" + (i) + "!", e);
	    }
	    i += 5;
	  }
	  m_MapROIs.put(parts[0].trim(), objs.toReport("Object."));
	}
      }

      // update
      m_PanelImage.clear();
      m_ModelIndices = new DefaultListModel<>();
      for (String key: keysLong)
        m_ModelIndices.addElement(key);
      m_ListIndices.setModel(m_ModelIndices);
      if (keys.size() > 0)
        m_ListIndices.setSelectedIndex(0);
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the ROIs in images for CNTK FastRCNN datasets.\n"
	+ "Requires a pair of files '.*_img_file.*' and '.*_roi_file.*'.\n"
	+ "The images listed in '.*_img_file.*' must be present as well.\n"
	+ "The file with the class mapping ('class_map.txt') is optional, but "
	+ "helps when displaying the classes in the overlay.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"txt"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    File	imgFile;
    File	roiFile;
    File	classMapFile;
    RoiPanel	panel;

    if (file.getName().contains("_img_file")) {
      imgFile = file;
      roiFile = new File(imgFile.getAbsolutePath().replace("_img_file", "_roi_file"));
    }
    else if (file.getName().contains("_roi_file")) {
      roiFile = file;
      imgFile = new File(roiFile.getAbsolutePath().replace("_roi_file", "_img_file"));
    }
    else {
      return new NoPreviewAvailablePanel();
    }

    if (!imgFile.exists() || !roiFile.exists() || imgFile.isDirectory() || roiFile.isDirectory())
      return new NoPreviewAvailablePanel();

    classMapFile = new File(imgFile.getParentFile().getAbsolutePath() + File.separator + "class_map.txt");
    if (!classMapFile.exists() || classMapFile.isDirectory())
      classMapFile = null;

    panel = new RoiPanel(this);
    panel.display(imgFile, roiFile, classMapFile);

    return new PreviewPanel(panel, panel);
  }
}
