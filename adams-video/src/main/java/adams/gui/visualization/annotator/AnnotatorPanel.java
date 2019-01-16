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
 * AnnotatorPanel.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.AbstractTrailReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Trail;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.chooser.TrailFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.video.vlcjplayer.VLCjDirectRenderPanel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for viewing and annotating videos
 *
 * @author sjb90
 */
public class AnnotatorPanel extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = 6965340882268141821L;

  public final static String VIDEO_SESSION_FILE = "AnnotatorVideoSession.props";

  public final static String BINDINGS_SESSION_FILE = "AnnotatorBindingSession.props";

  public final static String ANNOTATION_SESSION_FILE = "AnnotatorAnnotationSession.props";

  public final static String BACKGROUND_SESSION_FILE = "AnnotatorBackgroundSession.props";

  /** the list to store the bindings */
  protected List<Binding> m_Bindings;

  /** a title generator */
  protected TitleGenerator m_TitleGenerator;

  /** a video player panel */
  protected VLCjDirectRenderPanel m_VideoPlayer;

  /** a panel for the annotation bindings */
  protected BasePanel m_BindingPanel;

  /** dialog */
  protected EditBindingsDialog m_BindingsDialog;

  /** menu bad */
  protected JMenuBar m_MenuBar;

  /** menu item for 'open' */
  protected JMenuItem m_MenuItemFileOpen;

  /** Recent file handler for videos */
  protected RecentFilesHandler<JMenu> m_RecentVideosHandler;

  /** recent files menu for videos */
  protected JMenu m_MenuFileLoadRecentVideos;

  /** Recent file handler for Annotations */
  protected RecentFilesHandler<JMenu> m_RecentAnnotationsHandler;

  /** recent files menu for Annotations */
  protected JMenu m_MenuAnnotationLoadRecentAnnotations;

  /** Recent file handler for backgrounds */
  protected RecentFilesHandler<JMenu> m_RecentBackgroundHandler;

  /** recent files menu for backgrounds */
  protected JMenu m_MenuBackgroundLoadRecentBackground;

  /**
   * for closing the program
   */
  protected JMenuItem m_MenuItemVideoClose;

  /** New Bindings action. */
  protected AbstractBaseAction m_ActionNewBindings;

  /**
   * Edit Bindings action
   */
  protected AbstractBaseAction m_ActionEditBindings;

  /**
   * New annotations action
   */
  protected AbstractBaseAction m_ActionNewAnnotations;

  protected AbstractBaseAction m_ActionOpenAnnotations;

  protected JMenuItem m_MenuItemAnnotationsOpen;

  protected AbstractBaseAction m_ActionSaveAnnotations;

  /** extract background action */
  protected AbstractBaseAction m_ActionExtractBackground;

  /**
   * Export annotations
   */
  protected AbstractBaseAction m_ActionExportAnnotations;

  /**
   * save bindings
   */
  protected AbstractBaseAction m_ActionSaveBindings;

  /**
   * open bindings
   */
  protected  AbstractBaseAction m_ActionLoadBindings;

  /**
   * Date formater for outputing timestamps
   */
  protected DateFormat m_dateFormatter;

  /** the file chooser for exporting trails. */
  protected SpreadSheetFileChooser m_ExportFileChooser;

  /** the file chooser for saving bindings. */
  protected BaseFileChooser m_SavePropertiesFileChooser;

  /** the file chooser for saving bindings. */
  protected BaseFileChooser m_LoadPropertiesFileChooser;

  /** the queue that handles binding events */
  protected EventQueue m_EventQueue;

  /** handler for recent bindings */
  protected RecentFilesHandler<JMenu> m_RecentBindingsHandler;

  /** recent bindings menu */
  protected JMenu m_MenuFileLoadRecentBindings;

  /** the ticker that takes care of toggleable bindigns */
  protected Ticker m_Ticker;

  protected TrailFileChooser m_AnnotationsFileChooser;

  /** the dialog for extracting the background image */
  protected ExtractBackgroundDialog m_ExtractDialog;

  /** the extracted background image */
  protected BufferedImage m_BackgroundImage;

  /** clear background action */
  protected AbstractBaseAction m_ActionClearBackground;
  /** open background action */
  protected AbstractBaseAction m_ActionOpenBackground;
  /** save background action */
  protected AbstractBaseAction m_ActionSaveBackground;
  /** view background action */
  protected AbstractBaseAction m_ActionViewBackground;

  /** file chooser for images */
  protected ImageFileChooser m_BackgroundFileChooser;

  /** menu item for background extract */
  protected JMenuItem m_MenuItemBackgroundExtract;
  /** menu item for background save as */
  protected JMenuItem m_MenuItemBackgroundSaveAs;
  /** menu item for background view */
  protected JMenuItem m_MenuItemBackgroundView;


  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_TitleGenerator 		= new TitleGenerator("Annotator", true);
    m_dateFormatter  		= new DateFormat("HH:mm:ss");
    m_Bindings 			= new ArrayList<>();
    m_ExportFileChooser 	= new SpreadSheetFileChooser();
    m_SavePropertiesFileChooser = new BaseFileChooser();
    m_AnnotationsFileChooser    = new TrailFileChooser();
    m_LoadPropertiesFileChooser = new BaseFileChooser();
    m_BackgroundFileChooser	= new ImageFileChooser();

    m_SavePropertiesFileChooser.setAcceptAllFileFilterUsed(false);
    m_SavePropertiesFileChooser.setAutoAppendExtension(true);
    m_SavePropertiesFileChooser.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
    m_SavePropertiesFileChooser.setDefaultExtension("props");
    m_LoadPropertiesFileChooser.setAcceptAllFileFilterUsed(false);
    m_LoadPropertiesFileChooser.setAutoAppendExtension(true);
    m_LoadPropertiesFileChooser.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
    m_LoadPropertiesFileChooser.setDefaultExtension("props");

    m_EventQueue		= new EventQueue();
    initActions();
  }

  /**
   * for initializing actions
   */
  @SuppressWarnings("serial")
  protected void initActions(){
    AbstractBaseAction action;

    // New Bindings
    action = new AbstractBaseAction("New", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	newBindings();
      }
    };
    m_ActionNewBindings = action;

    // Bindings editor
    action = new AbstractBaseAction("Edit...", "properties.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	editBindings();
      }
    };
    m_ActionEditBindings = action;

    action = new AbstractBaseAction("Export...", "spreadsheet.png") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	export();
      }
    };
    m_ActionExportAnnotations = action;

    // Save Bindings
    action = new AbstractBaseAction("Save as...", "save.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	saveBindings();
      }
    };
    m_ActionSaveBindings = action;

    // open Bindings
    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	loadBindings();
      }
    };
    m_ActionLoadBindings = action;

    // New Trail
    action = new AbstractBaseAction("New", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_EventQueue.resetTrail();
      }
    };
    m_ActionNewAnnotations = action;

    // Open annotations
    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	openAnnotations();
      }
    };
    m_ActionOpenAnnotations = action;

    // Save annotations
    action = new AbstractBaseAction("Save As...", "save.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	saveAnnotations();
      }
    };
    m_ActionSaveAnnotations = action;

    action = new AbstractBaseAction("Extract...", "preferences.png") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	if (m_ExtractDialog == null) {
	  if (getParentDialog() != null)
	    m_ExtractDialog = new ExtractBackgroundDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
	  else
	    m_ExtractDialog = new ExtractBackgroundDialog(getParentFrame(), true);
	  m_ExtractDialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
	  m_ExtractDialog.setLocationRelativeTo(AnnotatorPanel.this);
	}

	m_ExtractDialog.setCurrentFile(new PlaceholderFile(m_VideoPlayer.getCurrentFile()));
	m_ExtractDialog.setVisible(true);
	if(m_ExtractDialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
	  m_BackgroundImage = m_ExtractDialog.getBackgroundImage();
	  m_EventQueue.setBackgroundImage(m_BackgroundImage);
	}
      }
    };
    m_ActionExtractBackground = action;

    action = new AbstractBaseAction("Clear", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	clearBackground();
      }
    };

    m_ActionClearBackground = action;

    action = new AbstractBaseAction("Save As...", "save.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	saveBackground();
      }
    };

    m_ActionSaveBackground = action;

    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	openBackground();
      }
    };

    m_ActionOpenBackground = action;

    action = new AbstractBaseAction("View...", "landscape.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	viewBackground();
      }
    };

    m_ActionViewBackground = action;

  }

  /**
   * shows the current background image in a dialog
   */
  protected void viewBackground() {
    ApprovalDialog dlg;
    ImagePanel panel;

    if (m_BackgroundImage == null) {
      GUIHelper.showErrorMessage(this, "No background image available!");
      return;
    }

    if(getParentDialog() != null)
      dlg = ApprovalDialog.getInformationDialog(getParentDialog());
    else
      dlg = ApprovalDialog.getInformationDialog(getParentFrame());
    dlg.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dlg.setTitle("Background");
    panel = new ImagePanel();
    panel.setCurrentImage(m_BackgroundImage);
    panel.setScale(-1);
    dlg.add(panel, BorderLayout.CENTER);
    dlg.setSize(640, 480);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
  }

  /**
   * loads a background from a file
   */
  protected void openBackground() {
    int retVal;

    retVal = m_BackgroundFileChooser.showOpenDialog(this);
    if(retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    openBackground(m_BackgroundFileChooser.getSelectedPlaceholderFile());

  }

  protected void openBackground(PlaceholderFile file) {
    AbstractImageContainer container;
    AbstractImageReader reader = m_BackgroundFileChooser.getReader();

    container = reader.read(file);

    m_BackgroundImage = (BufferedImage)container.getImage();
    m_EventQueue.setBackgroundImage(m_BackgroundImage);
  }

  /**
   * Saves the background to the selected file
   */
  protected void saveBackground() {
    PlaceholderFile file;
    AbstractImageWriter writer;
    BufferedImageContainer container;
    String error;
    int retVal;
    retVal = m_BackgroundFileChooser.showSaveDialog(this);
    container = new BufferedImageContainer();
    container.setImage(m_BackgroundImage);
    if(retVal != TrailFileChooser.APPROVE_OPTION)
      return;
    file 	= m_BackgroundFileChooser.getSelectedPlaceholderFile();
    writer 	= m_BackgroundFileChooser.getWriter();
    error = writer.write(file, container);
    if ( error == null )
      GUIHelper.showErrorMessage(this, "Failed to write image to '" + file + "'!");
  }

  /**
   * Clears the current background.
   */
  protected void clearBackground() {
    m_BackgroundImage = null;
    m_EventQueue.setBackgroundImage(null);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    m_VideoPlayer	= new VLCjDirectRenderPanel();
    m_BindingPanel	= new BasePanel(new FlowLayout());
    m_Ticker		= new Ticker(m_VideoPlayer);
    add(m_VideoPlayer, BorderLayout.CENTER);
    add(m_BindingPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar  result;
    JMenu     menu;
    JMenu     submenu;
    JMenuItem menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // Video
      menu = new JMenu("Video");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(e -> updateMenu());

      // Video/Open
      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(e -> {
	if (m_VideoPlayer.open()) {
	  m_EventQueue.resetTrail();
	  if(m_RecentVideosHandler != null)
	    m_RecentVideosHandler.addRecentItem(m_VideoPlayer.getCurrentFile());
	  revalidate();
	}
      });
      menu.add(menuitem);
      m_MenuItemFileOpen = menuitem;

      // Video/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentVideosHandler = new RecentFilesHandler<>(VIDEO_SESSION_FILE, 5, submenu);
      m_RecentVideosHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  m_VideoPlayer.open(e.getItem());
	}
      });
      m_MenuFileLoadRecentVideos = submenu;

      menuitem = new JMenuItem("Playback speed...");
      menuitem.addActionListener((ActionEvent e) -> m_VideoPlayer.enterPlaybackSpeed());
      menu.add(menuitem);

      menuitem = new JMenuItem("Quit", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('Q');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(e -> close());
      m_MenuItemVideoClose = menuitem;
      menu.addSeparator();
      menu.add(menuitem);

      // Annotations
      menu = new JMenu("Annotations");
      result.add(menu);
      menu.setMnemonic('A');
      menu.addChangeListener(e -> updateMenu());

      // Annotations/New
      menuitem = new JMenuItem(m_ActionNewAnnotations);
      menu.add(menuitem);

      // Annotations/Open
      menuitem = new JMenuItem(m_ActionOpenAnnotations);
      menu.add(menuitem);
      m_MenuItemAnnotationsOpen = menuitem;

      // Annotations/Open Recent
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentAnnotationsHandler = new RecentFilesHandler<>(ANNOTATION_SESSION_FILE, 5, submenu);
      m_RecentAnnotationsHandler.setAddShortcuts(false);
      m_RecentAnnotationsHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  openAnnotations(new PlaceholderFile(e.getItem()));
	}
      });
      m_MenuAnnotationLoadRecentAnnotations = submenu;

      // Annotations/Save As...
      menuitem = new JMenuItem(m_ActionSaveAnnotations);
      menu.add(menuitem);

      // Annotations/Export
      menuitem = new JMenuItem(m_ActionExportAnnotations);
      menu.add(menuitem);

      // Background
      menu = new JMenu("Background");
      menu.setMnemonic('g');
      menu.addChangeListener(e -> updateMenu());
      result.add(menu);

      // Background/Clear
      menuitem = new JMenuItem(m_ActionClearBackground);
      menu.add(menuitem);
      // Background/Open
      menuitem = new JMenuItem((m_ActionOpenBackground));
      menu.add(menuitem);

      // Background/OpenRecent
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentBackgroundHandler = new RecentFilesHandler<>(BACKGROUND_SESSION_FILE, 5, submenu);
      m_RecentBackgroundHandler.setAddShortcuts(false);
      m_RecentBackgroundHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  openBackground(new PlaceholderFile(e.getItem()));
	}
      });
      m_MenuBackgroundLoadRecentBackground = submenu;


      // Background/Save As
      menuitem = new JMenuItem((m_ActionSaveBackground));
      menu.add(menuitem);
      m_MenuItemBackgroundSaveAs = menuitem;
      menuitem.setEnabled(false);

      // Background/View
      menuitem = new JMenuItem((m_ActionViewBackground));
      menu.add(menuitem);
      m_MenuItemBackgroundView = menuitem;
      menuitem.setEnabled(false);

      // Annotations/Extract Background
      menuitem = new JMenuItem(m_ActionExtractBackground);
      menu.add(menuitem);
      m_MenuItemBackgroundExtract = menuitem;
      menuitem.setEnabled(false);

      // Shortcuts
      menu = new JMenu("Shortcuts");
      result.add(menu);
      menu.setMnemonic('H');
      menu.addChangeListener(e -> updateMenu());

      // Shortcuts/New Shortcuts
      menuitem = new JMenuItem(m_ActionNewBindings);
      menu.add(menuitem);

      // Shortcuts/Open Shortcuts
      menuitem = new JMenuItem(m_ActionLoadBindings);
      menu.add(menuitem);

      // Shortcuts/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentBindingsHandler = new RecentFilesHandler<>(BINDINGS_SESSION_FILE, 5, submenu);
      m_RecentBindingsHandler.setAddShortcuts(false);
      m_RecentBindingsHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  loadBindings(e.getItem().getAbsolutePath());
	}
      });
      m_MenuFileLoadRecentBindings = submenu;

      // Shortcuts/Edit Shortcuts
      menuitem = new JMenuItem(m_ActionEditBindings);
      menu.add(menuitem);

      // Shortcuts/Save Shortcuts
      menuitem = new JMenuItem(m_ActionSaveBindings);
      menu.add(menuitem);

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
    * Updates title and menu items.
  */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    Runnable run;

    if (!m_TitleGenerator.isEnabled())
      return;

    run = () -> {
      String title = m_TitleGenerator.generate(m_VideoPlayer.getCurrentFile());
      setParentTitle(title);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
    if(m_VideoPlayer.isVideoLoaded()) {
      m_MenuItemBackgroundExtract.setEnabled(true);
    }
    else {
      m_MenuItemBackgroundExtract.setEnabled(false);
    }
    if(m_BackgroundImage != null) {
      m_MenuItemBackgroundSaveAs.setEnabled(true);
      m_MenuItemBackgroundView.setEnabled(true);
    }
    else {
      m_MenuItemBackgroundSaveAs.setEnabled(false);
      m_MenuItemBackgroundView.setEnabled(false);
    }
  }

  @Override
  public void cleanUp() {
    if (m_BindingsDialog != null) {
      m_BindingsDialog.dispose();
      m_BindingsDialog = null;
    }
    if (m_VideoPlayer != null) {
      m_VideoPlayer.cleanUp();
      m_VideoPlayer = null;
    }
    if (m_EventQueue != null) {
      m_EventQueue.cleanUp();
      m_EventQueue = null;
    }
    if (m_ExtractDialog != null) {
      m_ExtractDialog.dispose();
      m_ExtractDialog = null;
    }
  }

  /**
   * Updates the binding bar to contain an indicator for every binding
   */
  protected void updateBindingBar() {
    Runnable run = () -> {
      AnnotationPanel panel;
      resetBindingBar();
      for (Binding item : m_Bindings) {
	panel = new AnnotationPanel();
	panel.configureAnnotationPanel(item, m_VideoPlayer);
	m_BindingPanel.add(panel);
	m_BindingPanel.revalidate();
	if(m_EventQueue != null)
	  panel.addListener(m_EventQueue);
	if(m_Ticker != null && item.isToggleable())
	  m_Ticker.addListener(panel);
      }
      invalidate();
      revalidate();
    };
    SwingUtilities.invokeLater(run);
  }

  protected void resetBindingBar() {
    m_BindingPanel.removeAll();
    m_Ticker.removeAll();
  }

  /**
   * Sets the base title to use for the title generator.
   *
   * @param value the title to use
   * @see #m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    //update();
  }

  /**
   * Closes the dialog, if possible.
   */
  protected void close() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(false);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(false);
    cleanUp();
    closeParent();
  }

  /**
   * Resets the bindings.
   */
  public void newBindings() {
    m_Bindings = new ArrayList<>();
    updateBindingBar();
  }

  /**
   * Pops up dialog for editing bindings
   */
  public void editBindings() {
    if(getParentDialog() != null)
      m_BindingsDialog = new EditBindingsDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
    else
      m_BindingsDialog = new EditBindingsDialog(getParentFrame(), true);
    m_BindingsDialog.setBindings(m_Bindings);
    m_BindingsDialog.setLocationRelativeTo(this);
    m_BindingsDialog.setVisible(true);
    if(m_BindingsDialog.getOption() == ApprovalDialog.CANCEL_OPTION)
      return;
    m_Bindings = m_BindingsDialog.getBindings();
    updateBindingBar();
  }

  /**
   * Exports the current trail to a spreadsheet file that the user selects.
   */
  public void export() {
    int retVal;
    SpreadSheet sheet;
    SpreadSheetWriter writer;

    if (m_EventQueue == null)
      return;

    retVal = m_ExportFileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;
    sheet = m_EventQueue.toSpreadSheet();
    writer = m_ExportFileChooser.getWriter();
    if (!writer.write(sheet, m_ExportFileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(this, "Failed to export data to: " + m_ExportFileChooser.getSelectedFile());
  }

  /**
   * Loads bindings from a file selected by the user
   */
  public void loadBindings() {
    int retVal;

    retVal = m_LoadPropertiesFileChooser.showOpenDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    String bindingPath = m_LoadPropertiesFileChooser.getSelectedFile().getAbsolutePath();
    loadBindings(bindingPath);
  }

  /**
   * Saves the current annotation to a trail file
   */
  protected void saveAnnotations() {
    Trail trail;
    PlaceholderFile file;
    AbstractDataContainerWriter writer;

    int retVal;
    retVal = m_AnnotationsFileChooser.showSaveDialog(this);
    if(retVal != TrailFileChooser.APPROVE_OPTION)
      return;
    file 	= m_AnnotationsFileChooser.getSelectedPlaceholderFile();
    trail 	= m_EventQueue.getTrail();
    writer 	= m_AnnotationsFileChooser.getWriter();

    writer.setOutput(file);
    if (!writer.write(trail))
      GUIHelper.showErrorMessage(this, "Failed to write trail to '" + file + "'!\nCheck console for error message.");
  }

  /**
   * Loads annotations from a trail file
   */
  protected void openAnnotations() {
    int retVal;

    retVal = m_AnnotationsFileChooser.showOpenDialog(this);
    if(retVal != TrailFileChooser.APPROVE_OPTION)
      return;

    openAnnotations(m_AnnotationsFileChooser.getSelectedPlaceholderFile());
  }

  /**
   * Loads annotations from a trail file
   */
  protected void openAnnotations(PlaceholderFile file) {
    List<Trail> readTrail;
    AbstractTrailReader reader = (AbstractTrailReader) m_AnnotationsFileChooser.getReader();

    reader.setInput(file);

    readTrail = reader.read();
    if(readTrail.size() == 0)
      return;
    m_EventQueue.loadTrail(readTrail.get(0));
    m_RecentAnnotationsHandler.addRecentItem(file);

  }

  /**
   * Loads bindings from a file selected by the user
   */
  public void loadBindings(String bindingPath){
    Properties props = new Properties();
    props.load(bindingPath);

    // Clear the current bindings
    m_Bindings = new ArrayList<>();
    // Convert to bindings
    int count = props.getInteger("Count");
    Binding b;
    for( int i = 0; i < count; i++) {
      try {
	String prefix = Integer.toString(i);
	Properties subset = props.subset(prefix);
	b = new Binding(subset, prefix);
	m_Bindings.add(b);
      }
      catch(InvalidKeyException e) {
	System.err.println(e.getMessage());
      }
    }
    updateBindingBar();

    if(m_RecentBindingsHandler != null)
      m_RecentBindingsHandler.addRecentItem(new File(bindingPath));
  }

  /**
   * Saves the current bindings to a file selected by the user
   */
  public void saveBindings() {
    int retVal;
    int i;
    Properties props = new Properties();

    retVal = m_SavePropertiesFileChooser.showSaveDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    for(i = 0; i < m_Bindings.size(); i++) {
      props.add(m_Bindings.get(i).toProperty(i));
    }
    props.setInteger("Count", i);
    String savePath = m_SavePropertiesFileChooser.getSelectedFile().getAbsolutePath();
    props.save(savePath);
    if(m_RecentBindingsHandler != null)
      m_RecentBindingsHandler.addRecentItem(new File(savePath));
  }
}
