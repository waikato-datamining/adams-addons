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
 * FixedIntervalBufferdImageSamplerVlcj.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.core.base.BaseTimeMsec;
import adams.data.image.BufferedImageContainer;
import adams.flow.transformer.movieimagesampler.AbstractBufferedImageMovieImageSampler;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class FixedIntervalBufferedImageSamplerVlcj extends AbstractBufferedImageMovieImageSampler {

  private static final long serialVersionUID = -577020017132279115L;

  /** the number of samples to generate. */
  protected int m_NumSamples;

  /** the offset. */
  protected BaseTimeMsec m_Offset;

  /** the interval in msec between the . */
  protected int m_Interval;

  /** headless media player */
  protected HeadlessMediaPlayer m_MediaPlayer;

  /** the samples. */
  protected List<BufferedImageContainer> m_Samples;

  protected MediaPlayerFactory m_Factory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a specified number of image samples at fixed intervals.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "offset", "offset",
      new BaseTimeMsec(BaseTimeMsec.INF_PAST));

    m_OptionManager.add(
      "interval", "interval",
      1000, 1, null);

    m_OptionManager.add(
      "num-samples", "numSamples",
      10, 1, null);
  }

  /**
   * Sets the offset for the samples.
   *
   * @param value	the offset
   */
  public void setOffset(BaseTimeMsec value) {
    m_Offset = value;
    reset();
  }

  /**
   * Returns the offset for the samples.
   *
   * @return		the offset
   */
  public BaseTimeMsec getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetTipText() {
    return "The offset for the samples, i.e., before starting the sampling.";
  }

  /**
   * Sets the interval in milli-seconds between samples.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    if (getOptionManager().isValid("interval", value)) {
      m_Interval = value;
      reset();
    }
  }

  /**
   * Returns the interval between samples in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval in milli-seconds between samples.";
  }

  /**
   * Sets the number of samples to take.
   *
   * @param value	the number
   */
  public void setNumSamples(int value) {
    if (getOptionManager().isValid("numSamples", value)) {
      m_NumSamples = value;
      reset();
    }
  }

  /**
   * Returns the number of samples to take.
   *
   * @return		the number
   */
  public int getNumSamples() {
    return m_NumSamples;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSamplesTipText() {
    return "The number of samples to take.";
  }


  /**
   * Samples images from a movie file.
   *
   * @param file the movie to sample
   * @return the samples, null if failed to sample
   */
  @Override
  protected BufferedImageContainer[] doSample(File file) {
    List<BufferedImageContainer> result = new ArrayList<>();
    BufferedImageContainer container;
    m_Factory = new MediaPlayerFactory();
    m_MediaPlayer = (m_Factory.newHeadlessMediaPlayer());
    m_MediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
      @Override
      public void playing(MediaPlayer mediaPlayer) {
	super.playing(mediaPlayer);
	//int width = (int)m_MediaPlayer.getVideoDimension().getWidth();
	//int height = (int)m_MediaPlayer.getVideoDimension().getHeight();
	for (int i = 0; i < m_NumSamples; i++) {
	  //BufferedImage image = m_MediaPlayer.getSnapshot(width, height);
	  //System.out.println("Loop: " + i + "image: " + image);
	  //container = new BufferedImageContainer();
	  //container.setImage(image);
	  //result.add(container);
	  m_MediaPlayer.skip(m_Interval);
	}
	m_MediaPlayer.stop();
	m_Factory.release();
	m_MediaPlayer.release();
      }
    });
    m_MediaPlayer.playMedia(file.getAbsolutePath());

    while(m_MediaPlayer.isPlaying());



    return result.toArray(new BufferedImageContainer[result.size()]);
  }

}
