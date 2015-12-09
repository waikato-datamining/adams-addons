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

package adams.gui.visualization.video.vlcjplayer;

import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.gui.action.AbstractBaseAction;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

  /** the properties file name. */
  public final static String FILENAME = "adams/gui/visualization/video/vlcjplayer/VLCjVideoPlayer.props";

  /** the properties to use. */
  protected static Properties m_Properties;

  /**
   * the menu bar, if used.
   */
  protected JMenuBar m_MenuBar;

  /**
   * the "load recent" submenu.
   */
  protected JMenu m_MenuFileLoadRecent;

  /**
   * for handling recent files.
   */
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
   * the menu item "stop"
   */
  protected JMenuItem m_MenuItemVideoStop;

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
   * slider for changing video position
   */
  protected JSlider m_PositionSlider;

  /**
   * the "play" button
   */
  protected JButton m_PlayButton;

  /**
   * the "pause" button
   */
  protected JButton m_StopButton;

  /**
   * Mute button
   */
  protected JButton m_MuteButton;
  /**
   * for generating the title.
   */
  protected TitleGenerator m_TitleGenerator;

  /**
   * flag to say if video is paused
   */
  protected boolean m_VideoPaused;

  /**
   * flag to say if video is loaded
   */
  protected boolean m_VideoLoaded;

  /**
   * flag to check that VLC is installed.
   */
  protected boolean m_VLCInstalled;

  /**
   * flag for player state, playing/not playing
   */
  protected boolean m_VideoPlaying;

  /**
   * a scheduler to deal with the slider
   */
  protected ScheduledExecutorService m_Executor;

  /**
   * the handler for the scheduled event
   */
  protected ScheduledFuture<?> m_ExecutorHandler;

  /**
   * label for time
   */
  protected JLabel m_PlaybackTimeLabel;

  /**
   * label for the length of the current media file
   */
  protected JLabel m_MediaLengthLabel;

  /**
   * current playback time
   */
  protected long m_PlaybackTime;

  /**
   * media length
   */
  protected long m_MediaLength;

  /**
   * Mute action
   */
  AbstractBaseAction m_MuteAction;

  /**
   * Play action
   */
  AbstractBaseAction m_PlayAction;

  /**
   * Pause action
   */
  AbstractBaseAction m_PauseAction;

  /**
   * Stop action
   */
  AbstractBaseAction m_StopAction;

  /**
   * Date formater for outputing timestamps
   */
  DateFormat m_dateFormatter;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Logger = LoggingHelper.getLogger(getClass());
    m_TitleGenerator = new TitleGenerator("VLCj Video Player", true);

    m_VideoPaused = false;
    m_VideoLoaded = false;
    m_VideoPlaying = false;
    m_dateFormatter = new DateFormat("HH:mm:ss");
    m_VLCInstalled = new NativeDiscovery().discover();
    if (!m_VLCInstalled) {
      adams.gui.core.GUIHelper.showErrorMessage(this, "VLC native libraries not found. Please install VLC:\n" +
	"http://www.videolan.org/vlc/ !");
      return;
    }
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
        mute();
        updateControls();
      }
    };

    action.setMnemonic(KeyEvent.VK_M);
    action.setAccelerator("ctrl pressed M");
    m_MuteAction = action;

    // Play action
    action = new AbstractBaseAction("Play", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
        play();
        updateControls();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator("ctrl pressed P");
    m_PlayAction = action;

    // Pause action
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
        pause();
        updateControls();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed U");
    m_PauseAction = action;

    // Stop action
    action = new AbstractBaseAction("Stop", "stop_blue.gif" ) {
      @Override
      protected void doActionPerformed(ActionEvent e) {
        stop();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed S");
    m_StopAction = action;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  public void initGUI() {
    super.initGUI();

    if (m_VLCInstalled) {
      m_MediaPlayerComponent = new EmbeddedMediaPlayerComponent();
      add(m_MediaPlayerComponent, BorderLayout.CENTER);
    }

    // Controls
    m_ControlsPanel = new BasePanel(new FlowLayout());
    add(m_ControlsPanel, BorderLayout.SOUTH);

    // Slider
    m_PositionSlider = new JSlider(0, 1000, 0);
    m_PositionSlider.addChangeListener(e -> {
      JSlider source = (JSlider) e.getSource();
      if (source.getValueIsAdjusting())
	m_MediaPlayerComponent.getMediaPlayer().setPosition(source.getValue() / 1000F);
    });
    m_ControlsPanel.add(m_PositionSlider);

    m_PlaybackTimeLabel = new JLabel("00:00:00 /");
    m_ControlsPanel.add(m_PlaybackTimeLabel);

    m_MediaLengthLabel = new JLabel("00:00:00");
    m_ControlsPanel.add(m_MediaLengthLabel);

    // Buttons
    m_PlayButton = new JButton(m_PlayAction);
    m_ControlsPanel.add(m_PlayButton);

    m_StopButton = new JButton(m_StopAction);
    m_ControlsPanel.add(m_StopButton);

    m_MuteButton = new JButton(m_MuteAction);
    m_ControlsPanel.add(m_MuteButton);

  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    // Sets up a scheduled executor to update the slider position and keep the time labels up to date.
    m_Executor = Executors.newSingleThreadScheduledExecutor();
    m_ExecutorHandler = m_Executor.scheduleAtFixedRate(() -> {
      int position = (int) (m_MediaPlayerComponent.getMediaPlayer().getPosition() * 1000.0F);
      SwingUtilities.invokeLater(() -> m_PositionSlider.setValue(position));
      // Update the current time in the video
      m_PlaybackTime = m_MediaPlayerComponent.getMediaPlayer().getTime();
      m_MediaLength  = m_MediaPlayerComponent.getMediaPlayer().getLength();
      updateControls();
    }, 0L, 1L, TimeUnit.SECONDS);
  }

  /**
   * Sets the base title to use for the title generator.
   *
   * @param value the title to use
   * @see #m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    update();
  }

  /**
   * Returns the base title in use by the title generator.
   *
   * @return the title in use
   * @see #m_TitleGenerator
   */
  public String getTitle() {
    return m_TitleGenerator.getTitle();
  }

  /**
   * Returns the title generator in use.
   *
   * @return the generator
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
      menuitem.addActionListener(e -> open());
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
      menuitem = new JMenuItem(m_PlayAction);
      menuitem.setEnabled(false);
      m_MenuItemVideoPlay = menuitem;
      menu.add(menuitem);

      // Video/Stop
      menuitem = new JMenuItem(m_StopAction);
      menuitem.setEnabled(false);
      m_MenuItemVideoStop = menuitem;
      menu.add(menuitem);

      //Video/Show/Hide Controls
      menuitem = new JCheckBoxMenuItem("Show Controls");
      menuitem.setSelected(getProperties().getBoolean("ShowControls", true));
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

  /**
   * Pauses the video assuming there is a video loaded and playing
   */
  protected void pause() {
    if(m_VideoLoaded && m_VideoPlaying) {
      m_MediaPlayerComponent.getMediaPlayer().pause();
      m_VideoPaused = !m_VideoPaused;
      update();
    }
  }

  /**
   * Plays or stops the video depending on what state it's in currently. If there is no video loaded then
   * nothing happens.
   */
  protected void play() {
    if (!m_VideoLoaded)
      return;
    if (m_VideoPlaying && !m_VideoPaused) {
      pause();
      return;
    } else {
      m_MediaPlayerComponent.getMediaPlayer().play();
      m_VideoPlaying = true;
      m_VideoPaused = false;
    }
    update();
  }

  /**
   * Stops the video
   */
  protected void stop() {
    if (!m_VideoLoaded)
      return;
    m_MediaPlayerComponent.getMediaPlayer().stop();
    m_VideoPlaying = false;
    m_VideoPaused  = false;
    update();
  }

  /**
   * Mutes the video
   */
  protected void mute() {
    m_MediaPlayerComponent.getMediaPlayer().mute();
  }

  /**
   * Returns the file chooser to use.
   *
   * @return the file chooser
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
   * @param file the file to open
   */
  public void open(File file) {
    m_CurrentFile = file;
    if (!m_VLCInstalled) {
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
    if (m_MediaPlayerComponent != null)
      m_MediaPlayerComponent.release();
    m_ExecutorHandler.cancel(true);
  }

  /**
   * Updates title and menu items.
   */
  protected void update() {
    updateTitle();
    updateMenu();
    updateControls();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    Runnable run;

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
    Runnable run;

    if (m_MenuBar == null)
      return;

    run = () -> {
      // Video
      m_MenuItemVideoPlay.setEnabled(m_VideoLoaded);
      m_MenuItemVideoStop.setEnabled(m_VideoPlaying);
      if (m_VideoPlaying && !m_VideoPaused) {
	m_MenuItemVideoPlay.setAction(m_PauseAction);
      } else {
	m_MenuItemVideoPlay.setAction(m_PlayAction);
      }


      updateControls();
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateControls() {
    Runnable run;
    if (m_ControlsPanel == null || m_StopButton == null || m_PlayButton == null || m_PositionSlider == null) {
      return;
    }
    run = () -> {
      m_StopButton.setEnabled(m_VideoLoaded && m_VideoPlaying);
      m_PlayButton.setEnabled(m_VideoLoaded);

      // Toggle Play and Pause Button
      if (m_VideoPlaying && !m_VideoPaused) {
	m_PlayButton.setAction(m_PauseAction);
      } else {
	m_PlayButton.setAction(m_PlayAction);
      }

      // Enables or disables the slider
      m_PositionSlider.setEnabled(m_VideoLoaded && m_VLCInstalled);
      // Updates the playback time labels to show the correct numbers
      if(!m_MediaPlayerComponent.getMediaPlayer().isPlaying()) {
        m_MediaLengthLabel.setText(m_dateFormatter.format(new Date(0)));
        m_PlaybackTimeLabel.setText(m_dateFormatter.format(new Date(0)) + " /");
      }
      else {
        m_MediaLengthLabel.setText(m_dateFormatter.format(new Date(m_MediaLength)));
        m_PlaybackTimeLabel.setText(m_dateFormatter.format(new Date(m_PlaybackTime)) + " /");
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


  /**
   * Returns the properties to use for the video player.
   *
   * @return the properties
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
        m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
        m_Properties = new Properties();
      }
    }
    return m_Properties;
  }
}
