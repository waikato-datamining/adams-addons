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

import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.gui.action.AbstractBaseAction;
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

/**
 * TODO: what class does.
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
  private java.util.List<Binding> m_Bindings;

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
  protected AbstractBaseAction m_MuteAction;

  /**
   * Play action
   */
  protected AbstractBaseAction m_PlayAction;

  /**
   * Pause action
   */
  protected AbstractBaseAction m_PauseAction;

  /**
   * Stop action
   */
  protected AbstractBaseAction m_StopAction;

  /**
   * Edit Bindings action
   */
  protected AbstractBaseAction m_EditBindingsAction;

  /**
   * Date formater for outputing timestamps
   */
  protected DateFormat m_dateFormatter;

  protected JMenuItem m_MenuItemVideoPlay;
  protected JMenuItem m_MenuItemVideoStop;

  /** the trail for recording the events. */
  protected Trail m_Trail;

  @Override
  protected void initialize() {
    super.initialize();
    m_TitleGenerator = new TitleGenerator("Annotator", true);
    m_dateFormatter  = new DateFormat("HH:mm:ss");
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
    m_MuteAction = action;

    // Play action
    action = new AbstractBaseAction("Play", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.play();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator("ctrl pressed P");
    m_PlayAction = action;

    // Pause action
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.pause();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed U");
    m_PauseAction = action;

    // Stop action
    action = new AbstractBaseAction("Stop", "stop_blue.gif" ) {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.stop();
	// TODO
	System.out.println(m_Trail);
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed S");
    m_StopAction = action;

    // Bindings editor
    action = new AbstractBaseAction("Edit Bindings...", "edit.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	if(getParentDialog() != null)
	  m_BindingsDialog = new EditBindingsDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
	else
	  m_BindingsDialog = new EditBindingsDialog(getParentFrame(), true);
	m_BindingsDialog.setBindings(m_Bindings);
	m_BindingPanel.setVisible(true);
	updateBindingBar();
      }
    };
    m_EditBindingsAction = action;
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
	  m_Trail = new Trail();
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
      menuitem = new JMenuItem("Open Bindings...");
      menu.add(menuitem);

      // File/Edit Bindings
      menuitem = new JMenuItem(m_EditBindingsAction);
      menu.add(menuitem);

      menuitem = new JMenuItem("Save Bindings...");
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
      menuitem = new JMenuItem(m_PlayAction);
      menuitem.setEnabled(false);
      m_MenuItemVideoPlay = menuitem;
      menu.add(menuitem);

      // Video/Stop
      menuitem = new JMenuItem(m_StopAction);
      menuitem.setEnabled(false);
      m_MenuItemVideoStop = menuitem;
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
	m_MenuItemVideoPlay.setAction(m_PauseAction);
      } else {
	m_MenuItemVideoPlay.setAction(m_PlayAction);
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
  }

  /**
   * Updates the binding bar to contain an indicator for every binding
   */
  private void updateBindingBar() {
    Runnable run = () -> {
      for (Binding item : m_Bindings) {
	m_BindingPanel.add(new JLabel(item.getName()));
	m_BindingPanel.revalidate();
	addKeyBinding(item);
      }
    };
    SwingUtilities.invokeLater(run);

  }

  /**
   * Adds a binding to the window  TODO: Fill out the rest of this
   * @param binding: binding to add
   * @return
   */
  private AbstractBaseAction addKeyBinding(Binding binding) {

    // The action that will be performed when the key is pressed
    AbstractBaseAction action;
    action = new AbstractBaseAction() {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	long msec = m_VideoPlayer.getTimeStamp();
	if (msec == -1)
	  return;
	HashMap<String,Object> meta = new HashMap<>();
	meta.put(binding.getName(), !binding.isInverted());
	Date timestamp = new Date(msec);
	Step step = new Step(timestamp, 0.0f, 0.0f, meta);
	Step oldStep = m_Trail.getStep(timestamp);
	if (oldStep != null) {
	  if (oldStep.hasMetaData())
	    step.getMetaData().putAll(oldStep.getMetaData());
	}
	m_Trail.add(step);
	System.out.println(binding.getName() + " " + m_dateFormatter.format(new Date(m_VideoPlayer.getTimeStamp())));
      }
    };
    KeyStroke keyStroke = binding.getBinding();
    System.out.println(keyStroke);
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, binding.getName());
    getActionMap().put(binding.getName(), action);
    return action;
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
}
