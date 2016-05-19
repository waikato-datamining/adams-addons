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
 * ImageRecordReaderConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import org.canova.api.records.reader.RecordReader;
import org.canova.image.recordreader.ImageRecordReader;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Configures a org.canova.image.recordreader.ImageRecordReader instance.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the images.
 * &nbsp;&nbsp;&nbsp;default: 64
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the images.
 * &nbsp;&nbsp;&nbsp;default: 64
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-channels &lt;int&gt; (property: channels)
 * &nbsp;&nbsp;&nbsp;The number of channels.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-append-label &lt;boolean&gt; (property: appendLabel)
 * &nbsp;&nbsp;&nbsp;Whether to append the label.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageRecordReaderConfigurator
  extends AbstractRecordReaderConfigurator {

  private static final long serialVersionUID = 8914456080710417165L;

  /** the width. */
  protected int m_Width;

  /** the height. */
  protected int m_Height;

  /** the number of channels. */
  protected int m_Channels;

  /** whether to append the label. */
  protected boolean m_AppendLabel;

  /** the labels. */
  protected BaseString[] m_Labels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + ImageRecordReader.class.getName() + " instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      64, 1, null);

    m_OptionManager.add(
      "height", "height",
      64, 1, null);

    m_OptionManager.add(
      "channels", "channels",
      3, 1, null);

    m_OptionManager.add(
      "append-label", "appendLabel",
      false);

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);
  }

  /**
   * Sets the width of the images.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the images.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the images.";
  }

  /**
   * Sets the height of the images.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the images.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the images.";
  }

  /**
   * Sets the number of channels.
   *
   * @param value	the number of channels
   */
  public void setChannels(int value) {
    if (getOptionManager().isValid("channels", value)) {
      m_Channels = value;
      reset();
    }
  }

  /**
   * Returns the number of channels.
   *
   * @return 		the number of channels
   */
  public int getChannels() {
    return m_Channels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String channelsTipText() {
    return "The number of channels.";
  }

  /**
   * Sets whether to append the label.
   *
   * @param value	true if to append the label
   */
  public void setAppendLabel(boolean value) {
    m_AppendLabel = value;
    reset();
  }

  /**
   * Returns whether to append the label.
   *
   * @return 		true if to append the label
   */
  public boolean getAppendLabel() {
    return m_AppendLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String appendLabelTipText() {
    return "Whether to append the label.";
  }

  /**
   * Sets the labels to use.
   *
   * @param value	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use.";
  }

  /**
   * Configures the actual {@link RecordReader} and returns it.
   *
   * @return		the reader
   */
  @Override
  protected RecordReader doConfigureRecordReader() {
    return new ImageRecordReader(m_Width, m_Height, m_Channels, m_AppendLabel, Arrays.asList(BaseObject.toStringArray(m_Labels)));
  }
}
