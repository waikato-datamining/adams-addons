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
 * AudioPlaybackPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.audio;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.gui.chooser.AudioFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple audio playback panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioPlaybackPanel
  extends BasePanel
  implements CleanUpHandler, LineListener {

  private static final long serialVersionUID = 3226494650794473459L;

  /** the placeholder for no time. */
  public final static String NO_TIME = "--:--:--";

  /** the button starting playback. */
  protected JButton m_ButtonStart;

  /** the button pausing/resuming playback. */
  protected JButton m_ButtonPauseResume;

  /** the button stopping playback. */
  protected JButton m_ButtonStop;

  /** the text displaying the time. */
  protected JLabel m_LabelTime;

  /** whether audio is being played back. */
  protected boolean m_Playing;

  /** whether the playback is paused. */
  protected boolean m_Paused;

  /** the current file. */
  protected File m_CurrentFile;

  /** the current clip. */
  protected transient Clip m_Clip;

  /** the filechooser for selecing audio files. */
  protected AudioFileChooser m_FileChooser;

  /** timer for refreshing the playback time. */
  protected Timer m_RefreshTimer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Playing      = false;
    m_Paused       = false;
    m_CurrentFile  = null;
    m_Clip         = null;
    m_FileChooser  = new AudioFileChooser();
    m_RefreshTimer = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    // time
    m_LabelTime = new JLabel(NO_TIME);
    m_LabelTime.setFont(m_LabelTime.getFont().deriveFont(m_LabelTime.getFont().getSize() * 2.0f));
    add(m_LabelTime, BorderLayout.CENTER);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonStart = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonStart.addActionListener((ActionEvent e) -> start());
    panel.add(m_ButtonStart);

    m_ButtonPauseResume = new JButton(GUIHelper.getIcon("pause.gif"));
    m_ButtonPauseResume.addActionListener((ActionEvent e) -> pauseOrResume());
    panel.add(m_ButtonPauseResume);

    m_ButtonStop = new JButton(GUIHelper.getIcon("stop_blue.gif"));
    m_ButtonStop.addActionListener((ActionEvent e) -> stop());
    panel.add(m_ButtonStop);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }

  /**
   * Lets the user select an audio file for playback.
   *
   * @return		whether a file was selected
   */
  public boolean open() {
    int		retVal;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != AudioFileChooser.APPROVE_OPTION)
      return false;

    open(m_FileChooser.getSelectedFile());
    return true;
  }

  /**
   * Opens the specified file for playback.
   *
   * @param file	the file
   */
  public void open(File file) {
    stop();
    m_CurrentFile = file;
    update();
  }

  /**
   * Returns the currently loaded file, if any.
   *
   * @return		the file, null if none loaded
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Starts the playback.
   */
  public void start() {
    AudioInputStream 	ais;
    AudioFormat 	format;
    DataLine.Info 	info;

    if (m_CurrentFile == null)
      return;
    if (m_Clip != null)
      return;

    try {
      ais    = AudioSystem.getAudioInputStream(getCurrentFile().getAbsoluteFile());
      format = ais.getFormat();
      info   = new DataLine.Info(Clip.class, format);
      m_Clip = (Clip) AudioSystem.getLine(info);
      m_Clip.addLineListener(this);
      m_Clip.open(ais);
      m_Clip.start();
      m_RefreshTimer = new Timer();
      m_RefreshTimer.schedule(new TimerTask() {
	@Override
	public void run() {
	  update();
	}
      }, 0, 500);
      m_Playing = true;
      m_Paused  = false;
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to playback file: " + getCurrentFile(), e);
      m_Clip = null;
    }

    update();
  }

  /**
   * Pauses or resumes the playback.
   */
  public void pauseOrResume() {
    if (m_Clip == null)
      return;

    if (isPaused()) {
      m_Clip.start();
      m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));
    }
    else {
      m_Clip.stop();
      m_ButtonPauseResume.setIcon(GUIHelper.getIcon("resume.gif"));
    }

    m_Paused = !m_Paused;

    update();
  }

  /**
   * Stops the playback.
   */
  public void stop() {
    m_Playing = false;
    m_Paused  = false;
    if (m_Clip != null) {
      m_Clip.removeLineListener(this);
      m_Clip.close();
      m_Clip = null;
    }
    if (m_RefreshTimer != null) {
      m_RefreshTimer.cancel();
      m_RefreshTimer = null;
    }
    m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));
    update();
  }

  /**
   * Returns whether playback is currently active.
   *
   * @return		true if playback active
   */
  public boolean isPlaying() {
    return m_Playing;
  }

  /**
   * Returns whether playback is currently paused.
   *
   * @return		true if playback paused
   */
  public boolean isPaused() {
    return m_Paused;
  }

  /**
   * Returns the current timestamp in millisecond.
   *
   * @return		the timestamp, -1 if not active
   */
  public long getTimestamp() {
    if (m_Clip != null)
      return m_Clip.getMicrosecondPosition() / 1000;
    return -1;
  }

  /**
   * Updates the displayed time.
   */
  protected void updateTime() {
    int		hours;
    int		mins;
    int		secs;
    int		time;

    if (!isPlaying() || isPaused()) {
      m_LabelTime.setText(NO_TIME);
    }
    else {
      time  = (int) m_Clip.getMicrosecondPosition() / 1000000;
      hours = time / 3600;
      time  = time % 3600;
      mins  = time / 60;
      secs  = time % 60;
      m_LabelTime.setText(
	Utils.padLeft("" + hours, '0', 2)
	  + ":"
	  + Utils.padLeft("" + mins, '0', 2)
	  + ":"
	  + Utils.padLeft("" + secs, '0', 2));
    }
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    boolean	fileLoaded;

    fileLoaded = (getCurrentFile() != null);
    m_ButtonStart.setEnabled(fileLoaded && !isPlaying());
    m_ButtonPauseResume.setEnabled(fileLoaded && isPlaying());
    m_ButtonStop.setEnabled(fileLoaded && isPlaying());
  }

  /**
   * Updates buttons and time.
   */
  protected void update() {
    updateTime();
    updateButtons();
  }

  /**
   * Informs the listener that a line's state has changed. The listener can
   * then invoke {@code LineEvent} methods to obtain information about the
   * event.
   *
   * @param  event a line event that describes the change
   */
  public void update(LineEvent event) {
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    stop();
  }
}
