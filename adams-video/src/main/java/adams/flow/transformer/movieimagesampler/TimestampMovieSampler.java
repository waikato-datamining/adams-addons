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
 * TimestampMovieSampler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.movieimagesampler;

import adams.core.base.BaseTimeMsec;
import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.video.vlcjplayer.VideoUtilities;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.condition.Condition;
import uk.co.caprica.vlcj.player.condition.conditions.PausedCondition;
import uk.co.caprica.vlcj.player.condition.conditions.PlayingCondition;
import uk.co.caprica.vlcj.player.condition.conditions.TimeReachedCondition;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a list of timestamps in BaseTimeMSec format and returns an array containing the frames closest
 * to those timestamps
 *
 * @author steven
 * @version $Revision$
 */
public class TimestampMovieSampler extends AbstractBufferedImageMovieImageSampler {

  /**
   * List of time stamps to sample
   */
  BaseTimeMsec[] m_TimeStampsToSample;

  /**
   * headless media player
   */
  protected DirectMediaPlayer m_MediaPlayer;

  long m_TargetTime;

  /**
   * the samples.
   */
  protected List<BufferedImageContainer> m_Samples;

  /** a media player factory for getting the media player */
  protected MediaPlayerFactory m_Factory;

  /** the dimensions of the video */
  protected Dimension m_VideoDimension;

  /** an image to feed the buffer into */
  protected BufferedImage m_Image;

  /** a container for storing the current image */
  protected BufferedImageContainer m_CurrentImage;

  /** a container for storing the previous image */
  protected BufferedImageContainer m_PreviousImage;

  /**
   * Returns the array of timestamps in BaseTimeMsec
   * @return the array of timestamps
   */
  public BaseTimeMsec[] getTimeStamps() {
    return m_TimeStampsToSample;
  }

  /**
   * Sets the timestamps to sample
   * @param timeStamps
   */
  public void setTimeStamps(BaseTimeMsec[] timeStamps) {
    m_TimeStampsToSample = timeStamps;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String timeStampsTipText() {
    return "The timestamps to capture images at.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an image at each of the provided timestamps";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "time-stamps", "timeStamps",
      new BaseTimeMsec[0], 1, null);
  }




  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_CurrentImage = new BufferedImageContainer();
    m_PreviousImage = new BufferedImageContainer();
    m_TimeStampsToSample = new BaseTimeMsec[0];
  }

  /**
   * Samples images from a movie file.
   * Code adapted from
   * https://github.com/caprica/vlcj/blob/vlcj-3.0.1/src/test/java/uk/co/caprica/vlcj/test/condition/ConditionTest.java
   *
   * @param file the movie to sample
   * @return the samples, null if failed to sample
   */
  @Override
  protected BufferedImageContainer[] doSample(File file) {
    m_VideoDimension = VideoUtilities.getVideoDimensions(file.getAbsolutePath());
    m_Image = new BufferedImage((int) m_VideoDimension.getWidth(), (int) m_VideoDimension.getHeight(),
      BufferedImage.TYPE_INT_RGB);
    m_Factory = new MediaPlayerFactory();
    BufferFormatCallback bufferFormatCallback = (i, i1) -> new RV32BufferFormat((int) m_VideoDimension.getWidth(),
      (int) m_VideoDimension.getHeight());
    m_MediaPlayer = m_Factory.newDirectMediaPlayer(bufferFormatCallback, new SnapshotRenderCallback());
    m_Samples = new ArrayList<>();

    // Uses the vlcj conditions to make sure the steps happen in sequence. the onBefore method ensures that
    // anything called inside the method happens AFTER the temporary listener has been added to the media player.
    try {
      Condition<?> playingCondition = new PlayingCondition(m_MediaPlayer) {
	@Override
	protected boolean onBefore() {
	  return m_MediaPlayer.startMedia(file.getAbsolutePath());
	}
      };

      playingCondition.await();
      for (int i = 0; i < m_TimeStampsToSample.length; i++) {
	m_TargetTime = m_TimeStampsToSample[i].dateValue().getTime();
	if (m_TargetTime > m_MediaPlayer.getLength())
	  break;
	Condition<?> timeReachedCondition = new TimeReachedCondition(m_MediaPlayer, m_TargetTime) {
	  @Override
	  protected boolean onBefore() {
	    m_MediaPlayer.setTime(targetTime);
	    return true;
	  }
	};
	timeReachedCondition.await();

	Condition<?> pausedCondition = new PausedCondition(m_MediaPlayer) {
	  @Override
	  protected boolean onBefore() {
	    m_MediaPlayer.pause();
	    return true;
	  }
	};
	pausedCondition.await();


	playingCondition = new PlayingCondition(m_MediaPlayer) {
	  @Override
	  protected boolean onBefore() {
	    m_MediaPlayer.play();
	    return true;
	  }
	};
	playingCondition.await();
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // make sure to clean up the media player and factory
    m_MediaPlayer.release();
    m_Factory.release();
    return m_Samples.toArray(new BufferedImageContainer[m_Samples.size()]);
  }

  /**
   * Internal RenderCallback class. Needed for the direct render media player
   */
  private class SnapshotRenderCallback extends RenderCallbackAdapter {

    SnapshotRenderCallback() {
      super(new int[((int) m_VideoDimension.getWidth()) * ((int) m_VideoDimension.getHeight())]);
    }

    @Override
    protected void onDisplay(DirectMediaPlayer directMediaPlayer, int[] rgbBuffer) {
      m_Image = new BufferedImage((int) m_VideoDimension.getWidth(), (int) m_VideoDimension.getHeight(),
	BufferedImage.TYPE_INT_RGB);
      m_CurrentImage = new BufferedImageContainer();
      m_Image.setRGB(0, 0, (int) m_VideoDimension.getWidth(), (int) m_VideoDimension.getHeight(), rgbBuffer, 0,
	(int) m_VideoDimension.getWidth());

      m_CurrentImage.setImage(m_Image);
      long currentTime = directMediaPlayer.getTime();

      if (currentTime == m_TargetTime)
	m_Samples.add(m_CurrentImage);
      else if (currentTime > m_TargetTime)
	m_Samples.add(m_PreviousImage);
      m_PreviousImage = new BufferedImageContainer();
      m_PreviousImage.setImage(m_CurrentImage.getImage());
    }
  }
}
