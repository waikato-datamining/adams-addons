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
 * HeatmapViewerPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.io.PlaceholderFile;
import adams.data.filter.AbstractFilter;
import adams.data.heatmap.Heatmap;
import adams.data.io.input.AbstractHeatmapReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.gui.chooser.HeatmapFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.FilterEvent;
import adams.gui.event.FilterListener;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.container.FilterDialog;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.image.plugins.AbstractImageFilter;
import adams.gui.visualization.image.plugins.ImageJTransformer;
import adams.gui.visualization.image.plugins.ImageMagick;
import adams.gui.visualization.image.plugins.JAITransformer;

/**
 * Panel for viewing/processing heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapViewerPanel
  extends BasePanel
  implements MenuBarProvider, StatusMessageHandler,
             FilterListener<Heatmap>, SendToActionSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2642034258827736757L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "HeatmapViewerPanelSession.props";

  /** the setup for the panel. */
  protected static Properties m_Properties;

  /** the tabbed pane for the heatmaps. */
  protected BaseTabbedPane m_TabbedPane;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileOpenRecent;

  /** the "reload current" menu item. */
  protected JMenuItem m_MenuItemFileReloadCurrent;

  /** the "reload all" menu item. */
  protected JMenuItem m_MenuItemFileReloadAll;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "close current" menu item. */
  protected JMenuItem m_MenuItemFileCloseCurrent;

  /** the "close all" menu item. */
  protected JMenuItem m_MenuItemFileCloseAll;

  /** the filter menu item. */
  protected JMenuItem m_MenuItemProcessFilterHeatmap;

  /** the filter all menu item. */
  protected JMenuItem m_MenuItemProcessFilterAllHeatmaps;

  /** the ImageJ menu item. */
  protected JMenuItem m_MenuItemProcessApplyImageJ;

  /** the JAI menu item. */
  protected JMenuItem m_MenuItemProcessApplyJAI;

  /** the ImageMagick menu item. */
  protected JMenuItem m_MenuItemProcessApplyImageMagick;

  /** the show centroid menu item. */
  protected JMenuItem m_MenuItemViewShowCentroid;

  /** the show spreadhseet menu item. */
  protected JMenuItem m_MenuItemViewShowSpreadsheet;

  /** the show statistics menu item. */
  protected JMenuItem m_MenuItemViewShowStatistics;

  /** the show notes menu item. */
  protected JMenuItem m_MenuItemViewShowNotes;

  /** the menu "zoom". */
  protected JMenu m_MenuViewZoom;

  /** the menu item "zoom in". */
  protected JMenuItem m_MenuItemViewZoomIn;

  /** the menu item "zoom out". */
  protected JMenuItem m_MenuItemViewZoomOut;

  /** the color generator menu item. */
  protected JMenuItem m_MenuItemViewColorGenerator;

  /** for loading heatmaps from disk. */
  protected HeatmapFileChooser m_FileChooser;

  /** the current filter. */
  protected AbstractFilter<Heatmap> m_CurrentFilter;

  /** the filter dialog. */
  protected FilterDialog m_DialogFilter;

  /** indicates whether the filtered data was overlayed over the original. */
  protected boolean m_FilterOverlayOriginalData;

  /** indicates whether filter is applied to all heatmaps. */
  protected boolean m_FilterAll;

  /** the search panel for the heatmap report. */
  protected SearchPanel m_SearchPanel;
  
  /** the dialog for selecting the color provider. */
  protected GenericObjectEditorDialog m_DialogColorGenerator;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Properties		props;

    super.initialize();

    props                  = getProperties();
    m_FileChooser          = new HeatmapFileChooser(props.getPath("InitialDir", "%h"));
    m_CurrentFilter        = new adams.data.filter.Normalize();
    m_FilterAll            = false;
    m_DialogColorGenerator = null;
    m_RecentFilesHandler   = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);

    m_TabbedPane = new BaseTabbedPane();
    m_TabbedPane.setTabLayoutPolicy(BaseTabbedPane.SCROLL_TAB_LAYOUT);
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    panel.add(m_TabbedPane, BorderLayout.CENTER);

    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true, "_Search", true, null);
    m_SearchPanel.setMinimumChars(2);
    m_SearchPanel.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	search(m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
      }
    });
    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel2.add(m_SearchPanel);
    panel.add(panel2, BorderLayout.SOUTH);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenuItem		menuitem;
    JMenu		submenu;
    int			i;
    int[]		zooms;
    String[]		shortcuts;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Load from file
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  load();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load(new File[]{e.getItem().getFile()}, (AbstractHeatmapReader) e.getItem().getHandler());
	}
      });
      m_MenuItemFileOpenRecent = submenu;

      // File/Save As
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      });
      m_MenuItemFileSaveAs = menuitem;

      // File/Reload current
      menuitem = new JMenuItem("Reload current");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  reloadCurrent();
	}
      });
      m_MenuItemFileReloadCurrent = menuitem;

      // File/Reload all
      menuitem = new JMenuItem("Reload all");
      menu.add(menuitem);
      menuitem.setMnemonic('e');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F5"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  reloadAll();
	}
      });
      m_MenuItemFileReloadAll = menuitem;

      // File/Close tab
      menuitem = new JMenuItem("Close tab");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
      menuitem.setIcon(GUIHelper.getIcon("delete.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  closeCurrent();
	}
      });
      m_MenuItemFileCloseCurrent = menuitem;

      // File/Close all tabs
      menuitem = new JMenuItem("Close all tabs");
      menu.add(menuitem);
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("delete_all.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  closeAll();
	}
      });
      m_MenuItemFileCloseAll = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

      // Process
      menu = new JMenu("Process");
      result.add(menu);
      menu.setMnemonic('P');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Process/Filter heatmap
      menuitem = new JMenuItem("Filter heatmap...");
      menu.add(menuitem);
      menuitem.setMnemonic('F');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
      menuitem.setIcon(GUIHelper.getIcon("run.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_FilterAll = false;
	  filter();
	}
      });
      m_MenuItemProcessFilterHeatmap = menuitem;

      // Process/Filter heatmap
      menuitem = new JMenuItem("Filter all heatmaps...");
      menu.add(menuitem);
      menuitem.setMnemonic('a');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_FilterAll = true;
	  filter();
	}
      });
      m_MenuItemProcessFilterAllHeatmaps = menuitem;

      menu.addSeparator();

      // Process/Apply ImageJ
      menuitem = new JMenuItem("Apply ImageJ...");
      menu.add(menuitem);
      menuitem.setMnemonic('I');
      menuitem.setIcon(GUIHelper.getIcon("imagej.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  applyImageJ(getCurrentPanel());
	}
      });
      m_MenuItemProcessApplyImageJ = menuitem;

      // Process/Apply ImageJ
      menuitem = new JMenuItem("Apply JAI...");
      menu.add(menuitem);
      menuitem.setMnemonic('J');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  applyJAI(getCurrentPanel());
	}
      });
      m_MenuItemProcessApplyJAI = menuitem;

      // Process/Apply ImageMagick
      menuitem = new JMenuItem("Apply ImageMagick...");
      menu.add(menuitem);
      menuitem.setMnemonic('M');
      menuitem.setIcon(GUIHelper.getIcon("imagemagick.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  applyImageMagick(getCurrentPanel());
	}
      });
      m_MenuItemProcessApplyImageMagick = menuitem;

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // View/Zoom
      submenu = new JMenu("Zoom");
      menu.add(submenu);
      submenu.setMnemonic('Z');
      submenu.setIcon(GUIHelper.getIcon("glasses.gif"));
      submenu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      m_MenuViewZoom = submenu;

      //View/Zoom/Zoom in
      menuitem = new JMenuItem("Zoom in");
      submenu.add(menuitem);
      menuitem.setMnemonic('i');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed I"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().getImagePanel().setScale(getCurrentPanel().getImagePanel().getScale() * 1.5);
	}
      });
      m_MenuItemViewZoomIn = menuitem;

      //View/Zoom/Zoom out
      menuitem = new JMenuItem("Zoom out");
      submenu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed O"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().getImagePanel().setScale(getCurrentPanel().getImagePanel().getScale() / 1.5);
	}
      });
      m_MenuItemViewZoomOut = menuitem;

      // zoom levels
      // TODO: add "fit" zoom
      zooms = new int[]{
	  -100,
	  25,
	  50,
	  66,
	  75,
	  100,
	  150,
	  200,
	  400,
	  800,
	  1600,
      };
      shortcuts = new String[]{
	  "F",
	  "",
	  "",
	  "",
	  "",
	  "1",
	  "",
	  "2",
	  "4",
	  "",
	  "",
      };
      submenu.addSeparator();
      for (i = 0; i < zooms.length; i++) {
	final int fZoom = zooms[i];
	if (zooms[i] == -100)
	  menuitem = new JMenuItem("Best fit");
	else
	  menuitem = new JMenuItem(zooms[i] + "%");
	submenu.add(menuitem);
	if (shortcuts[i].length() > 0)
	  menuitem.setAccelerator(GUIHelper.getKeyStroke(shortcuts[i]));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    zoom(fZoom);
	  }
	});
      }

      // View/Show centroid
      menuitem = new JCheckBoxMenuItem("Show centroid");
      menu.add(menuitem);
      menuitem.setSelected(false);
      menuitem.setMnemonic('c');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  boolean show = m_MenuItemViewShowCentroid.isSelected();
	  for (int i = 0; i < m_TabbedPane.getTabCount(); i++) {
	    getPanelAt(i).setShowCentroid(show);
	    if (show)
	      showStatus("Calculating centroid " + (i+1) + "/" + m_TabbedPane.getTabCount() + "...");
	  }
	  showStatus("");
	}
      });
      m_MenuItemViewShowCentroid = menuitem;

      // View/Color generator
      menuitem = new JMenuItem("Color generator...");
      menu.add(menuitem);
      menuitem.setMnemonic('G');
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  selectColorGenerator();
	}
      });
      m_MenuItemViewColorGenerator = menuitem;

      // View/Show spreadsheet
      menuitem = new JMenuItem("Show spreadsheet...");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('s');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("spreadsheet.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().showSpreadsheet();
	}
      });
      m_MenuItemViewShowSpreadsheet = menuitem;

      // View/Show statistics
      menuitem = new JMenuItem("Show statistics...");
      menu.add(menuitem);
      menuitem.setMnemonic('i');
      menuitem.setIcon(GUIHelper.getIcon("properties.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().showStatistics();
	}
      });
      m_MenuItemViewShowStatistics = menuitem;

      // View/Show notes
      menuitem = new JMenuItem("Show notes...");
      menu.add(menuitem);
      menuitem.setMnemonic('n');
      menuitem.setIcon(GUIHelper.getIcon("report.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().showNotes();
	}
      });
      m_MenuItemViewShowNotes = menuitem;

      m_MenuBar = result;
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * Returns the specified heatmap panel.
   *
   * @param index	the index of the panel
   * @return		the panel, null if invalid index
   */
  public HeatmapPanel getPanelAt(int index) {
    HeatmapPanel	result;

    result = null;

    if (index != -1)
      result = (HeatmapPanel) m_TabbedPane.getComponentAt(index);

    return result;
  }

  /**
   * Returns the currently selected heatmap panel.
   *
   * @return		the panel, null if none selected
   */
  public HeatmapPanel getCurrentPanel() {
    return getPanelAt(m_TabbedPane.getSelectedIndex());
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	dataLoaded;

    if (m_MenuBar == null)
      return;

    dataLoaded = (m_TabbedPane.getTabCount() > 0);

    // File
    m_MenuItemFileSaveAs.setEnabled(getCurrentPanel() != null);
    m_MenuItemFileReloadCurrent.setEnabled(dataLoaded && getCurrentPanel().canReload());
    m_MenuItemFileReloadAll.setEnabled(dataLoaded);
    m_MenuItemFileCloseCurrent.setEnabled(getCurrentPanel() != null);
    m_MenuItemFileCloseAll.setEnabled(dataLoaded);

    // Process
    m_MenuItemProcessFilterHeatmap.setEnabled(dataLoaded);
    m_MenuItemProcessFilterAllHeatmaps.setEnabled(dataLoaded);
    m_MenuItemProcessApplyImageJ.setEnabled(dataLoaded);
    m_MenuItemProcessApplyJAI.setEnabled(dataLoaded);
    m_MenuItemProcessApplyImageMagick.setEnabled(dataLoaded);

    // View
    m_MenuViewZoom.setEnabled(dataLoaded);
    m_MenuItemViewZoomIn.setEnabled(dataLoaded);
    m_MenuItemViewZoomOut.setEnabled(dataLoaded);
    m_MenuItemViewColorGenerator.setEnabled(dataLoaded);
    m_MenuItemViewShowSpreadsheet.setEnabled(dataLoaded);
    m_MenuItemViewShowStatistics.setEnabled(dataLoaded);
    m_MenuItemViewShowNotes.setEnabled(dataLoaded);
  }

  /**
   * Reloads the current panel's heatmap.
   */
  protected void reloadCurrent() {
    HeatmapPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    panel.reload();
    m_TabbedPane.setTitleAt(m_TabbedPane.getSelectedIndex(), panel.getTitle());
  }

  /**
   * Reloads all heatmaps.
   */
  protected void reloadAll() {
    int			i;
    HeatmapPanel	panel;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      panel = getPanelAt(i);
      panel.reload();
      m_TabbedPane.setTitleAt(i, panel.getTitle());
    }
  }

  /**
   * Removes the current tab.
   */
  public void closeCurrent() {
    if (m_TabbedPane.getSelectedIndex() != -1)
      m_TabbedPane.removeTabAt(m_TabbedPane.getSelectedIndex());
  }

  /**
   * Removes all the data.
   */
  public void closeAll() {
    m_TabbedPane.removeAll();
  }

  /**
   * Creates a new panel and configures it.
   *
   * @param map		the map to create the panel for
   * @return		the panel
   */
  protected HeatmapPanel newPanel(Heatmap map) {
    HeatmapPanel 	result;

    result = new HeatmapPanel(this);
    if (m_MenuItemViewShowCentroid != null)
      result.setShowCentroid(m_MenuItemViewShowCentroid.isSelected());
    result.setSearchPanelVisible(false);
    result.setHeatmap(map);

    return result;
  }

  /**
   * Loads heatmaps from disk, popping up a file chooser dialog.
   */
  public void load(final File[] files, final AbstractHeatmapReader reader) {
    Runnable	run;
    
    for (final File file: files) {
      run = new Runnable() {
        @Override
	public void run() {
          showStatus("Loading file: " + file);
          reader.setInput(new PlaceholderFile(file));
          List<Heatmap> maps = reader.read();
          if (maps.size() == 0) {
            GUIHelper.showErrorMessage(HeatmapViewerPanel.this, "Failed to read heatmap from:\n" + reader.getInput());
	    showStatus("");
            return;
          }
          HeatmapPanel panel = newPanel(maps.get(0));
          panel.setReader(reader);
          m_TabbedPane.addTab(panel.getTitle(), panel);
          showStatus("");
          if (m_RecentFilesHandler != null)
            m_RecentFilesHandler.addRecentItem(new Setup(file, reader));
        }
      };
      SwingUtilities.invokeLater(run);
    }
    showStatus("");
  }

  /**
   * Loads the specified file from disk.
   * If no reader can be determined, the 
   */
  public void load(File file) {
    AbstractHeatmapReader 	reader;
    
    reader = (AbstractHeatmapReader) m_FileChooser.getReaderForFile(file);
    if (reader == null) {
      m_FileChooser.setSelectedFile(file);
      GUIHelper.showErrorMessage(this, "Failed to automatically determine reader for file, please choose appropriate one:\n" + file);
      load();
      return;
    }
    
    load(new File[]{file}, (AbstractHeatmapReader) reader);
  }

  /**
   * Loads heatmaps from disk, popping up a file chooser dialog.
   */
  public void load() {
    int		retVal;

    m_FileChooser.setMultiSelectionEnabled(true);
    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != HeatmapFileChooser.APPROVE_OPTION)
      return;

    load(m_FileChooser.getSelectedFiles(), (AbstractHeatmapReader) m_FileChooser.getReader());
  }

  /**
   * Saves the current heatmap to disk.
   */
  protected void saveAs() {
    int				retVal;
    Heatmap			map;
    PlaceholderFile		file;
    AbstractDataContainerWriter	writer;
    
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != HeatmapFileChooser.APPROVE_OPTION)
      return;
      
    map    = getCurrentPanel().getHeatmap();
    file   = m_FileChooser.getSelectedPlaceholderFile();
    writer = m_FileChooser.getWriter();
    writer.setOutput(file);
    if (!writer.write(map))
      GUIHelper.showErrorMessage(this, "Failed to write heatmap to '" + file + "'!\nCheck console for error message.");
  }
  
  /**
   * closes the dialog/frame.
   */
  public void close() {
    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * pops up GOE dialog for filter.
   */
  protected void filter() {
    if (m_DialogFilter == null) {
      if (getParentDialog() != null)
	m_DialogFilter = new FilterDialog(getParentDialog());
      else
	m_DialogFilter = new FilterDialog(getParentFrame());
      m_DialogFilter.setFilterListener(this);
    }

    m_DialogFilter.setFilter(m_CurrentFilter);
    m_DialogFilter.setOverlayOriginalData(m_FilterOverlayOriginalData);
    m_DialogFilter.setLocationRelativeTo(this);
    m_DialogFilter.setVisible(true);
  }

  /**
   * Filters the data.
   *
   * @param e		the event
   */
  @Override
  public void filter(final FilterEvent<Heatmap> e) {
    int		i;
    int		count;
    Runnable	run;
    int[]	indices;

    m_CurrentFilter             = e.getFilter();
    m_FilterOverlayOriginalData = e.getOverlayOriginalData();

    count = m_TabbedPane.getTabCount();
    if (m_FilterAll) {
      indices = new int[count];
      for (i = 0; i < count; i++)
	indices[i] = i;
    }
    else {
      indices = new int[]{m_TabbedPane.getSelectedIndex()};
    }

    for (i = 0; i < indices.length; i++) {
      final int index = indices[i];
      run = new Runnable() {
	@Override
	public void run() {
	  AbstractFilter<Heatmap> filter = e.getFilter().shallowCopy();
	  HeatmapPanel panel = getPanelAt(index);
	  Heatmap filtered = filter.filter(panel.getHeatmap());
	  filter.cleanUp();
	  if (e.getOverlayOriginalData()) {
	    panel = newPanel(filtered);
	    m_TabbedPane.addTab(panel.getTitle(), panel);
	  }
	  else {
	    panel.setHeatmap(filtered);
	    m_TabbedPane.setTitleAt(index, panel.getTitle());
	  }
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }

  /**
   * Applies the image filter to the heatmap image.
   *
   * @param panel	the panel's image to process
   * @param filter	the image filter to apply
   */
  protected void applyImageFilter(HeatmapPanel panel, AbstractImageFilter filter) {
    String	result;
    double	scale;

    scale  = panel.getImagePanel().getScale();
    result = filter.execute(panel.getImagePanel());
    panel.getImagePanel().setScale(scale);
    if (result != null)
      GUIHelper.showErrorMessage(this, "Failed to filter heatmap image:\n" + result);
  }

  /**
   * Applies ImageJ to the heatmap images.
   *
   * @param panel	the heatmap image to process
   */
  protected void applyImageJ(HeatmapPanel panel) {
    applyImageFilter(panel, new ImageJTransformer());
  }

  /**
   * Applies JAI to the heatmap images.
   *
   * @param panel	the heatmap image to process
   */
  protected void applyJAI(HeatmapPanel panel) {
    applyImageFilter(panel, new JAITransformer());
  }

  /**
   * Applies ImageMagick to the heatmap images.
   *
   * @param panel	the heatmap image to process
   */
  protected void applyImageMagick(HeatmapPanel panel) {
    applyImageFilter(panel, new ImageMagick());
  }

  /**
   * Performs a search.
   *
   * @param text	the search text
   * @param isRegExp	whether the search text is a regular expression
   */
  public void search(String text, boolean isRegExp) {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      getPanelAt(i).search(text, isRegExp);
  }

  /**
   * Zooms in/out.
   *
   * @param zoom	the zoom (in percent)
   */
  protected void zoom(int zoom) {
    getCurrentPanel().setZoom((double) zoom / 100);
  }

  /**
   * Lets the user select a new color generator.
   */
  protected void selectColorGenerator() {
    if (m_DialogColorGenerator == null) {
      if (getParentDialog() != null)
	m_DialogColorGenerator = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_DialogColorGenerator = new GenericObjectEditorDialog(getParentFrame(), true);
      m_DialogColorGenerator.setTitle("Select color generator");
      m_DialogColorGenerator.getGOEEditor().setClassType(AbstractColorGradientGenerator.class);
      m_DialogColorGenerator.getGOEEditor().setCanChangeClassInDialog(true);
      m_DialogColorGenerator.setLocationRelativeTo(this);
    }
    
    m_DialogColorGenerator.setCurrent(getCurrentPanel().getColorGenerator().shallowCopy());
    m_DialogColorGenerator.setVisible(true);
    if (m_DialogColorGenerator.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    getCurrentPanel().setColorGenerator(((AbstractColorGradientGenerator) m_DialogColorGenerator.getCurrent()).shallowCopy());
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  @Override
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return (getCurrentPanel() != null);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (getCurrentPanel() != null) {
	result = getCurrentPanel();
      }
    }

    return result;
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
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_DialogColorGenerator != null) {
      m_DialogColorGenerator.dispose();
      m_DialogColorGenerator = null;
    }
  }
}
