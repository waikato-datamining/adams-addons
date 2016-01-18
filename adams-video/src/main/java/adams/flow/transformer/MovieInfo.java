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
 * MovieInfo.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Outputs basic information about a video and its audio&#47;video streams.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Long<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MovieInfo
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;MOVIE_DURATION|MOVIE_START_TIME|BITRATE|NUM_STREAMS|STREAM_DURATION|STREAM_START_TIME|CODEC_TYPE|CODEC_ID|LANGUAGE|STREAM_TIME_BASE|CODER_TIME_BASE|AUDIO_SAMPLE_RATE|AUDIO_CHANNELS|AUDIO_FORMAT|VIDEO_WIDTH|VIDEO_HEIGHT|VIDEO_FORMAT|VIDEO_FRAME_RATE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of info to retrieve.
 * &nbsp;&nbsp;&nbsp;default: MOVIE_DURATION
 * </pre>
 * 
 * <pre>-stream-index &lt;adams.core.Index&gt; (property: streamIndex)
 * &nbsp;&nbsp;&nbsp;The index of the stream to retrieve the information for.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MovieInfo
  extends AbstractTransformer
  implements DataInfoActor {

  private static final long serialVersionUID = 2225032460979700294L;

  /**
   * The type of information to extract.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** the movie duration. */
    MOVIE_DURATION,
    /** the movie start time. */
    MOVIE_START_TIME,
    /** the bitrate. */
    BITRATE,
    /** the number of streams. */
    NUM_STREAMS,
    /** the stream duration. */
    STREAM_DURATION,
    /** the stream start time. */
    STREAM_START_TIME,
    /** the codec type (per stream). */
    CODEC_TYPE,
    /** the codec ID (per stream). */
    CODEC_ID,
    /** the language (per stream). */
    LANGUAGE,
    /** the stream time base (per stream). */
    STREAM_TIME_BASE,
    /** the coder time base (per stream). */
    CODER_TIME_BASE,
    /** the audio sample rate (per audio stream). */
    AUDIO_SAMPLE_RATE,
    /** the number of audio channels (per audio stream). */
    AUDIO_CHANNELS,
    /** the audio format (per audio stream). */
    AUDIO_FORMAT,
    /** the width of the movie (per video stream). */
    VIDEO_WIDTH,
    /** the height of the movie (per video stream). */
    VIDEO_HEIGHT,
    /** the format of the movie (per video stream). */
    VIDEO_FORMAT,
    /** the frame rate of the movie (per video stream). */
    VIDEO_FRAME_RATE,
  }

  /** the info type. */
  protected InfoType m_Type;

  /** the stream index. */
  protected Index m_StreamIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs basic information about a video and its audio/video streams.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.MOVIE_DURATION);

    m_OptionManager.add(
      "stream-index", "streamIndex",
      new Index(Index.FIRST));
  }

  /**
   * Sets the info type.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the info type.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of info to retrieve.";
  }

  /**
   * Sets the stream index.
   *
   * @param value	the index
   */
  public void setStreamIndex(Index value) {
    m_StreamIndex = value;
    reset();
  }

  /**
   * Returns the stream index.
   *
   * @return		the index
   */
  public Index getStreamIndex() {
    return m_StreamIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String streamIndexTipText() {
    return "The index of the stream to retrieve the information for.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type);
    result += QuickInfoHelper.toString(this, "streamIndex", m_StreamIndex, ", stream: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case NUM_STREAMS:
      case BITRATE:
      case AUDIO_CHANNELS:
      case VIDEO_WIDTH:
      case VIDEO_HEIGHT:
      case AUDIO_SAMPLE_RATE:
	return new Class[]{Integer.class};
      case MOVIE_DURATION:
      case STREAM_DURATION:
      case MOVIE_START_TIME:
      case STREAM_START_TIME:
	return new Class[]{Long.class};
      case STREAM_TIME_BASE:
      case CODER_TIME_BASE:
      case VIDEO_FRAME_RATE:
	return new Class[]{Double.class};
      case CODEC_TYPE:
      case CODEC_ID:
      case LANGUAGE:
      case VIDEO_FORMAT:
      case AUDIO_FORMAT:
	return new Class[]{String.class};
      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    IContainer 		container;
    IStream 		stream;
    IStreamCoder 	coder;
    String		filename;

    result = null;

    if (m_InputToken.getPayload() instanceof File)
      filename = ((File) m_InputToken.getPayload()).getAbsolutePath();
    else
      filename = (String) m_InputToken.getPayload();
    filename = new PlaceholderFile(filename).getAbsolutePath();

    container = IContainer.make();
    if (container.open(filename, IContainer.Type.READ, null) < 0)
      result = "Failed to open movie file: " + filename;

    if (result == null) {
      // stream information
      stream = null;
      coder  = null;
      m_StreamIndex.setMax(container.getNumStreams());
      switch (m_Type) {
	case CODEC_TYPE:
	case CODEC_ID:
	case LANGUAGE:
	case STREAM_TIME_BASE:
	case CODER_TIME_BASE:
	case AUDIO_SAMPLE_RATE:
	case AUDIO_CHANNELS:
	case AUDIO_FORMAT:
	case VIDEO_WIDTH:
	case VIDEO_HEIGHT:
	case VIDEO_FORMAT:
	case VIDEO_FRAME_RATE:
	case STREAM_DURATION:
	case STREAM_START_TIME:
	  stream = container.getStream(m_StreamIndex.getIntIndex());
	  coder  = stream.getStreamCoder();
      }

      switch (m_Type) {
	case MOVIE_DURATION:
	  m_OutputToken = new Token(container.getDuration());
	  break;
	case MOVIE_START_TIME:
	  m_OutputToken = new Token(container.getStartTime());
	  break;
	case BITRATE:
	  m_OutputToken = new Token(container.getBitRate());
	  break;
	case NUM_STREAMS:
	  m_OutputToken = new Token(container.getNumStreams());
	  break;
	case LANGUAGE:
	  m_OutputToken = new Token(stream.getLanguage());
	  break;
	case STREAM_DURATION:
	  m_OutputToken = new Token(stream.getDuration());
	  break;
	case STREAM_START_TIME:
	  m_OutputToken = new Token(stream.getStartTime());
	  break;
	case STREAM_TIME_BASE:
	  m_OutputToken = new Token((double) stream.getTimeBase().getNumerator() / (double) stream.getTimeBase().getDenominator());
	  break;
	case CODER_TIME_BASE:
	  m_OutputToken = new Token((double) coder.getTimeBase().getNumerator() / (double) coder.getTimeBase().getDenominator());
	  break;
	case CODEC_TYPE:
	  m_OutputToken = new Token(coder.getCodecType().toString());
	  break;
	case CODEC_ID:
	  m_OutputToken = new Token(coder.getCodecID().toString());
	  break;
	case AUDIO_SAMPLE_RATE:
	  if (coder.getCodecType() == Type.CODEC_TYPE_AUDIO)
	    m_OutputToken = new Token(coder.getSampleRate());
	  break;
	case AUDIO_CHANNELS:
	  if (coder.getCodecType() == Type.CODEC_TYPE_AUDIO)
	    m_OutputToken = new Token(coder.getChannels());
	  break;
	case AUDIO_FORMAT:
	  if (coder.getCodecType() == Type.CODEC_TYPE_AUDIO)
	    m_OutputToken = new Token(coder.getSampleFormat());
	  break;
	case VIDEO_WIDTH:
	  if (coder.getCodecType() == Type.CODEC_TYPE_VIDEO)
	    m_OutputToken = new Token(coder.getWidth());
	  break;
	case VIDEO_HEIGHT:
	  if (coder.getCodecType() == Type.CODEC_TYPE_VIDEO)
	    m_OutputToken = new Token(coder.getHeight());
	  break;
	case VIDEO_FORMAT:
	  if (coder.getCodecType() == Type.CODEC_TYPE_VIDEO)
	    m_OutputToken = new Token(coder.getPixelType().toString());
	  break;
	case VIDEO_FRAME_RATE:
	  if (coder.getCodecType() == Type.CODEC_TYPE_VIDEO)
	    m_OutputToken = new Token(coder.getFrameRate().getDouble());
	  break;
	default:
	  throw new IllegalStateException("Unhandled info type: " + m_Type);
      }

      container.close();
    }

    return result;
  }
}
