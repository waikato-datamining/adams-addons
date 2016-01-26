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
 * VlcjDirectRenderPanelClean.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.video.vlcjplayer;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A panel that is a self contained Direct rendering video player.
 *
 * @author sjb90
 * @version $Revision$
 */
public class DirectRenderMediaPlayerPanel extends BasePanel {


  /** The width of the video */
  protected int m_VideoWidth;

  /** The height of the video */
  protected int m_VideoHeight;

  /** Media componant for playing the video */
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

  public void open(String fileName) {
    getVideoDimensions(fileName);
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
  }

  protected int getVideoDimensions(String fileName) {
    IContainer container = IContainer.make();
    if(container.open(fileName, IContainer.Type.READ, null) < 0)
      return -1;
    int max = container.getNumStreams();
    if(max == 0)
      return -1;
    IStream stream;
    IStreamCoder coder;
    m_VideoHeight = 0;
    m_VideoWidth = 0;
    for (int i = 0; i < max; i++) {
      stream = container.getStream(i);
      coder = stream.getStreamCoder();
      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
        if (coder.getWidth() > m_VideoWidth || coder.getHeight() > m_VideoHeight) {
          m_VideoHeight = coder.getHeight();
          m_VideoWidth 	= coder.getWidth();
        }
      }

    }
    return -1;
  }

  public void setRate(float rate) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().setRate(rate);
  }

  public void setPosition(float v) {
    if(m_MediaComponent == null)
      return;
  m_MediaComponent.getMediaPlayer().setPosition(v);
  }

  public float getRate() {
    if(m_MediaComponent == null)
      return 1;
    return m_MediaComponent.getMediaPlayer().getRate();
  }

  public boolean isMute() {
    if(m_MediaComponent == null)
      return false;
    return m_MediaComponent.getMediaPlayer().isMute();
  }

  public float getPosition() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getPosition();
  }

  public long getTime() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getTime();
  }

  public long getLength() {
    if(m_MediaComponent == null)
      return 0;
    return m_MediaComponent.getMediaPlayer().getLength();
  }

  public void addMediaPlayerEventListener(MediaPlayerEventAdapter mediaPlayerEventAdapter) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().addMediaPlayerEventListener(mediaPlayerEventAdapter);
  }

  public void pause() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().pause();
  }

  public void play() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().play();
  }

  public void stop() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().stop();
  }

  public boolean mute() {
    if(m_MediaComponent == null)
      return false;
    return m_MediaComponent.getMediaPlayer().mute();
  }

  public void prepareMedia(String absolutePath) {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.getMediaPlayer().prepareMedia(absolutePath);
  }

  public void release() {
    if(m_MediaComponent == null)
      return;
    m_MediaComponent.release();
  }

  public boolean isPlaying() {
    if(m_MediaComponent == null)
      return false;
    return m_MediaComponent.getMediaPlayer().isPlaying();
  }

  private class VideoPlayerRenderCallbackAdapter extends RenderCallbackAdapter {

    public VideoPlayerRenderCallbackAdapter() {
      super(new int[m_VideoWidth * m_VideoHeight]);
    }

    @Override
    protected void onDisplay(DirectMediaPlayer directMediaPlayer, int[] rgbBuffer) {
      // Copy buffer to the image and repaint
      m_Image.setRGB(0, 0, m_VideoWidth, m_VideoHeight, rgbBuffer, 0, m_VideoWidth);
      DirectRenderMediaPlayerPanel.this.repaint();
    }
  }

}
