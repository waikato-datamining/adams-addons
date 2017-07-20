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
 * DefaultCNTK.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.ml.cntk.modelapplier.ImageChannels;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Scales the image and turns the intensities into features (order is BGR per pixel).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for the feature names.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width to scale to.
 * &nbsp;&nbsp;&nbsp;default: 32
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height to scale to.
 * &nbsp;&nbsp;&nbsp;default: 32
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-channels &lt;GRAY|BGR&gt; (property: channels)
 * &nbsp;&nbsp;&nbsp;The image channels to use.
 * &nbsp;&nbsp;&nbsp;default: GRAY
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
@MixedCopyright(
  author = "CNTK",
  copyright = "Microsoft",
  license = License.MIT,
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java"
)
public class DefaultCNTK
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the image width to scale to. */
  protected int m_Width;

  /** the image height to scale to. */
  protected int m_Height;

  /** the channels to use. */
  protected ImageChannels m_Channels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Scales the image and turns the intensities into features (order is BGR per pixel).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      32, 1, null);

    m_OptionManager.add(
      "height", "height",
      32, 1, null);

    m_OptionManager.add(
      "channels", "channels",
      ImageChannels.GRAY);
  }

  /**
   * Sets the width to scale to.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width to scale to.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width to scale to.";
  }

  /**
   * Sets the height to scale to.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height to scale to.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height to scale to.";
  }

  /**
   * Sets the image channels to use.
   *
   * @param value	the channels
   */
  public void setChannels(ImageChannels value) {
    m_Channels = value;
    reset();
  }

  /**
   * Returns the image channels to use.
   *
   * @return		the channels
   */
  public ImageChannels getChannels() {
    return m_Channels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String channelsTipText() {
    return "The image channels to use.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;
    int			i;

    result = new HeaderDefinition();
    switch (m_Channels) {
      case GRAY:
	for (i = 0; i < m_Width * m_Height; i++)
	  result.add("Intensity-" + (i+1), DataType.NUMERIC);
	break;

      case BGR:
	for (i = 0; i < m_Width * m_Height; i++)
	  result.add("Intensity-" + (i+1) + "-B", DataType.NUMERIC);
	for (i = 0; i < m_Width * m_Height; i++)
	  result.add("Intensity-" + (i+1) + "-G", DataType.NUMERIC);
	for (i = 0; i < m_Width * m_Height; i++)
	  result.add("Intensity-" + (i+1) + "-R", DataType.NUMERIC);
	break;

      default:
	throw new IllegalStateException("Unhandled image channels: " + m_Channels);
    }

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    BufferedImage		bImg;
    BufferedImage 		bmp;
    Image 			resized;

    result    = new List[1];
    result[0] = new ArrayList<>();

    bmp     = img.toBufferedImage();
    resized = bmp.getScaledInstance(m_Width, m_Height, Image.SCALE_DEFAULT);
    bImg    = new BufferedImage(resized.getWidth(null), resized.getHeight(null), BufferedImage.TYPE_INT_RGB);
    bImg.getGraphics().drawImage(resized, 0, 0, null);

    switch (m_Channels) {
      case GRAY:
	for (int h = 0; h < bImg.getHeight(); h++) {
	  for (int w = 0; w < bImg.getWidth(); w++) {
	    Color color = new Color(bImg.getRGB(w, h));
	    result[0].add(color.getBlue());
	  }
	}
	break;

      case BGR:
	for (int c = 0; c < 3; c++) {
	  for (int h = 0; h < bImg.getHeight(); h++) {
	    for (int w = 0; w < bImg.getWidth(); w++) {
	      Color color = new Color(bImg.getRGB(w, h));
	      if (c == 0)
		result[0].add(color.getBlue());
	      else if (c == 1)
		result[0].add(color.getGreen());
	      else
		result[0].add(color.getRed());
	    }
	  }
	}
	break;

      default:
	throw new IllegalStateException("Unhandled image channels: " + m_Channels);
    }

    return result;
  }
}
