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
 * VLCjPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.video.VLCjPlayer;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingHelper;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.logging.Logger;

/**
 * A basic video player. Allows a user to open, play, and pause video
 *
 * @author sjb90 (sjb90 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class VLCjPanel
  extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = -6333350276893620652L;

  /**
   * the file to store the recent files in.
   */
  public final static String SESSION_FILE = "VLCjVideoPlayerSession.props";

  /**
   * the menu bar, if used.
   */
  protected JMenuBar m_MenuBar;

  /**
   * the "load recent" submenu.
   */
  protected JMenu m_MenuFileLoadRecent;

  /** for handling recent files. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /**
   * the menu item "close".
   */
  protected JMenuItem m_MenuItemFileClose;

  /**
   * the menu item "open".
   */
  protected JMenuItem m_MenuItemFileOpen;

  /**
   * the file chooser
   */
  protected BaseFileChooser m_FileChooser;

  /**
   * the menu item "show/hide controls"
   */
  protected JMenuItem m_MenuItemVideoShowHideControls;

  /**
   * the menu item "play"
   */
  protected JMenuItem m_MenuItemVideoPlay;

  /**
   * the menu item "pause"
   */
  protected JMenuItem m_MenuItemVideoPause;

  /**
   * The path to the file we're currently viewing
   */
  protected File m_CurrentFile;

  /**
   * the mediaplayer componenet used to play video
   */
  protected EmbeddedMediaPlayerComponent m_MediaPlayerComponent;
  /**
   * the logger used to output information to the adams console
   */
  protected Logger m_Logger;

  /**
   * the panel for our video controls
   */
  protected BasePanel m_ControlsPanel;

  /**
   * the "play" button
   */
  protected JButton m_PlayButton;

  /**
   * the "pause" button
   */
  protected JButton m_PauseButton;

  /**
   * the "stop" button
   */
  protected JButton m_StopButton;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** flag to say if video is paused */
  protected boolean m_VideoPaused;

  /** flag to say if video is loaded */
  protected boolean m_VideoLoaded;

  /** flag to check that VLC is installed. */
  protected boolean m_VLCInstalled;


  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Logger         = LoggingHelper.getLogger(getClass());
    m_TitleGenerator = new TitleGenerator("VLCj Video Player", true);

    m_VideoPaused    = false;
    m_VideoLoaded    = false;
    m_VLCInstalled   = new NativeDiscovery().discover();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  public void initGUI() {
    super.initGUI();

    m_MediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    add(m_MediaPlayerComponent, BorderLayout.CENTER);

    // Controls
    m_ControlsPanel = new BasePanel(new FlowLayout());
    add(m_ControlsPanel, BorderLayout.SOUTH);

    m_PlayButton = new JButton("Play", GUIHelper.getIcon("run.gif"));
    m_PlayButton.addActionListener(e -> {
      play();
      updateButtons();
    });
    m_ControlsPanel.add(m_PlayButton);

    m_PauseButton = new JButton("Pause", GUIHelper.getIcon("pause.gif"));
    m_PauseButton.addActionListener(e -> {
      pause();
      updateButtons();
    });
    m_ControlsPanel.add(m_PauseButton);

    m_StopButton  = new JButton("Stop", GUIHelper.getIcon("stop_blue.gif"));
    m_StopButton.addActionListener(e -> {
      stop();
      updateButtons();
    });
    m_ControlsPanel.add(m_StopButton);

  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Sets the base title to use for the title generator.
   *
   * @param value	the title to use
   * @see		#m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    update();
  }

  /**
   * Returns the base title in use by the title generator.
   *
   * @return		the title in use
   * @see		#m_TitleGenerator
   */
  public String getTitle() {
    return m_TitleGenerator.getTitle();
  }

  /**
   * Returns the title generator in use.
   *
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar result;
    JMenu menu;
    JMenu submenu;
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
      menuitem.addActionListener(e ->  open());
      menu.add(menuitem);
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
        @Override
        public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
          // ignored
        }
        @Override
        public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  open(e.getItem());
        }
      });
      m_MenuFileLoadRecent = submenu;

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
      menuitem = new JMenuItem("Play", GUIHelper.getIcon("run.gif"));
      menuitem.setMnemonic('P');
      menuitem.setAccelerator((GUIHelper.getKeyStroke("ctrl pressed P")));
      menuitem.addActionListener(e -> play());
      menuitem.setEnabled(false);
      m_MenuItemVideoPlay = menuitem;
      menu.add(menuitem);

      // Video/Pause
      menuitem = new JMenuItem("Pause", GUIHelper.getIcon("pause.gif"));
      menuitem.setMnemonic('U');
      menuitem.setAccelerator((GUIHelper.getKeyStroke("ctrl pressed U")));
      menuitem.addActionListener(e -> pause());
      menuitem.setEnabled(false);
      m_MenuItemVideoPause = menuitem;
      menu.add(menuitem);

      //Video/Show/Hide Controls
      menuitem = new JMenuItem(("Hide Controls"));
      menuitem.setMnemonic('H');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed H"));
      menuitem.addActionListener(e -> showHideControls());
      m_MenuItemVideoShowHideControls = menuitem;
      menu.add(menuitem);

      m_MenuBar = result;
    } else {
      result = m_MenuBar;
    }
    return result;
  }

  protected void pause() {
    m_MediaPlayerComponent.getMediaPlayer().pause();
    m_VideoPaused = !m_VideoPaused;
    update();
  }

  protected void play(){
    m_MediaPlayerComponent.getMediaPlayer().play();
    if(m_VideoPaused)
      m_VideoPaused = false;
    update();
  }

  protected void stop() {
    m_MediaPlayerComponent.getMediaPlayer().stop();
    m_VideoPaused = false;
  }

  /**
   * Returns the file chooser to use.
   *
   * @return		the file chooser
   */
  protected BaseFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = new BaseFileChooser();

    return m_FileChooser;
  }

  /**
   * Pops up dialog to open a file.
   */
  public void open() {
    int retVal;
    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    open(getFileChooser().getSelectedFile());
  }


  /**
   * Opens the specified file.
   *
   * @param file	the file to open
   */
  public void open(File file) {
    m_CurrentFile = file;
    if(!m_VLCInstalled) {
      adams.gui.core.GUIHelper.showErrorMessage(this, "VLC native libraries not found. Please install VLC: " +
        "http://www.videolan.org/vlc/ !");
      return;
    }
    m_MediaPlayerComponent.getMediaPlayer().prepareMedia(m_CurrentFile.getAbsolutePath());
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(m_CurrentFile);
    m_VideoLoaded = true;
    update();
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
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_MediaPlayerComponent.release();
  }

  /**
   * Updates title and menu items.
   */
  protected void update() {
    updateTitle();
    updateMenu();
    updateButtons();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    Runnable	run;

    if (!m_TitleGenerator.isEnabled())
      return;

    run = () -> {
      String title = m_TitleGenerator.generate(m_CurrentFile);
      setParentTitle(title);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    Runnable	run;

    if (m_MenuBar == null)
      return;

    run = () -> {
      // Video
        m_MenuItemVideoPlay.setEnabled(m_VideoLoaded);
        m_MenuItemVideoPause.setEnabled(m_VideoLoaded);

      updateButtons();
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    Runnable run;
    if (m_ControlsPanel == null || m_PauseButton == null || m_PlayButton == null || m_StopButton == null) {
      return;
    }
    run = () -> {
        m_StopButton.setEnabled(m_VideoLoaded);
        m_PauseButton.setEnabled(m_VideoLoaded);
        m_PlayButton.setEnabled(m_VideoLoaded);

      if(m_VideoPaused) {
        m_PauseButton.setText("Resume");
        m_PauseButton.setIcon(GUIHelper.getIcon("resume.gif"));
      }
      else {
        m_PauseButton.setText("Pause");
        m_PauseButton.setIcon(GUIHelper.getIcon("pause.gif"));
      }

    };

    SwingUtilities.invokeLater(run);
  }

  /**
   * Safely hides or shows the control panel
   */
  protected void showHideControls() {
    Runnable run;
    if (m_ControlsPanel == null)
      return;

    run = () -> m_ControlsPanel.setVisible(!m_ControlsPanel.isVisible());

    SwingUtilities.invokeLater(run);
  }
}
