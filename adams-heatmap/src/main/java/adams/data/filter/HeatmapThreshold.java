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
 * HeatmapThreshold.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.filter.heatmapthreshold.AbstractHeatmapThreshold;
import adams.data.filter.heatmapthreshold.Manual;
import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Replaces all intensity values that are either below or above a user-specified threshold, depending on the selected threshold type, using the pre-defined replacement value.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;ABOVE|BELOW&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of threshold to use: if BELOW then all values that fall below the 
 * &nbsp;&nbsp;&nbsp;threshold are zeroed, if ABOVE then all values that are above the threshold 
 * &nbsp;&nbsp;&nbsp;are zeroed.
 * &nbsp;&nbsp;&nbsp;default: BELOW
 * </pre>
 * 
 * <pre>-threshold &lt;adams.data.filter.heatmapthreshold.AbstractHeatmapThreshold&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.heatmapthreshold.Manual
 * </pre>
 * 
 * <pre>-replacement &lt;double&gt; (property: replacement)
 * &nbsp;&nbsp;&nbsp;The replacement value to use.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * </pre>
 * 
 * <pre>-replace-with-missing &lt;boolean&gt; (property: replaceWithMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, the values are replaced with missing values rather than actual 
 * &nbsp;&nbsp;&nbsp;values.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-missing-values-handling &lt;SKIP|REPLACE&gt; (property: missingValuesHandling)
 * &nbsp;&nbsp;&nbsp;Determines how missing values are handled when processing the heatmap, eg 
 * &nbsp;&nbsp;&nbsp;whether they get skipped or always replaced.
 * &nbsp;&nbsp;&nbsp;default: SKIP
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapThreshold
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

  /**
   * Determines how missing values are treated.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum MissingValuesHandling {
    /** ignores missing values. */
    SKIP,
    /** always replaces misssing values. */
    REPLACE,
  }

  /** the type of threshold. */
  protected Type m_Type;

  /** the threshold. */
  protected AbstractHeatmapThreshold m_Threshold;

  /** the replacement value. */
  protected double m_Replacement;

  /** whether to replace with missing values. */
  protected boolean m_ReplaceWithMissing;

  /** how missing values are treated. */
  protected MissingValuesHandling m_MissingValuesHandling;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Replaces all intensity values that are either below or above a user-specified "
      + "threshold, depending on the selected threshold type, using the pre-defined "
      + "replacement value.";
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
      new Manual());

    m_OptionManager.add(
      "replacement", "replacement",
      -1.0);

    m_OptionManager.add(
      "replace-with-missing", "replaceWithMissing",
      false);

    m_OptionManager.add(
      "missing-values-handling", "missingValuesHandling",
      MissingValuesHandling.SKIP);
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
   * Sets the threshold algorithm to use.
   *
   * @param value 	the algorithm
   */
  public void setThreshold(AbstractHeatmapThreshold value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold algorithm in use.
   *
   * @return 		the algorithm
   */
  public AbstractHeatmapThreshold getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold algorithm to use.";
  }

  /**
   * Sets the replacement value to use.
   *
   * @param value 	the replacement
   */
  public void setReplacement(double value) {
    m_Replacement = value;
    reset();
  }

  /**
   * Returns the replacement value in use.
   *
   * @return 		the replacement
   */
  public double getReplacement() {
    return m_Replacement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replacementTipText() {
    return "The replacement value to use.";
  }

  /**
   * Sets whether to replace the values with missing values instead.
   *
   * @param value 	true if to replace with missing
   */
  public void setReplaceWithMissing(boolean value) {
    m_ReplaceWithMissing = value;
    reset();
  }

  /**
   * Returns whether to replace the values with missing values instead.
   *
   * @return 		true if to replace with missing
   */
  public boolean getReplaceWithMissing() {
    return m_ReplaceWithMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceWithMissingTipText() {
    return "If enabled, the values are replaced with missing values rather than actual values.";
  }

  /**
   * Sets how missing values are handled.
   *
   * @param value 	the handling
   */
  public void setMissingValuesHandling(MissingValuesHandling value) {
    m_MissingValuesHandling = value;
    reset();
  }

  /**
   * Returns how missing values are handled.
   *
   * @return 		the handling
   */
  public MissingValuesHandling getMissingValuesHandling() {
    return m_MissingValuesHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValuesHandlingTipText() {
    return
        "Determines how missing values are handled when processing the heatmap, "
	  + "eg whether they get skipped or always replaced.";
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
    double		value;
    double		threshold;

    result    = data.getClone();
    threshold = m_Threshold.calcThreshold(data);
    if (isLoggingEnabled())
      getLogger().info("Threshold: " + threshold);

    for (i = 0; i < data.size(); i++) {
      value = data.get(i);
      if (Heatmap.isMissingValue(value)) {
	switch (m_MissingValuesHandling) {
	  case SKIP:
	    // don't do anything;
	    break;

	  case REPLACE:
	    if (!m_ReplaceWithMissing)
	      result.set(i, m_Replacement);
	    break;

	  default:
	    throw new IllegalStateException("Unhandled missing values handling: " + m_MissingValuesHandling);
	}
      }
      else {
	switch (m_Type) {
	  case BELOW:
	    if (value < threshold) {
	      if (m_ReplaceWithMissing)
		result.setMissing(i);
	      else
		result.set(i, m_Replacement);
	    }
	    break;

	  case ABOVE:
	    if (value > threshold) {
	      if (m_ReplaceWithMissing)
		result.setMissing(i);
	      else
		result.set(i, m_Replacement);
	    }
	    break;

	  default:
	    throw new IllegalStateException("Unhandled type: " + m_Type);
	}
      }
    }

    return result;
  }
}
