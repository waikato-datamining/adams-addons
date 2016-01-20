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
import adams.core.DateUtils;
import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.*;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
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
public class VLCjDirectRenderPanel
  extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = -6333350276893620652L;

  /**
   * the file to store the recent files in.
   */
  public final static String SESSION_FILE = "VLCjVideoPlayerSession.props";

  /** the properties file name. */
  public final static String FILENAME    = "adams/gui/visualization/video/vlcjplayer/VLCjVideoPlayer.props";
  public static final String EVENT_PLAY  = "play";
  public static final String EVENT_PAUSE = "pause";
  public static final String EVENT_STOP  = "stop";
  public static final String EVENT_MUTE  = "mute";

  /** the values used for playback rate */
  protected static final double DEFAULT_RATE 	= 1.0d;
  protected static final double MAX_RATE	= 4.0d;
  protected static final double MIN_RATE	= 0.25d;
  protected static final double RATE_STEP	= 0.25d;


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
   * the menu item "Set Speed"
   */
  protected JMenuItem m_MenuItemSetSpeed;


  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * The path to the file we're currently viewing
   */
  protected File m_CurrentFile;

  /**
   * the media player component used to play video
   */
  protected DirectMediaPlayerComponent m_MediaPlayerComponent;
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

  /** spinner for setting the playback rate */
  protected JSpinner m_RateSpinner;

  /** the model for our spinner */
  protected SpinnerNumberModel m_SpinnerModel;

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
   * flag to say if sound is muted
   */
  protected boolean m_SoundMuted;
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
   * Set speed action
   */
  protected AbstractBaseAction m_SetSpeedAction;

  /**
   * Date formater for outputing timestamps
   */
  protected DateFormat m_dateFormatter;

  /** the listeners for the play event. */
  protected HashSet<ActionListener> m_PlayListeners;

  /** the listeners for the play event. */
  protected HashSet<ActionListener> m_PauseListeners;

  /** the listeners for the play event. */
  protected HashSet<ActionListener> m_StopListeners;

  /** the listeners for the play event. */
  protected HashSet<ActionListener> m_MuteListeners;

  /*******************************************************************************************
   * Direct rendering additions
   */
  protected DirectMediaPlayerComponent m_DirectVideoPlayer;

  protected BasePanel m_VideoSurface;

  protected final int width = 800; //getWidth();
  protected final int height = 600; //getHeight();
  protected BufferedImage m_Image;


  protected void initDirectRenderingComponent() {

    BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
      @Override
      public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        return new RV32BufferFormat(width, height);
      }
    };

    m_MediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
      @Override
      protected RenderCallback onGetRenderCallback() {
        return new VideoPlayerRenderCallbackAdapter();
      }
    };

    m_Image = GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getDefaultScreenDevice()
      .getDefaultConfiguration()
      .createCompatibleImage(width, height);

    m_VideoSurface = new VideoSurfacePanel();
  }

  private class VideoPlayerRenderCallbackAdapter extends RenderCallbackAdapter {

    public VideoPlayerRenderCallbackAdapter() {
      super(new int[width * height]);
    }

    @Override
    protected void onDisplay(DirectMediaPlayer directMediaPlayer, int[] rgbBuffer) {
      // Copy buffer to the image and repaint
      m_Image.setRGB(0, 0, width, height, rgbBuffer, 0, width);
      m_VideoSurface.repaint();
    }
  }

  private class VideoSurfacePanel extends BasePanel {
    private VideoSurfacePanel() {
      setBackground(Color.black);
      setOpaque(true);
      setPreferredSize(new Dimension(width,height));
      setMinimumSize(new Dimension(width, height));
      setMaximumSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(m_Image,0,0,getWidth(),getHeight(),null);
    }
  }

  /*******************************************************************************************
   * Direct rendering additions end
   */



  /**
   * Gets the paused status of the video
   * @return true if video is paused
   */
  public boolean isVideoPaused() {
    return m_VideoPaused;
  }

  /**
   * Gets the loaded status of the video
   * @return true if a media file is loaded
   */
  public boolean isVideoLoaded() {
    return m_VideoLoaded;
  }

  /**
   * Gets the playing status of the video
   * @return returns true if the video is currently playing
   */
  public boolean isVideoPlaying() {
    return m_VideoPlaying;
  }

  /**
   * Gets the VLC install status
   * @return true if VLC is installed.
   */
  public boolean isVLCInstalled() {
    return m_VLCInstalled;
  }

  /**
   * Gets the muted state of the sound
   * @return true if the sound is muted
   */
  public boolean isSoundMuted() {
    return m_SoundMuted;
  }

  /**
   * Get's the set speed action
   * @return
   */
  public AbstractBaseAction getSetSpeedAction() {
    return m_SetSpeedAction;
  }


  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Logger = LoggingHelper.getLogger(getClass());
    m_TitleGenerator = new TitleGenerator("VLCj Video Player", true);

    m_PlayListeners  = new HashSet<>();
    m_PauseListeners = new HashSet<>();
    m_StopListeners = new HashSet<>();
    m_MuteListeners = new HashSet<>();

    m_VideoPaused    = false;
    m_VideoLoaded    = false;
    m_VideoPlaying   = false;
    m_dateFormatter  = DateUtils.getTimeFormatter();
    m_VLCInstalled   = new NativeDiscovery().discover();
    if (!m_VLCInstalled) {
      GUIHelper.showErrorMessage(this, "VLC native libraries not found. Please install VLC:\n" +
	"http://www.videolan.org/vlc/ !");
      return;
    }

    m_SpinnerModel = new SpinnerNumberModel(DEFAULT_RATE, MIN_RATE, MAX_RATE, RATE_STEP);

    //if (m_VLCInstalled) {
    initDirectRenderingComponent();
    //}
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

    // Set Playback Rate action
    action = new AbstractBaseAction("Set Playback Speed...") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	String currentRate = Float.toString(m_MediaPlayerComponent.getMediaPlayer().getRate());
	String rateString = GUIHelper.showInputDialog(VLCjDirectRenderPanel.this, "Enter Playback Speed", currentRate);
	setPlaybackRate(rateString);
      }
    };
    m_SetSpeedAction = action;
  }

  /**
   * Sets the playback rate to the given rate
   * @param rateString
   */
  public void setPlaybackRate(String rateString) {
    try {
      Float rate = Float.parseFloat(rateString);
      m_MediaPlayerComponent.getMediaPlayer().setRate(rate);
    }
    catch (NumberFormatException numberFormatException) {
      GUIHelper.showErrorMessage(VLCjDirectRenderPanel.this, "Number Format Exception: " + numberFormatException.getMessage());
    }
    catch(NullPointerException nullException ) {
      GUIHelper.showErrorMessage(VLCjDirectRenderPanel.this, "Null Pointer Exception: " + nullException.getMessage());
    }
    catch(Exception ex) {
      //Ignore
    }
  }

  /**
   * For initializing the GUI.
   */
  @Override
  public void initGUI() {
    super.initGUI();

    add(m_VideoSurface, BorderLayout.CENTER);

    // Controls
    m_ControlsPanel = new BasePanel(new FlowLayout());
    add(m_ControlsPanel, BorderLayout.SOUTH);

    // Rate spinner
    m_RateSpinner = new JSpinner(m_SpinnerModel);
    m_RateSpinner.addChangeListener(e -> {
      JSpinner source = (JSpinner)e.getSource();
      double rate = (double)source.getValue();
      m_MediaPlayerComponent.getMediaPlayer().setRate((float)rate);
    });
    m_ControlsPanel.add(m_RateSpinner);

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
    // Make sure our mute status is correct
    m_SoundMuted = m_MediaPlayerComponent.getMediaPlayer().isMute();
    // Sets up a scheduled executor to update the slider position and keep the time labels up to date.
    m_Executor = Executors.newSingleThreadScheduledExecutor();
    m_ExecutorHandler = m_Executor.scheduleAtFixedRate(() -> {
      int position = (int) (m_MediaPlayerComponent.getMediaPlayer().getPosition() * 1000.0F);
      SwingUtilities.invokeLater(() -> m_PositionSlider.setValue(position));
      // Update the current time in the video
      m_PlaybackTime = m_MediaPlayerComponent.getMediaPlayer().getTime();
      m_MediaLength  = m_MediaPlayerComponent.getMediaPlayer().getLength();
      m_MediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(
	new MediaPlayerEventListener() {
	  @Override
	  public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t, String s) {

	  }

	  @Override
	  public void opening(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void buffering(MediaPlayer mediaPlayer, float v) {

	  }

	  @Override
	  public void playing(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void paused(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void stopped(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void forward(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void backward(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void finished(MediaPlayer mediaPlayer) {
	    m_VideoPlaying = false;
	    updateControls();
	  }

	  @Override
	  public void timeChanged(MediaPlayer mediaPlayer, long l) {

	  }

	  @Override
	  public void positionChanged(MediaPlayer mediaPlayer, float v) {

	  }

	  @Override
	  public void seekableChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void pausableChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void titleChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void snapshotTaken(MediaPlayer mediaPlayer, String s) {

	  }

	  @Override
	  public void lengthChanged(MediaPlayer mediaPlayer, long l) {

	  }

	  @Override
	  public void videoOutput(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void scrambledChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void elementaryStreamAdded(MediaPlayer mediaPlayer, int i, int i1) {

	  }

	  @Override
	  public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int i, int i1) {

	  }

	  @Override
	  public void elementaryStreamSelected(MediaPlayer mediaPlayer, int i, int i1) {

	  }

	  @Override
	  public void corked(MediaPlayer mediaPlayer, boolean b) {

	  }

	  @Override
	  public void muted(MediaPlayer mediaPlayer, boolean b) {

	  }

	  @Override
	  public void volumeChanged(MediaPlayer mediaPlayer, float v) {

	  }

	  @Override
	  public void audioDeviceChanged(MediaPlayer mediaPlayer, String s) {

	  }

	  @Override
	  public void error(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void mediaMetaChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

	  }

	  @Override
	  public void mediaDurationChanged(MediaPlayer mediaPlayer, long l) {

	  }

	  @Override
	  public void mediaParsedChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void mediaFreed(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void mediaStateChanged(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

	  }

	  @Override
	  public void newMedia(MediaPlayer mediaPlayer) {

	  }

	  @Override
	  public void subItemPlayed(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void subItemFinished(MediaPlayer mediaPlayer, int i) {

	  }

	  @Override
	  public void endOfSubItems(MediaPlayer mediaPlayer) {

	  }
	}
      );
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

      //Video/Set Playback Speed
      menuitem = new JMenuItem(m_SetSpeedAction);
      m_MenuItemSetSpeed = menuitem;
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
  public void pause() {
    if(m_VideoLoaded && m_VideoPlaying) {
      m_MediaPlayerComponent.getMediaPlayer().pause();
      m_VideoPaused = !m_VideoPaused;
      notifyPauseListeners();
      update();
    }
  }

  /**
   * Plays or stops the video depending on what state it's in currently. If there is no video loaded then
   * nothing happens.
   */
  public void play() {
    if (!m_VideoLoaded)
      return;
    if (m_VideoPlaying && !m_VideoPaused) {
      pause();
      return;
    } else {
      m_MediaPlayerComponent.getMediaPlayer().play();
      m_VideoPlaying = true;
      m_VideoPaused = false;
      notifyPlayListeners();
    }
    update();
  }



  /**
   * Stops the video
   */
  public void stop() {

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
  public void mute() {
    m_SoundMuted = m_MediaPlayerComponent.getMediaPlayer().mute();

    update();
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
   * @return true if video got loaded
   */
  public boolean open() {
    int retVal;
    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return false;

    return open(getFileChooser().getSelectedFile());
  }


  /**
   * Opens the specified file.
   *
   * @param file the file to open
   * @return true if video got loaded
   */
  public boolean open(File file) {
    m_CurrentFile = file;
    if (!m_VLCInstalled) {
      GUIHelper.showErrorMessage(this, "VLC native libraries not found. Please install VLC: " +
	"http://www.videolan.org/vlc/ !");
      return false;
    }
    m_MediaPlayerComponent.getMediaPlayer().prepareMedia(m_CurrentFile.getAbsolutePath());
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(m_CurrentFile);
    m_VideoLoaded = true;
    update();
    return true;
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

      if (!m_SoundMuted) {
        m_MuteButton.setText("Unmute");
        m_MuteButton.setIcon(GUIHelper.getIcon("unmute.png"));
      }
      else {
        m_MuteButton.setText("Mute");
        m_MuteButton.setIcon(GUIHelper.getIcon("mute.png"));
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
  public void showHideControls() {
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

  /**
   * Returns the current video time in milliseconds
   */
  public long getTimeStamp() {
    return m_MediaPlayerComponent.getMediaPlayer().getTime();
  }

  /**
   * Adds the listener for the play events.
   *
   * @param l   the listener to add
   */
  public void addPlayListener(ActionListener l) {
    m_PlayListeners.add(l);
  }

  /**
   * Removes the listener for the play events.
   *
   * @param l   the listener to remove
   */
  public void removePlayListener(ActionListener l) {
    m_PlayListeners.remove(l);
  }

  /**
   * Notifies all play listeners.
   */
  protected synchronized void notifyPlayListeners() {
    ActionEvent e;

    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_PLAY);

    for (ActionListener l: m_PlayListeners)
      l.actionPerformed(e);
  }


  /**
   * Adds the listener for the pause events.
   *
   * @param l   the listener to add
   */
  public void addPauseListener(ActionListener l) {
    m_PauseListeners.add(l);
  }

  /**
   * Removes the listener for the pause events.
   *
   * @param l   the listener to remove
   */
  public void removePauseListener(ActionListener l) {
    m_PauseListeners.remove(l);
  }

  /**
   * Notifies all pause listeners.
   */
  protected synchronized void notifyPauseListeners() {
    ActionEvent e;

    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_PAUSE);

    for (ActionListener l: m_StopListeners)
      l.actionPerformed(e);
  }

  /**
   * Adds the listener for the stop events.
   *
   * @param l   the listener to add
   */
  public void addStopListener(ActionListener l) {
    m_StopListeners.add(l);
  }

  /**
   * Removes the listener for the stop events.
   *
   * @param l   the listener to remove
   */
  public void removeStopListener(ActionListener l) {
    m_StopListeners.remove(l);
  }

  /**
   * Notifies all stop listeners.
   */
  protected synchronized void notifyStopListeners() {
    ActionEvent e;

    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_STOP);

    for (ActionListener l: m_StopListeners)
      l.actionPerformed(e);
  }

  /**
   * Adds the listener for the mute events.
   *
   * @param l   the listener to add
   */
  public void addMuteListener(ActionListener l) {
    m_MuteListeners.add(l);
  }

  /**
   * Removes the listener for the mute events.
   *
   * @param l   the listener to remove
   */
  public void removeMuteListener(ActionListener l) {
    m_MuteListeners.remove(l);
  }

  /**
   * Notifies all mute listeners.
   */
  protected synchronized void notifyMuteListeners() {
    ActionEvent e;

    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_MUTE);

    for (ActionListener l: m_MuteListeners)
      l.actionPerformed(e);
  }
}
