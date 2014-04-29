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
 * FeatureGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.featuregenerator.AbstractFeatureGenerator;
import adams.data.heatmap.Heatmap;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A filter that modifies the quantitation reports of chromatograms being passed through. The supplied quantitation report filter updates/modifies the quantitation report.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-debug-out &lt;java.io.File&gt; (property: debugOutputFilePrefix)
 *         If the file is not pointing to a directory, then the filtered data gets
 *         dumped to a file with a filename consisting of this prefix, the database
 *          ID and the extension 'chrom'.
 *         default: .
 * </pre>
 *
 * <pre>-filter &lt;gcms.data.quantitation.AbstractFeatureGenerator [options]&gt; (property: filter)
 *         The filter to use for updating/modifying the quantitation report.
 *         default: gcms.data.quantitation.PassThrough -debug-out .
 * </pre>
 *
 * Default options for gcms.data.quantitation.PassThrough (-filter/filter):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-debug-out &lt;java.io.File&gt; (property: debugOutputFilePrefix)
 *         If the file is not pointing to a directory, then the filtered data gets
 *         dumped to a file with a filename consisting of this prefix, the database
 *          ID and the extension 'chrom'.
 *         default: .
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to filter
 */
public class FeatureGenerator
  extends AbstractDatabaseConnectionFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -754895778604425899L;

  /** the report filter. */
  protected AbstractFeatureGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A filter that adds features to the report. The supplied feature "
      + "generator updates/modifies the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

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
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractFeatureGenerator value) {
    m_Generator = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the current generator.
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
    return "The generator to use for updating/modifying the report.";
  }

  /**
   * Updates the database connection in the filter.
   */
  protected void updateDatabaseConnection() {
    if (m_Generator instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Generator).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected Heatmap processData(Heatmap data) {
    Heatmap	result;

    result = m_Generator.generate((Heatmap) data.getClone());
    // free up memory
    m_Generator.cleanUp();

    return result;
  }
}
