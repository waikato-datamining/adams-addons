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
 * AudioAnnotatorPanel.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.data.audioannotations.AudioAnnotations;
import adams.data.io.input.AbstractAudioAnnotationsReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.action.AbstractBaseAction;
import adams.gui.audio.AudioPlaybackPanel;
import adams.gui.chooser.AudioAnnotationsFileChooser;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for viewing and annotating videos
 *
 * @author sjb90
 * @version $Revision$
 */
public class AudioAnnotatorPanel extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = 6965340882268141821L;

  public final static String AUDIO_SESSION_FILE = "AudioAnnotatorAudioSession.props";

  public final static String BINDINGS_SESSION_FILE = "AudioAnnotatorBindingSession.props";

  public final static String ANNOTATION_SESSION_FILE = "AudioAnnotatorAnnotationSession.props";

  /** the list to store the bindings */
  protected List<Binding> m_Bindings;

  /** a title generator */
  protected TitleGenerator m_TitleGenerator;

  /** a audio player panel */
  protected AudioPlaybackPanel m_Player;

  /** a panel for the annotation bindings */
  protected BasePanel m_BindingPanel;

  /** dialog */
  protected EditBindingsDialog m_BindingsDialog;

  /** menu bad */
  protected JMenuBar m_MenuBar;

  /** menu item for 'open' */
  protected JMenuItem m_MenuItemAudioOpen;

  /** Recent file handler for audio files */
  protected RecentFilesHandler<JMenu> m_RecentAudioFilesHandler;

  /** recent files menu for audio files */
  protected JMenu m_MenuFileLoadRecentAudioFiles;

  /** Recent file handler for Annotations */
  protected RecentFilesHandler<JMenu> m_RecentAnnotationsHandler;

  /** recent files menu for Annotations */
  protected JMenu m_MenuAnnotationLoadRecentAnnotations;

  /**
   * for closing the program
   */
  protected JMenuItem m_MenuItemFileClose;

  /**
   * Play action
   */
  protected AbstractBaseAction m_ActionPlay;

  /**
   * Pause action
   */
  protected AbstractBaseAction m_ActionPause;

  /**
   * Stop action
   */
  protected AbstractBaseAction m_ActionStop;

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
   * Date formatter for outputting timestamps
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

  protected AudioAnnotationsFileChooser m_AnnotationsFileChooser;


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
    m_AnnotationsFileChooser    = new AudioAnnotationsFileChooser();
    m_LoadPropertiesFileChooser = new BaseFileChooser();

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

    // Play action
    action = new AbstractBaseAction("Play", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_Player.start();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator("ctrl pressed P");
    m_ActionPlay = action;

    // Pause action
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_Player.pauseOrResume();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed U");
    m_ActionPause = action;

    // Stop action
    action = new AbstractBaseAction("Stop", "stop_blue.gif" ) {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_Player.stop();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed S");
    m_ActionStop = action;

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
	m_EventQueue.resetAnnotations();
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
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    m_Player       = new AudioPlaybackPanel();
    m_BindingPanel = new BasePanel(new FlowLayout());
    m_Ticker	   = new Ticker(m_Player);
    add(m_Player, BorderLayout.CENTER);
    add(m_BindingPanel, BorderLayout.SOUTH);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
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

      // Audio
      menu = new JMenu("Audio");
      result.add(menu);
      menu.setMnemonic('A');
      menu.addChangeListener(e -> updateMenu());

      // Audio/Open
      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(e -> {
	if (m_Player.open()) {
	  m_EventQueue.resetAnnotations();
	  if(m_RecentAudioFilesHandler != null)
	    m_RecentAudioFilesHandler.addRecentItem(m_Player.getCurrentFile());
	  revalidate();
	}
      });
      menu.add(menuitem);
      m_MenuItemAudioOpen = menuitem;

      // Audio/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentAudioFilesHandler = new RecentFilesHandler<>(AUDIO_SESSION_FILE, 5, submenu);
      m_RecentAudioFilesHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  m_Player.open(e.getItem());
	}
      });
      m_MenuFileLoadRecentAudioFiles = submenu;

      menuitem = new JMenuItem("Quit", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('Q');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(e -> close());
      m_MenuItemFileClose = menuitem;
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
      String title = m_TitleGenerator.generate(m_Player.getCurrentFile());
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
  }

  @Override
  public void cleanUp() {
    if (m_BindingsDialog != null) {
      m_BindingsDialog.dispose();
      m_BindingsDialog = null;
    }
    if (m_Player != null) {
      m_Player.cleanUp();
      m_Player = null;
    }
    if (m_EventQueue != null) {
      m_EventQueue.cleanUp();
      m_EventQueue = null;
    }
  }

  /**
   * Updates the binding bar to contain an indicator for every binding
   */
  protected void updateBindingBar() {
    Runnable run = () -> {
      AudioAnnotationPanel panel;
      resetBindingBar();
      for (Binding item : m_Bindings) {
	panel = new AudioAnnotationPanel();
	panel.configureAnnotationPanel(item, m_Player);
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
    AudioAnnotations annotations;
    PlaceholderFile file;
    AbstractDataContainerWriter writer;

    int retVal;
    retVal = m_AnnotationsFileChooser.showSaveDialog(this);
    if(retVal != AudioAnnotationsFileChooser.APPROVE_OPTION)
      return;
    file 	= m_AnnotationsFileChooser.getSelectedPlaceholderFile();
    annotations = m_EventQueue.getAnnotations();
    writer 	= m_AnnotationsFileChooser.getWriter();

    writer.setOutput(file);
    if (!writer.write(annotations))
      GUIHelper.showErrorMessage(this, "Failed to write trail to '" + file + "'!\nCheck console for error message.");
  }

  /**
   * Loads annotations from a trail file
   */
  protected void openAnnotations() {
    int retVal;

    retVal = m_AnnotationsFileChooser.showOpenDialog(this);
    if(retVal != AudioAnnotationsFileChooser.APPROVE_OPTION)
      return;

    openAnnotations(m_AnnotationsFileChooser.getSelectedPlaceholderFile());
  }

  /**
   * Loads annotations from a trail file
   */
  protected void openAnnotations(PlaceholderFile file) {
    List<AudioAnnotations> readTrail;
    AbstractAudioAnnotationsReader reader = (AbstractAudioAnnotationsReader) m_AnnotationsFileChooser.getReader();

    reader.setInput(file);

    readTrail = reader.read();
    if(readTrail.size() == 0)
      return;
    m_EventQueue.loadAnnotations(readTrail.get(0));
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
