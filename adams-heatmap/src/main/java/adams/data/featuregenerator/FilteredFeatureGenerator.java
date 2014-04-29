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
 * FilteredFeatureGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.featuregenerator;

import adams.data.filter.AbstractFilter;
import adams.data.heatmap.Heatmap;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * This feature generator first pushes the data through the provided data filter before applying the actual feature generator. The updated data obtained from the feature generator then replaces the report of the original data container.
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in the field for the generated features.
 * &nbsp;&nbsp;&nbsp;default: Feature
 * </pre>
 *
 * <pre>-filter &lt;adams.data.filter.AbstractFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to apply to the heatmap before pushing it through the feature
 * &nbsp;&nbsp;&nbsp;generator.
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.PassThrough
 * </pre>
 *
 * <pre>-generator &lt;adams.data.featuregenerator.AbstractFeatureGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to apply to the filtered data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featuregenerator.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredFeatureGenerator
  extends AbstractDatabaseConnectionFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 8646651693938769168L;

  /** the pre-filter for filtering the data. */
  protected AbstractFilter m_Filter;

  /** the actual feature generator. */
  protected AbstractFeatureGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "This feature generator first pushes the data through the provided data filter "
      + "before applying the actual feature generator. The updated data obtained "
      + "from the feature generator then replaces the report of the original "
      + "data container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new adams.data.filter.PassThrough());

    m_OptionManager.add(
	    "generator", "generator",
	    new adams.data.featuregenerator.PassThrough());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the filter.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractFilter value) {
    m_Filter = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the filter.
   *
   * @return		the filter
   */
  public AbstractFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filterTipText() {
    return "The filter to apply to the heatmap before pushing it through the feature generator.";
  }

  /**
   * Sets the generator.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractFeatureGenerator value) {
    m_Generator = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the generator.
   *
   * @return		the generator
   */
  public AbstractFeatureGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String generatorTipText() {
    return "The generator to apply to the filtered data.";
  }

  /**
   * Updates the database connection in dependent schemes.
   */
  protected void updateDatabaseConnection() {
    if (m_Filter instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Filter).setDatabaseConnection(getDatabaseConnection());
    if (m_Generator instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Generator).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Processes the data/report.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  protected Heatmap processData(Heatmap data) {
    Heatmap	result;
    Heatmap	filtered;

    filtered = (Heatmap) m_Filter.filter(data);
    filtered = m_Generator.generate(filtered);
    result   = data.getClone();
    result.setReport(filtered.getReport().getClone());

    return result;
  }
}
