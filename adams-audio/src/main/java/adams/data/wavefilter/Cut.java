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
 * Cut.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.wavefilter;

import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.audio.WaveContainer;

/**
 <!-- globalinfo-start -->
 * Cuts out the specified section from the Wave object.<br>
 * Only works if 'subChunk2Id' is 'data' not 'LIST'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;SAMPLES|SECONDS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;How to interpret the start&#47;duration values.
 * &nbsp;&nbsp;&nbsp;default: SAMPLES
 * </pre>
 *
 * <pre>-start &lt;double&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The starting point for the cut.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-duration &lt;double&gt; (property: duration)
 * &nbsp;&nbsp;&nbsp;The duration of the cut.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the Wave is created before cutting.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Cut
  extends AbstractWaveFilter
  implements InPlaceProcessing {

  /** for serialization. */
  private static final long serialVersionUID = 2319957467336388607L;

  /** the starting/duration. */
  protected WaveIndexingType m_Type;

  /** the start. */
  protected double m_Start;

  /** the duration. */
  protected double m_Duration;

  /** whether to skip creating a copy of the container. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cuts out the specified section from the Wave object.\n"
      + "Only works if 'subChunk2Id' is 'data' not 'LIST'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      WaveIndexingType.SAMPLES);

    m_OptionManager.add(
      "start", "start",
      0.0, 0.0, null);

    m_OptionManager.add(
      "duration", "duration",
      0.0, 0.0, null);

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets the indexing type to use.
   *
   * @param value	the type
   */
  public void setType(WaveIndexingType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the indexing type in use.
   *
   * @return		the type
   */
  public WaveIndexingType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to interpret the start/duration values.";
  }

  /**
   * Sets the starting point of the cut.
   *
   * @param value	the starting point
   */
  public void setStart(double value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the starting point of the cut.
   *
   * @return		the starting point
   */
  public double getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The starting point for the cut.";
  }

  /**
   * Sets the duration of the cut.
   *
   * @param value	the duration
   */
  public void setDuration(double value) {
    m_Duration = value;
    reset();
  }

  /**
   * Returns the duration of the cut.
   *
   * @return		the duration
   */
  public double getDuration() {
    return m_Duration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String durationTipText() {
    return "The duration of the cut.";
  }

  /**
   * Sets whether to skip creating a copy of the wave before cutting.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the wave before cutting.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the Wave is created before cutting.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "start", m_Start, ", start: ");
    result += QuickInfoHelper.toString(this, "duration", m_Duration, ", duration: ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no-copy", ", ");

    return result;
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected WaveContainer processData(WaveContainer data) {
    WaveContainer 	result;

    if (!m_NoCopy)
      result = (WaveContainer) data.getClone();
    else
      result = data;

    switch (m_Type) {
      case SAMPLES:
        result.getAudio().trim((int) m_Start, (int) (result.getAudio().getWaveHeader().getSubChunk2Size() - m_Duration - m_Start));
        break;
      case SECONDS:
        result.getAudio().trim(m_Start, result.getAudio().getLengthInSeconds() - m_Duration - m_Start);
        break;
      default:
        throw new IllegalStateException("Unhandled cut type: " + m_Type);
    }

    return result;
  }
}
