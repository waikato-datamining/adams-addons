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
 * MultiGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.featuregenerator;

import adams.core.option.OptionUtils;
import adams.data.heatmap.Heatmap;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A meta-generator that applies multiple feature generators to the data.
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in the field for the generated features.
 * &nbsp;&nbsp;&nbsp;default: Feature
 * </pre>
 *
 * <pre>-generator &lt;adams.data.featuregenerator.AbstractFeatureGenerator&gt; [-generator ...] (property: subGenerators)
 * &nbsp;&nbsp;&nbsp;The array of generators to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featuregenerator.PassThrough
 * </pre>
 *
 * <pre>-override-prefix (property: overridePrefix)
 * &nbsp;&nbsp;&nbsp;If enabled, then this prefix will override the ones specified by the sub-generators.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiGenerator
  extends AbstractDatabaseConnectionFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 805661569976845842L;

  /** the generators. */
  protected AbstractFeatureGenerator[] m_Generators;

  /** whether to override the sub-generators' prefix. */
  protected boolean m_OverridePrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-generator that applies multiple feature generators to the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "subGenerators",
	    new AbstractFeatureGenerator[]{new PassThrough()});

    m_OptionManager.add(
	    "override-prefix", "overridePrefix",
	    false);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the generators to use.
   *
   * @param value	the generators to use
   */
  public void setSubGenerators(AbstractFeatureGenerator[] value) {
    if (value != null) {
      m_Generators = value;
      updateDatabaseConnection();
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": generators cannot be null!");
    }
  }

  /**
   * Returns the generators in use.
   *
   * @return		the generators
   */
  public AbstractFeatureGenerator[] getSubGenerators() {
    return m_Generators;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String subGeneratorsTipText() {
    return "The array of generators to use.";
  }

  /**
   * Sets whether to override the generators' prefix.
   *
   * @param value	if true then the prefix of the sub-generators will be
   * 			overridden
   */
  public void setOverridePrefix(boolean value) {
    m_OverridePrefix = value;
    reset();
  }

  /**
   * Returns whether the prefix of the generators are overridden.
   *
   * @return		true if the prefix of the sub-generators gets overridden
   */
  public boolean getOverridePrefix() {
    return m_OverridePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overridePrefixTipText() {
    return "If enabled, then this prefix will override the ones specified by the sub-generators.";
  }

  /**
   * Updates the database connection in the sub-generators.
   */
  @Override
  protected void updateDatabaseConnection() {
    for (AbstractFeatureGenerator generator: m_Generators) {
      if (generator instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) generator).setDatabaseConnection(getDatabaseConnection());
    }
  }

  /**
   * Performs the actual feature generation.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  @Override
  protected Heatmap processData(Heatmap data) {
    Heatmap			result;
    int				i;
    Heatmap			input;
    Heatmap			output;
    AbstractFeatureGenerator	generator;

    input  = data;
    output = data;  // in case there are no generators provided

    for (i = 0; i < m_Generators.length; i++) {
      getLogger().info(
	    "Generator " + (i+1) + "/" + m_Generators.length + ": "
	    + OptionUtils.getCommandLine(m_Generators[i]));

      generator = m_Generators[i].shallowCopy(true);
      if (m_OverridePrefix)
	generator.setPrefix(getPrefix());
      output    = generator.generate(input);
      generator.cleanUp();

      // prepare input for next generator
      input = output;
    }

    getLogger().info("Finished!");

    // final output
    result = output.getClone();

    return result;
  }
}
