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
 * VlcjDirectRenderPanelClean.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.video.vlcjplayer;

import adams.gui.core.BasePanel;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A panel that is a self contained Direct rendering video player.
 *
 * @author sjb90
 */
public class DirectRenderMediaPlayerPanel extends BasePanel {

  /** The width of the video */
  protected int m_VideoWidth;

  /** The height of the video */
  protected int m_VideoHeight;

  /** Media component for playing the video */
  protected DirectMediaPlayerComponent m_MediaComponent;

  /** an image to fill with the video frame */
  protected BufferedImage m_Image;

  protected BufferFormatCallback m_BufferFormatCallback;

  @Override
  protected void initGUI() {
    super.initGUI();
    setBackground(Color.BLACK);
    setOpaque(true);
  }

  @Override
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void finishInit() {
    super.finishInit();
  }

  @Override
  public void paint(Graphics g) {
    // Turns on resizing
    boolean resize = false;
    // Scaling code borrowed from ImagePanel
    double	result;
    double	scaleW;
    double	scaleH;
    double	widthDiff;
    double	heightDiff;
    int		newWidth;
    int		newHeight;
    int		x = 0;
    int		y = 0;
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    newWidth  = getWidth();
    newHeight = getHeight();
    scaleW = (double) newWidth / (double) m_VideoWidth;
    scaleH = (double) newHeight / (double) m_VideoHeight;
    result = Math.min(scaleW, scaleH);
    widthDiff  = newWidth - (m_VideoWidth*result);
    heightDiff = newHeight - (m_VideoHeight*result);
    if(resize) {
      x = (int) (widthDiff / 2);
      y = (int) (heightDiff / 2);
    }

    g2.scale(result, result);
    g2.drawImage(m_Image,null,x,y);
  }

  /**
   * opens a file for playing
   * @param fileName the file to be played
   */
  public void open(String fileName) {
    Dimension d = VideoUtilities.getVideoDimensions(fileName);
    m_VideoHeight = (int)d.getHeight();
    m_VideoWidth  = (int)d.getWidth();
    m_Image = GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getDefaultScreenDevice()
      .getDefaultConfiguration()
      .createCompatibleImage(m_VideoWidth, m_VideoHeight);

    m_BufferFormatCallback = (i, i1) -> new RV32BufferFormat(m_VideoWidth, m_VideoHeight);

    m_MediaComponent = new DirectMediaPlayerComponent(m_BufferFormatCallback) {
      @Override
      protected RenderCallback onGetRenderCallback() {
        return new VideoPlayerRenderCallbackAdapter();
      }
    };
    m_MediaComponent.getMediaPlayer().prepareMedia(fileName);

    invalidate();
    revalidate();
    repaint();
  }

  /**
   * Sets the playback rate
   * @param rate the rate play back at
   */
  public void setRate(float rate) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().setRate(rate);
  }

  /**
   * Sets the position in the video
   * @param v the position to go to
   */
  public void setPosition(float v) {
    if(m_MediaComponent == null)
      return;
  m_MediaComponent.getMediaPlayer().setPosition(v);
  }

  /**
   * A getter for the playback rate
   * @return the current playback rate
   */
  public float getRate() {
    if(m_MediaComponent == null)
      return 1;
    return m_MediaComponent.getMediaPlayer().getRate();
  }

  /**
   * checks to see if the player is muted
   * @return true if the player is muted, false otherwise
   */
  public boolean isMute() {
    if(m_MediaComponent == null)
      return false;
    return m_MediaComponent.getMediaPlayer().isMute();
  }

  /**
   * a getter for the current position in the video playback
   * @return the current position
   */
  public float getPosition() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getPosition();
  }

  /**
   * Returns the current playback time
   * @return the current playback time
   */
  public long getTime() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getTime();
  }

  /**
   * Gets the length of the media
   * @return the length
   */
  public long getLength() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getLength();
  }

  /**
   * adds a media player event listener to our media player
   * @param mediaPlayerEventListener the listener to add
   */
  public void addMediaPlayerEventListener(MediaPlayerEventListener mediaPlayerEventListener) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().addMediaPlayerEventListener(mediaPlayerEventListener);
  }

  /**
   * Pauses the video playback
   */
  public void pause() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().pause();
  }

  /**
   * Plays the current media
   */
  public void play() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().play();
  }

  /**
   * Stops playback
   */
  public void stop() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().stop();
  }

  /**
   * mutes the player
   */
  public void mute() {
    if(m_MediaComponent != null)
      m_MediaComponent.getMediaPlayer().mute(true);
  }

  /**
   * unmutes the player
   */
  public void unmute() {
    if(m_MediaComponent != null)
      m_MediaComponent.getMediaPlayer().mute(false);
  }

  /**
   * Pre prepares a media file for playing
   * @param absolutePath the file name and path to prepare
   */
  public void prepareMedia(String absolutePath) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().prepareMedia(absolutePath);
  }

  /**
   * releases the media player to clean up memory usage
   */
  public void release() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.release();
    m_MediaComponent = null;
  }

  /**
   * A getter for the playing state of the player
   * @return true if the media is playing
   */
  public boolean isPlaying() {
    if(m_MediaComponent == null)
      return false;
    return m_MediaComponent.getMediaPlayer().isPlaying();
  }

  /**
   * Private class required for direct rendering. Simply copies the buffer into the image we're
   * using for storage and then calls a repaint on the panel.
   */
  private class VideoPlayerRenderCallbackAdapter extends RenderCallbackAdapter {

    public VideoPlayerRenderCallbackAdapter() {
      super(new int[m_VideoWidth * m_VideoHeight]);
    }

    @Override
    protected void onDisplay(DirectMediaPlayer directMediaPlayer, int[] rgbBuffer) {
      m_Image.setRGB(0, 0, m_VideoWidth, m_VideoHeight, rgbBuffer, 0, m_VideoWidth);
      DirectRenderMediaPlayerPanel.this.repaint();
    }
  }

}
