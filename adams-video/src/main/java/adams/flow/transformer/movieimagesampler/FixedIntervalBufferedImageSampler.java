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
 * FixedIntervalBufferedImageSampler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.movieimagesampler;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseTimeMsec;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IError;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 <!-- globalinfo-start -->
 * Generates a specified number of image samples at fixed intervals.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-offset &lt;adams.core.base.BaseTimeMsec&gt; (property: offset)
 * &nbsp;&nbsp;&nbsp;The offset for the samples, i.e., before starting the sampling.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval in milli-seconds between samples.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-samples &lt;int&gt; (property: numSamples)
 * &nbsp;&nbsp;&nbsp;The number of samples to take.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Xuggle-Xuggler-Main",
  license = License.LGPL3,
  url = "http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle/mediatool/demos/DecodeAndCaptureFrames.java"
)
public class FixedIntervalBufferedImageSampler
  extends AbstractBufferedImageMovieImageSampler {

  private static final long serialVersionUID = -3415741990134536419L;

  /** the number of samples to generate. */
  protected int m_NumSamples;

  /** the offset. */
  protected BaseTimeMsec m_Offset;

  /** the interval in msec between the . */
  protected int m_Interval;

  /** the reader to use. */
  protected transient IMediaReader m_Reader;

  /** the listener to use. */
  protected transient MediaListenerAdapter m_Listener;

  /** the last frame write. */
  protected long m_LastPtsWrite;

  /** the interval for the frames. */
  protected long m_MicroSecondsBetweenFrames;

  /** The video stream index, used to ensure we display frames from one
   * and only one video stream from the media container. */
  protected int m_VideoStreamIndex;

  /** the samples. */
  protected List<BufferedImageContainer> m_Samples;

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
   * @param file	the movie to sample
   * @return		the samples
   */
  @Override
  protected BufferedImageContainer[] doSample(File file) {
    IError 	error;

    // reset
    m_Samples                   = new ArrayList<>();
    m_LastPtsWrite              = Global.NO_PTS;
    m_VideoStreamIndex          = -1;
    m_MicroSecondsBetweenFrames = Global.DEFAULT_PTS_PER_SECOND * m_Interval / 1000;

    try {
      m_Reader = ToolFactory.makeReader(file.getAbsolutePath());
      m_Reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
      m_Listener = new MediaListenerAdapter() {
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
	  if (m_Stopped) {
	    m_Reader.close();
	    return;
	  }
	  try {
	    // if the stream index does not match the selected stream index,
	    // then have a closer look
	    if (event.getStreamIndex() != m_VideoStreamIndex) {
	      // if the selected video stream id is not yet set, go ahead an
	      // select this lucky video stream
	      if (m_VideoStreamIndex == -1)
		m_VideoStreamIndex = event.getStreamIndex();
	      else
		return;
	    }
	    // if uninitialized, backdate mLastPtsWrite so we get the very first frame
	    if (m_LastPtsWrite == Global.NO_PTS)
	      m_LastPtsWrite = event.getTimeStamp() + m_Offset.dateValue().getTime() - m_MicroSecondsBetweenFrames;
	    // if it's time to write the next frame
	    if (event.getTimeStamp() - m_LastPtsWrite >= m_MicroSecondsBetweenFrames) {
	      BufferedImageContainer cont = new BufferedImageContainer();
	      cont.setImage(event.getImage());
	      Field field = new Field("Frame", DataType.NUMERIC);
	      cont.getReport().addField(field);
	      cont.getReport().setValue(field, event.getStreamIndex());
	      field = new Field("Timestamp", DataType.STRING);
	      DateFormat dformat = DateUtils.getTimestampFormatterMsecs();
	      cont.getReport().addField(field);
	      cont.getReport().setValue(field, dformat.format(new Date(event.getTimeStamp(TimeUnit.MILLISECONDS))));
	      m_Samples.add(cont);
	      // update last write time
	      m_LastPtsWrite += m_MicroSecondsBetweenFrames;
	      // gathered all the samples?
	      if (m_Samples.size() == m_NumSamples)
		m_Reader.close();
	    }
	  }
	  catch (Exception e) {
	    Utils.handleException(FixedIntervalBufferedImageSampler.this, "Failed to process video event!", e);
	  }
	}
      };
      m_Reader.addListener(m_Listener);
      do {
	error = m_Reader.readPacket();
	if (error != null) {
	  getLogger().severe("Failed to start reading: " + error.toString());
	  break;
	}
      }
      while (m_Reader.isOpen());
      m_Reader.removeListener(m_Listener);
      if (m_Reader.isOpen())
	m_Reader.close();
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to open video file: " + file, e);
    }

    return m_Samples.toArray(new BufferedImageContainer[m_Samples.size()]);
  }
}
