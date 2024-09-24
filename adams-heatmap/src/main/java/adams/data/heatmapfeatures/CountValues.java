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
 * CountValues.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.heatmapfeatures;

import adams.core.QuickInfoHelper;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Meta-feature-generator that counts numeric values that satisfy the specified min&#47;max range.
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
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
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
 * <pre>-generator &lt;adams.data.heatmapfeatures.AbstractHeatmapFeatureGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The base feature generator to use for generating data to be used as input 
 * &nbsp;&nbsp;&nbsp;for the counts.
 * &nbsp;&nbsp;&nbsp;default: adams.data.heatmapfeatures.Values -converter \"adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet\"
 * </pre>
 * 
 * <pre>-minimum &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum value that the values must satisfy; use NaN (not a number) to 
 * &nbsp;&nbsp;&nbsp;ignore minimum.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 * 
 * <pre>-minimum-included &lt;boolean&gt; (property: minimumIncluded)
 * &nbsp;&nbsp;&nbsp;If enabled, then the minimum value gets included (testing '&lt;=' rather than 
 * &nbsp;&nbsp;&nbsp;'&lt;').
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-maximum &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum value that the values must satisfy; use NaN (not a number) to 
 * &nbsp;&nbsp;&nbsp;ignore maximum.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 * 
 * <pre>-maximum-included &lt;boolean&gt; (property: maximumIncluded)
 * &nbsp;&nbsp;&nbsp;If enabled, then the maximum value gets included (testing '&gt;=' rather than 
 * &nbsp;&nbsp;&nbsp;'&gt;').
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The optional label to use; otherwise min&#47;max are used to construct label.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CountValues
  extends AbstractMetaHeatmapFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -5349388859224578387L;

  /** the placeholder for NaN. */
  public final static String NAN = "NaN";

  /** the minimum value. */
  protected double m_Minimum;

  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;

  /** the maximum value. */
  protected double m_Maximum;

  /** whether the maximum value is included. */
  protected boolean m_MaximumIncluded;

  /** the optional label for the generated feature. */
  protected String m_Label;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Meta-feature-generator that counts numeric values that satisfy the specified min/max range.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "minimum", "minimum",
	    Double.NaN);

    m_OptionManager.add(
	    "minimum-included", "minimumIncluded",
	    false);

    m_OptionManager.add(
	    "maximum", "maximum",
	    Double.NaN);

    m_OptionManager.add(
	    "maximum-included", "maximumIncluded",
	    false);

    m_OptionManager.add(
	    "label", "label",
	    "");
  }

  /**
   * Returns the default generator to use.
   * 
   * @return		the generator
   */
  @Override
  protected AbstractHeatmapFeatureGenerator getDefaultGenerator() {
    return new Values();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generatorTipText() {
    return "The base feature generator to use for generating data to be used as input for the counts.";
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumTipText() {
    return
	"The minimum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore minimum.";
  }

  /**
   * Sets whether to exclude the minimum.
   *
   * @param value	true to exclude minimum
   */
  public void setMinimumIncluded(boolean value) {
    m_MinimumIncluded = value;
    reset();
  }

  /**
   * Returns whether the minimum is included.
   *
   * @return		true if minimum included
   */
  public boolean getMinimumIncluded() {
    return m_MinimumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumIncludedTipText() {
    return "If enabled, then the minimum value gets included (testing '<=' rather than '<').";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumTipText() {
    return
	"The maximum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore maximum.";
  }

  /**
   * Sets whether to exclude the maximum.
   *
   * @param value	true to exclude maximum
   */
  public void setMaximumIncluded(boolean value) {
    m_MaximumIncluded = value;
    reset();
  }

  /**
   * Returns whether the maximum is included.
   *
   * @return		true if maximum included
   */
  public boolean getMaximumIncluded() {
    return m_MaximumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumIncludedTipText() {
    return "If enabled, then the maximum value gets included (testing '>=' rather than '>').";
  }

  /**
   * Sets the custom label to use.
   *
   * @param value	the label to use
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the custom label to use.
   *
   * @return		the label to use
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String labelTipText() {
    return "The optional label to use; otherwise min/max are used to construct label.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "minimum", m_Minimum, "min: ");
    result += " [" + QuickInfoHelper.toString(this, "minimumIncluded", (m_MinimumIncluded ? "incl" : "excl")) + "]";
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum, ", max: ");
    result += " [" + QuickInfoHelper.toString(this, "maximumIncluded", (m_MaximumIncluded ? "incl" : "excl")) + "]";
    result += QuickInfoHelper.toString(this, "label", (m_Label.isEmpty() ? "automatic" : m_Label), ", label: ");
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return m_Converter.getDatasetFormat();
  }

  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return m_Converter.getRowFormat();
  }

  /**
   * Creates the header from a template heatmap.
   *
   * @param map		the heatmap to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(Heatmap map) {
    HeaderDefinition 	result;
    String		name;

    if (m_Label.isEmpty()) {
      if (m_MinimumIncluded)
	name = "[";
      else
	name = "(";
      name += (Double.isNaN(m_Minimum) ? "-Inf" : m_Minimum) + ";" + (Double.isNaN(m_Maximum) ? "+Inf" : m_Maximum);
      if (m_MaximumIncluded)
	name += "]";
      else
	name += ")";
    }
    else {
      name = m_Label;
    }

    result = new HeaderDefinition();
    result.add(name, DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param map		the heatmap to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(Heatmap map) {
    List<Object>[]	result;
    List<Object>[] 	rows;
    List<Object> 	row;
    List<Object> 	countData;
    int			n;
    int			i;
    int			count;
    double		value;
    boolean 		use;

    rows   = m_Generator.generateRows(map);
    result = new List[rows.length];

    for (n = 0; n < rows.length; n++) {
      row       = rows[n];
      count     = 0;
      countData = new ArrayList();
      result[n] = countData;
      for (i = 0; i < row.size(); i++) {
	if (!(row.get(i) instanceof Number))
	  continue;
	value = ((Number) row.get(i)).doubleValue();

	use = true;
	if (!Double.isNaN(m_Minimum)) {
	  if (m_MinimumIncluded) {
	    if (value < m_Minimum)
	      use = false;
	  }
	  else {
	    if (value <= m_Minimum)
	      use = false;
	  }
	}
	if (!Double.isNaN(m_Maximum)) {
	  if (m_MaximumIncluded) {
	    if (value > m_Maximum)
	      use = false;
	  }
	  else {
	    if (value >= m_Maximum)
	      use = false;
	  }
	}
	if (use)
	  count++;
      }
      countData.add((double) count);
    }

    return result;
  }
}
