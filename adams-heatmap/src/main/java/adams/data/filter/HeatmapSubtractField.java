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
 * SubtractField.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * Subtracts the value of the numeric field from all non-zero heatmap values.<br>
 * Values that fall below zero are automatically set to zero (minimum value for a heatmap).
 * <br><br>
 <!-- globalinfo-end -->
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
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The (numeric) field in the report to subtract from the heatmap values.
 * &nbsp;&nbsp;&nbsp;default: Heatsense\\tTemperature[N]
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapSubtractField
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -7041791005249685082L;

  /** the field containing the numeric value. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Subtracts the value of the numeric field from all non-zero heatmap values.\n"
      + "Values that fall below zero are automatically set to zero (minimum value for a heatmap).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"field", "field",
	new Field("Temperature", DataType.NUMERIC));
  }

  /**
   * Sets the field to normalize to.
   *
   * @param value 	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to normalize to.
   *
   * @return 		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The (numeric) field in the report to subtract from the heatmap values.";
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
    Double[]		values;
    double		subtract;
    int			i;

    if (!data.hasReport()) {
      result = data.getClone();
      result.getNotes().addError(getClass(), "No report attached!");
      return result;
    }
    if (!data.getReport().hasValue(m_Field)) {
      result = data.getClone();
      result.getNotes().addError(getClass(), "Field '" + m_Field + "' not found in report!");
      return result;
    }
    if (!(data.getReport().getValue(m_Field) instanceof Double)) {
      result = data.getClone();
      result.getNotes().addError(getClass(), "Field '" + m_Field + "' not numeric!");
      return result;
    }

    subtract = data.getReport().getDoubleValue(m_Field);

    result = data.getHeader();
    values = data.toDoubleArray();
    for (i = 0; i < values.length; i++) {
      if (values[i] > 0) {
	values[i] -= subtract;
	if (values[i] < 0.0)
	  values[i] = 0.0;
      }
    }
    result.set(values);

    return result;
  }
}
