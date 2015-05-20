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
 * Centroid.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.filter.heatmapcrop.CropToCentroid;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * Computes the centroid of a heatmap and adds this to the heatmap's report.<br>
 * It is possible to perform multiple iterations and each time with a shrunken heatmap. This is useful if the centroid cannot be computed reliably the first time.<br>
 * For more information on the centroid calculation, see:<br>
 * WikiPedia. Image moment.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Image moment},
 *    HTTP = {http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Image_moment}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-num-iterations &lt;int&gt; (property: numIterations)
 * &nbsp;&nbsp;&nbsp;The number of iterations to perform for finding the centroid.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-shrink-factor &lt;double&gt; (property: shrinkFactor)
 * &nbsp;&nbsp;&nbsp;The factor to shrink the heatmap with after each iteration.
 * &nbsp;&nbsp;&nbsp;default: 0.75
 * &nbsp;&nbsp;&nbsp;minimum: 0.0010
 * &nbsp;&nbsp;&nbsp;maximum: 0.999
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapCentroid
  extends AbstractFilter<Heatmap>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2270876952032422552L;

  /** the X of the centroid. */
  public final static String CENTROID_X = "Centroid.X";

  /** the Y of the centroid. */
  public final static String CENTROID_Y = "Centroid.Y";

  /** the number of iterations to peform. */
  protected int m_NumIterations;

  /** the factor to shrink the heatmap after each iteration. */
  protected double m_ShrinkFactor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Computes the centroid of a heatmap and adds this to the heatmap's "
      + "report.\n"
      + "It is possible to perform multiple iterations and each time with a "
      + "shrunken heatmap. This is useful if the centroid cannot be computed "
      + "reliably the first time.\n"
      + "For more information on the centroid calculation, see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(TechnicalInformation.Field.AUTHOR, "WikiPedia");
    result.setValue(TechnicalInformation.Field.TITLE, "Image moment");
    result.setValue(TechnicalInformation.Field.HTTP, "http://en.wikipedia.org/wiki/Image_moment");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"num-iterations", "numIterations",
	1, 1, null);

    m_OptionManager.add(
	"shrink-factor", "shrinkFactor",
	0.75, 0.001, 0.999);
  }

  /**
   * Sets the number of iterations for finding the centroid.
   *
   * @param value 	the number of iterations
   */
  public void setNumIterations(int value) {
    if (value > 0) {
      m_NumIterations = value;
      reset();
    }
    else {
      getLogger().severe("NumIterations must be > 0, provided: " + value);
    }
  }

  /**
   * Returns the number of iterations for finding the centroid.
   *
   * @return 		the number of iterations
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The number of iterations to perform for finding the centroid.";
  }

  /**
   * Sets the factor to shrink the heatmap with after each iteration.
   *
   * @param value 	the factor
   */
  public void setShrinkFactor(double value) {
    if ((value > 0) && (value < 1.0)) {
      m_ShrinkFactor = value;
      reset();
    }
    else {
      getLogger().severe("ShrinkFactor must be 0 < x < 1, provided: " + value);
    }
  }

  /**
   * Returns the factor to shrink the heatmap with after each iteration
   *
   * @return 		the factor
   */
  public double getShrinkFactor() {
    return m_ShrinkFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shrinkFactorTipText() {
    return "The factor to shrink the heatmap with after each iteration.";
  }

  /**
   * Finds the centroid.
   *
   * @param data	the data to find the centroid in
   * @return		the data with the updated report
   */
  protected Heatmap findCentroid(Heatmap data) {
    Heatmap		result;
    double		m00;
    double		m01;
    double		m10;
    int			x;
    int			y;
    double		x_bar;
    double		y_bar;
    Field		field;

    result = data.getClone();

    // Centroid: {x_bar, y_bar} = {M10/M00, M01/M00}
    m00 = 0;
    m01 = 0;
    m10 = 0;
    for (y = 0; y < data.getHeight(); y++) {
      for (x = 0; x < data.getWidth(); x++) {
	m00 +=     data.get(y, x);
	m10 += x * data.get(y, x);
	m01 += y * data.get(y, x);
      }
    }

    if (m00 > 0) {
      x_bar = m10 / m00;
      y_bar = m01 / m00;
    }
    else {
      x_bar = -1.0;
      y_bar = -1.0;
    }

    field = new Field(CENTROID_X, DataType.NUMERIC);
    result.getReport().addField(field);
    result.getReport().setValue(field, x_bar);

    field = new Field(CENTROID_Y, DataType.NUMERIC);
    result.getReport().addField(field);
    result.getReport().setValue(field, y_bar);

    if (isLoggingEnabled()) {
      getLogger().info("M00: " + m00 + ", M10: " + m10 + ", M01: " + m01);
      getLogger().info("x bar: " + x_bar + ", y bar: " + y_bar);
    }

    return result;
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Heatmap processData(Heatmap data) {
    Heatmap		result;
    Heatmap		current;
    int			i;
    double		oldX;
    double		oldY;
    double		newX;
    double		newY;
    int			width;
    int			height;
    int			offsetX;
    int			offsetY;

    result  = data.getClone();
    current = data.getClone();

    oldX    = -1;
    oldY    = -1;
    offsetX = 0;
    offsetY = 0;
    for (i = 0; i < m_NumIterations; i++) {
      current = findCentroid(current);

      // analyze centroid
      newX = current.getReport().getDoubleValue(CENTROID_X);
      newY = current.getReport().getDoubleValue(CENTROID_Y);
      result.getReport().setNumericValue(CENTROID_X, newX + offsetX);
      result.getReport().setNumericValue(CENTROID_Y, newY + offsetY);
      if ((i < m_NumIterations - 1) && (newX == oldX) && (newY == oldY)) {
	result.getNotes().addWarning(getClass(), "Stopped after iteration #" + (i+1) + " since centroid did not change.");
	break;
      }

      // crop
      width   = (int) Math.round(data.getWidth()  * m_ShrinkFactor);
      height  = (int) Math.round(data.getHeight() * m_ShrinkFactor);
      current = CropToCentroid.crop(current, (int) Math.round(newY), (int) Math.round(newX), height, width);

      // offset for next iteration
      offsetX += current.getReport().getDoubleValue(CropToCentroid.CROP_LEFT).intValue();
      offsetY += current.getReport().getDoubleValue(CropToCentroid.CROP_TOP).intValue();
    }

    return result;
  }
}
