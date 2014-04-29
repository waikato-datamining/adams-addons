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
 * Threshold.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Zeroes all intensity values that are either below or above a user-specified threshold, depending on the selected threshold type.
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
 * <pre>-type &lt;ABOVE|BELOW&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of threshold to use: if BELOW then all values that fall below the
 * &nbsp;&nbsp;&nbsp;threshold are zeroed, if ABOVE then all values that are above the threshold
 * &nbsp;&nbsp;&nbsp;are zeroed.
 * &nbsp;&nbsp;&nbsp;default: BELOW
 * </pre>
 *
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold in percent (0.0 - 100.0).
 * &nbsp;&nbsp;&nbsp;default: 90.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 100.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Threshold
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -1306518673446335794L;

  /**
   * The type of threshold.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** above. */
    ABOVE,
    /** below. */
    BELOW
  }

  /** the type of threshold. */
  protected Type m_Type;

  /** the threshold percentage. */
  protected double m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Zeroes all intensity values that are either below or above a user-specified "
      + "threshold, depending on the selected threshold type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"type", "type",
	Type.BELOW);

    m_OptionManager.add(
	"threshold", "threshold",
	90.0, 0.0, 100.0);
  }

  /**
   * Sets the type of threshold to use.
   *
   * @param value 	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of threshold in use.
   *
   * @return 		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return
        "The type of threshold to use: if " + Type.BELOW + " then all values "
      + "that fall below the threshold are zeroed, if " + Type.ABOVE + " then "
      + "all values that are above the threshold are zeroed.";
  }

  /**
   * Sets the threshold to use.
   *
   * @param value 	the threshold (0.0 - 100.0)
   */
  public void setThreshold(double value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold in use.
   *
   * @return 		the threshold (0.0 - 100.0)
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold in percent (0.0 - 100.0).";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected Heatmap processData(Heatmap data) {
    Heatmap		result;
    int			i;
    double		thr;
    double		value;

    result = data.getHeader();
    thr    = (data.getMax() - data.getMin()) * m_Threshold / 100.0 + data.getMin();
    for (i = 0; i < data.size(); i++) {
      value = data.get(i);

      switch (m_Type) {
	case BELOW:
	  if (value >= thr)
	    result.set(i, value);
	  break;

	case ABOVE:
	  if (value <= thr)
	    result.set(i, value);
	  break;

	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }
    }

    return result;
  }
}
