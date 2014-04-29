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
 * IntensityInstanceGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import adams.data.heatmap.Heatmap;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * A generator for turning a heatmap's intensities and fields of its report into weka.core.Instance objects.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-add-db-id (property: addDatabaseID)
 * &nbsp;&nbsp;&nbsp;If set to true, then the database ID will be added to the output.
 * </pre>
 *
 * <pre>-add-herd-number (property: addHerdNumber)
 * &nbsp;&nbsp;&nbsp;If set to true, then the herd number will be added to the output.
 * </pre>
 *
 * <pre>-add-cow-number (property: addCowNumber)
 * &nbsp;&nbsp;&nbsp;If set to true, then the cow number will be added to the output.
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-min-intensity &lt;double&gt; (property: minIntensity)
 * &nbsp;&nbsp;&nbsp;The minimum intensity to keep.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 *
 * <pre>-max-intensity &lt;double&gt; (property: maxIntensity)
 * &nbsp;&nbsp;&nbsp;The maximum intensity to keep.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IntensityInstanceGenerator
  extends AbstractFieldInstanceGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8643534190230443073L;

  /** the minimum m/z ratio to keep. */
  protected double m_MinIntensity;

  /** the maximum m/z ratio to keep. */
  protected double m_MaxIntensity;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A generator for turning a heatmap's intensities and fields of its report "
      + "into weka.core.Instance objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-intensity", "minIntensity",
	    -1.0, -1.0, null);

    m_OptionManager.add(
	    "max-intensity", "maxIntensity",
	    -1.0, -1.0, null);
  }

  /**
   * Sets the minimum intensity to keep.
   *
   * @param value	the intensity
   */
  public void setMinIntensity(double value) {
    if ((value > 0) || (value == -1.0)) {
      m_MinIntensity = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName()
	  + ": only positive numbers or -1 are allowed for the minimum intensity "
	  + "(provided: " + value + ")!");
    }
  }

  /**
   * Returns the minimum intensity to keep.
   *
   * @return		the intensity
   */
  public double getMinIntensity() {
    return m_MinIntensity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minIntensityTipText() {
    return "The minimum intensity to keep.";
  }

  /**
   * Sets the maximum intensity to keep.
   *
   * @param value	the intensity
   */
  public void setMaxIntensity(double value) {
    if ((value > 0) || (value == -1.0)) {
      m_MaxIntensity = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName()
	  + ": only positive numbers or -1 are allowed for the maximum intensity "
	  + "(provided: " + value + ")!");
    }
  }

  /**
   * Returns the maximum intensity to keep.
   *
   * @return		the intensity
   */
  public double getMaxIntensity() {
    return m_MaxIntensity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxIntensityTipText() {
    return "The maximum intensity to keep.";
  }

  /**
   * Checks whether the number of waves are the same.
   *
   * @param data	the input data
   */
  @Override
  protected void checkHeader(Heatmap data) {
    int		size;

    size = m_OutputHeader.numAttributes();
    if (m_AddDatabaseID)
      size--;

    if (size != m_Fields.length + data.size())
      throw new IllegalStateException(
	  "Number of fields+heatmap points and output attributes differ (" + data + "): "
	  + m_Fields.length + "+" + data.size()  + " != " + size);
  }

  /**
   * Generates the header of the output data.
   *
   * @param data	the input data
   */
  @Override
  protected void generateHeader(Heatmap data) {
    ArrayList<Attribute>	atts;
    StringBuilder		name;
    int				i;

    atts = new ArrayList<Attribute>();

    // bins
    for (i = 0; i < data.size(); i++)
      atts.add(new Attribute("Intensity-" + (i+1)));

    // fields
    name = new StringBuilder();
    for (Field target: m_Fields)
      atts.add(createAttribute(target, name));

    m_OutputHeader = new Instances(getClass().getName() + "-" + name.toString(), atts, 0);
  }

  /**
   * Generates the actual data.
   *
   * @param data	the input data to transform
   * @return		the generated data
   */
  @Override
  protected Instance generateOutput(Heatmap data) {
    Instance		result;
    double[]		values;
    int			i;
    int			offset;
    double		min;
    double		max;

    values = new double[m_OutputHeader.numAttributes()];

    for (i = 0; i < values.length; i++)
      values[i] = Utils.missingValue();
    offset = 0;
    if (m_AddDatabaseID)
      offset++;

    // intensities
    min = (m_MinIntensity == -1.0) ? data.getMin() : m_MinIntensity;
    max = (m_MaxIntensity == -1.0) ? data.getMax() : m_MaxIntensity;
    for (i = 0; i < data.size(); i++)
      values[offset + i] = Math.min(Math.max(data.get(i), min), max);

    // fields
    addFields(data, values);

    result = new DenseInstance(1.0, values);
    result.setDataset(m_OutputHeader);

    return result;
  }
}
