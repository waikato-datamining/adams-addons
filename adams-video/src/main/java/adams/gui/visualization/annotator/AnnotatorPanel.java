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
 * AnnotatorPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.core.*;
import adams.core.Properties;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.*;
import adams.gui.dialog.EditBindingsDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.visualization.video.vlcjplayer.VLCjPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Panel for viewing and annotating videos
 *
 * @author sjb90
 * @version $Revision$
 */
public class AnnotatorPanel extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  /**
   * the file to store the recent files in.
   */
  public final static String SESSION_FILE = "AnnotatorSession.props";

  private static final long serialVersionUID = 6965340882268141821L;

  /** the list to store the bindings */
  private List<Binding> m_Bindings;

  /** a title generator */
  private TitleGenerator m_TitleGenerator;

  /** a video player panel */
  private VLCjPanel m_VideoPlayer;

  /** a panel for the annotation bindings */
  private BasePanel m_BindingPanel;

  /** dialog */
  private EditBindingsDialog m_BindingsDialog;

  /** menu bad */
  protected JMenuBar m_MenuBar;

  /** menu item for 'open */
  protected JMenuItem m_MenuItemFileOpen;

  /**
   * for handling recent files.
   */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;
  protected JMenu m_MenuFileLoadRecent;
  protected JMenuItem m_MenuItemFileClose;

  /**
   * Mute action
   */
  protected AbstractBaseAction m_ActionMute;

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

  /**
   * Edit Bindings action
   */
  protected AbstractBaseAction m_ActionEditBindings;

  /**
   * Export trail
   */
  protected AbstractBaseAction m_ActionExportTrail;

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

  protected JMenuItem m_MenuItemVideoPlay;
  protected JMenuItem m_MenuItemVideoStop;

  /** the file chooser for exporting trails. */
  protected SpreadSheetFileChooser m_ExportFileChooser;

  /** thefile chooser for saving bindings. */
  protected BaseFileChooser m_SavePropertiesFileChooser;

  /** thefile chooser for saving bindings. */
  protected BaseFileChooser m_LoadPropertiesFileChooser;
  private EventQueue m_EventQueue;

  @Override
  protected void initialize() {
    super.initialize();
    m_TitleGenerator 		= new TitleGenerator("Annotator", true);
    m_dateFormatter  		= new DateFormat("HH:mm:ss");
    m_Bindings 			= new ArrayList<>();
    m_ExportFileChooser 	= new SpreadSheetFileChooser();
    m_SavePropertiesFileChooser = new BaseFileChooser();
    m_LoadPropertiesFileChooser = new BaseFileChooser();
    m_EventQueue		= new EventQueue();
    initActions();
  }

  /**
   * for initializing actions
   */
  protected void initActions(){
    AbstractBaseAction action;

    // Mute action
    action = new AbstractBaseAction("Mute", "mute.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.mute();
      }
    };

    action.setMnemonic(KeyEvent.VK_M);
    action.setAccelerator("ctrl pressed M");
    m_ActionMute = action;

    // Play action
    action = new AbstractBaseAction("Play", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.play();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator("ctrl pressed P");
    m_ActionPlay = action;

    // Pause action
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.pause();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed U");
    m_ActionPause = action;

    // Stop action
    action = new AbstractBaseAction("Stop", "stop_blue.gif" ) {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.stop();
	// TODO
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed S");
    m_ActionStop = action;

    // Bindings editor
    action = new AbstractBaseAction("Edit Bindings...", "edit.gif") {
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
    m_ActionExportTrail = action;

    // Save Bindings
    action = new AbstractBaseAction("Save Bindings...", "save.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	saveBindings();
      }
    };
    m_ActionSaveBindings = action;

    // Save Bindings
    action = new AbstractBaseAction("Load Bindings...", "load.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	loadBindings();
      }
    };
    m_ActionLoadBindings = action;
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    m_VideoPlayer    = new VLCjPanel();
    m_BindingPanel   = new BasePanel(new FlowLayout());
    add(m_VideoPlayer, BorderLayout.CENTER);
    add(m_BindingPanel, BorderLayout.SOUTH);
  }

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

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(e -> updateMenu());

      // File/Open
      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(e -> {
	if (m_VideoPlayer.open())
	  m_EventQueue.resetTrail();
      });
      menu.add(menuitem);
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  m_VideoPlayer.open(e.getItem());
	}
      });
      m_MenuFileLoadRecent = submenu;

      menu.addSeparator();

      // File/Open Bindings
      menuitem = new JMenuItem(m_ActionLoadBindings);
      menu.add(menuitem);

      // File/Save Bindings
      menuitem = new JMenuItem(m_ActionSaveBindings);
      menu.add(menuitem);

      // File/Export
      menuitem = new JMenuItem(m_ActionExportTrail);
      menu.addSeparator();
      menu.add(menuitem);

      // File/Close
      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(e -> close());
      m_MenuItemFileClose = menuitem;
      menu.addSeparator();
      menu.add(menuitem);

      // Video
      menu = new JMenu("Video");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(e -> updateMenu());

      // Video/Play
      menuitem = new JMenuItem(m_ActionPlay);
      menuitem.setEnabled(false);
      m_MenuItemVideoPlay = menuitem;
      menu.add(menuitem);

      // Video/Stop
      menuitem = new JMenuItem(m_ActionStop);
      menuitem.setEnabled(false);
      m_MenuItemVideoStop = menuitem;
      menu.add(menuitem);

      // Bindings
      menu = new JMenu("Bindings");
      result.add(menu);
      menu.setMnemonic('B');
      menu.addChangeListener(e -> updateMenu());

      // Bindings/Edit Bindings
      menuitem = new JMenuItem(m_ActionEditBindings);
      menu.add(menuitem);

      m_MenuBar = result;
    } else {
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
    Runnable run;

    if (m_MenuBar == null)
      return;

    run = () -> {
      // Video
      m_MenuItemVideoPlay.setEnabled(m_VideoPlayer.isVideoLoaded());
      m_MenuItemVideoStop.setEnabled(m_VideoPlayer.isVideoPlaying());
      if (m_VideoPlayer.isVideoPlaying() && !m_VideoPlayer.isVideoPaused()) {
	m_MenuItemVideoPlay.setAction(m_ActionPause);
      } else {
	m_MenuItemVideoPlay.setAction(m_ActionPlay);
      }
    };
    SwingUtilities.invokeLater(run);
  }

  @Override
  public void cleanUp() {
    if (m_BindingsDialog != null) {
      m_BindingsDialog.dispose();
      m_BindingsDialog = null;
    }
    if (m_VideoPlayer != null) {
      m_VideoPlayer.cleanUp();
    }
    List<Component> comps = new ArrayList<>(Arrays.asList(m_BindingPanel.getComponents()));
    comps.stream().filter(c -> c instanceof AnnotationPanel).forEach(c -> {
      ((AnnotationPanel) c).cleanUp();
    });
  }

  /**
   * Updates the binding bar to contain an indicator for every binding
   */
  private void updateBindingBar() {
    System.out.println("Updating the Binding Bar");
    Runnable run = () -> {
      AnnotationPanel panel;
      System.out.println("adding a binding label");
      for (Binding item : m_Bindings) {
	panel = new AnnotationPanel();
	panel.configureAnnotationPanel(item, m_VideoPlayer);
	m_BindingPanel.add(panel);
	m_BindingPanel.revalidate();
	if(m_EventQueue != null)
	  panel.addListener(m_EventQueue);
      }
    };
    SwingUtilities.invokeLater(run);
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
  public void loadBindings(){
    int retVal;
    Properties props = new Properties();

    retVal = m_LoadPropertiesFileChooser.showOpenDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    props.load(m_LoadPropertiesFileChooser.getSelectedFile().getAbsolutePath());

    // Convert to bindings
    int count = props.getInteger("Count");
    Binding b;
    for( int i = 0; i < count; i++) {
      b = new Binding(props.getProperty(i + ".Name"),
	   props.getProperty(i + ".Binding"), props.getBoolean(i + ".Toggleable"), props.getLong(i + ".Interval") ,props.getBoolean(i + ".Inverted"));
      System.out.println("Binding added with key = " + b.getBinding().toString() + " and name = " + b.getName());
      m_Bindings.add(b);
    }
    updateBindingBar();
  }

  /**
   * Saves the current bindings to a file selected by the user
   */
  public void saveBindings() {
    int retVal;
    int i;
    Properties props = new Properties();

    retVal = m_SavePropertiesFileChooser.showOpenDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    for(i = 0; i < m_Bindings.size(); i++) {
      props.add(m_Bindings.get(i).toProperty(i));
    }
    props.setInteger("Count", i);
    props.save(m_SavePropertiesFileChooser.getSelectedFile().getAbsolutePath());
  }
}
