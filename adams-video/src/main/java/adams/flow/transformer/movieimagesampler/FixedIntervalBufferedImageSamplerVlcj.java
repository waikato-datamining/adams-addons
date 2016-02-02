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

package adams.flow.transformer.movieimagesampler;

import adams.core.base.BaseTimeMsec;
import adams.data.image.BufferedImageContainer;
import com.sun.jna.Memory;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.condition.Condition;
import uk.co.caprica.vlcj.player.condition.conditions.PausedCondition;
import uk.co.caprica.vlcj.player.condition.conditions.PlayingCondition;
import uk.co.caprica.vlcj.player.condition.conditions.SnapshotTakenCondition;
import uk.co.caprica.vlcj.player.condition.conditions.TimeReachedCondition;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

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
  protected DirectMediaPlayer m_MediaPlayer;

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
   * Code adapted from
   * https://github.com/caprica/vlcj/blob/vlcj-3.0.1/src/test/java/uk/co/caprica/vlcj/test/condition/ConditionTest.java
   *
   * @param file the movie to sample
   * @return the samples, null if failed to sample
   */
  @Override
  protected BufferedImageContainer[] doSample(File file) {
    m_Factory = new MediaPlayerFactory();
    BufferFormatCallback bufferFormatCallback= (i, i1) -> new RV32BufferFormat(200, 200);
    m_MediaPlayer = m_Factory.newDirectMediaPlayer(bufferFormatCallback, new BlankRenderCallback());
    m_Samples = new ArrayList<>();

    try {
      Condition<?> playingCondition = new PlayingCondition(m_MediaPlayer) {
	@Override
	protected boolean onBefore() {
	  return m_MediaPlayer.startMedia(file.getAbsolutePath());
	}
      };

      playingCondition.await();
      long time = 0;
      for (int i = 0; i < m_NumSamples; i++) {
	System.out.println("Loop number: " + (i+1));
	System.out.println("number of samples: " + m_NumSamples);

	Condition<?> timeReachedCondition = new TimeReachedCondition(m_MediaPlayer, time ) {
	  @Override
	  protected boolean onBefore() {
            if(targetTime > 0)
              m_MediaPlayer.setTime(targetTime - 10);
            else
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

	Condition<?> snapshotTakenCondition = new SnapshotTakenCondition(m_MediaPlayer) {
	  @Override
	  protected boolean onBefore() {
	    BufferedImageContainer container = new BufferedImageContainer();
	    container.setImage(m_MediaPlayer.getSnapshot());
	    m_Samples.add(container);
	    return true;
	  }
	};
	snapshotTakenCondition.await();

	playingCondition = new PlayingCondition(m_MediaPlayer) {
	  @Override
	  protected boolean onBefore() {
	    m_MediaPlayer.play();
	    return true;
	  }
	};
	playingCondition.await();
	time += m_Interval;
	if(time > m_MediaPlayer.getLength())
	  break;
      }
    }
    catch(Exception e) {
      //ignore
    }

    m_MediaPlayer.release();
    m_Factory.release();
    return m_Samples.toArray(new BufferedImageContainer[m_Samples.size()]);
  }

  private class BlankRenderCallback implements RenderCallback {

    @Override
    public void display(DirectMediaPlayer directMediaPlayer, Memory[] memories, BufferFormat bufferFormat) {
      return;
    }
  }
}
