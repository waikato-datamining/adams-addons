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
 * Trim.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.wavefilter;

import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.audio.WaveContainer;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Trim
  extends AbstractWaveFilter
  implements InPlaceProcessing {

  /** for serialization. */
  private static final long serialVersionUID = 2319957467336388607L;

  public enum TrimType {
    SAMPLES,
    SECONDS,
  }

  /** how to trim. */
  protected TrimType m_Type;

  /** the left trim. */
  protected double m_Left;

  /** the right trim. */
  protected double m_Right;

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
        "Trims the Wave object left and/or right, using either sample number of time in seconds.\n"
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
      TrimType.SAMPLES);

    m_OptionManager.add(
      "left", "left",
      0.0, 0.0, null);

    m_OptionManager.add(
      "right", "right",
      0.0, 0.0, null);

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets the trim type to use.
   *
   * @param value	the type
   */
  public void setType(TrimType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the trim type in use.
   *
   * @return		the type
   */
  public TrimType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to interpret the left/right values.";
  }

  /**
   * Sets the starting point of the trim.
   *
   * @param value	the starting point
   */
  public void setLeft(double value) {
    m_Left = value;
    reset();
  }

  /**
   * Returns the starting point of the trim.
   *
   * @return		the starting point
   */
  public double getLeft() {
    return m_Left;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftTipText() {
    return "The starting point of the trimming.";
  }

  /**
   * Sets the end point of the trim.
   *
   * @param value	the end point
   */
  public void setRight(double value) {
    m_Right = value;
    reset();
  }

  /**
   * Returns the end point of the trim.
   *
   * @return		the end point
   */
  public double getRight() {
    return m_Right;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rightTipText() {
    return "The end point of the trimming; ignored if 0.";
  }

  /**
   * Sets whether to skip creating a copy of the wave before trimming.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the wave before trimming.
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
    return "If enabled, no copy of the Wave is created before trimming it.";
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
    result += QuickInfoHelper.toString(this, "left", m_Left, ", left: ");
    result += QuickInfoHelper.toString(this, "right", m_Right, ", right: ");
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
        result.getAudio().trim((int) m_Left, (int) m_Right);
        break;
      case SECONDS:
        result.getAudio().trim(m_Left, m_Right);
        break;
      default:
        throw new IllegalStateException("Unhandled trim type: " + m_Type);
    }

    return result;
  }
}
