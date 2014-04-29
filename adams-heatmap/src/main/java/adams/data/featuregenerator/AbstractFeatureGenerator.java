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
 * AbstractFeatureGenerator.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.featuregenerator;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Abstract base class for feature generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFeatureGenerator
  extends AbstractOptionHandler
  implements Comparable, ShallowCopySupporter<AbstractFeatureGenerator> {

  /** for serialization. */
  private static final long serialVersionUID = -7572598575382208115L;

  /** the default prefix for the feature fields. */
  public final static String PREFIX_FEATURE = "Feature";

  /** the prefix for the feature fields. */
  protected String m_Prefix;

  /**
   * Resets the generator.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    getDefaultPrefix());
  }

  /**
   * Returns the default prefix for the generated features.
   *
   * @return		the default prefix
   */
  protected String getDefaultPrefix() {
    return PREFIX_FEATURE;
  }

  /**
   * Sets the prefix to use for the generated features.
   *
   * @param value	the prefix to use
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix in use for the generated features.
   *
   * @return		the prefix in use
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String prefixTipText() {
    return "The prefix to use in the field for the generated features.";
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    reset();
  }

  /**
   * Generates the features and adds them to the report.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  public Heatmap generate(Heatmap data) {
    Heatmap	result;

    checkData(data);
    result = processData(data);

    return result;
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to process
   */
  protected void checkData(Heatmap data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Adds the specified feature. Uses the class name (without packages) as
   * feature suffix.
   *
   * @param data	the heatmap to add the feature to
   * @param value	the value of the feature
   * @return		the previous value for this feature if any, otherwise null
   */
  protected Double addFeature(Heatmap data, double value) {
    return addFeature(data, getClass().getName().replaceAll(".*\\.", ""), value);
  }

  /**
   * Adds the specified feature. Uses an explicit suffix for the feature.
   *
   * @param data	the heatmap to add the feature to
   * @param suffix	the suffix of the feature
   * @param value	the value of the feature
   * @return		the previous value for this feature if any, otherwise null
   */
  protected Double addFeature(Heatmap data, String suffix, double value) {
    Double	result;
    Field	field;
    Report	report;

    result = null;

    if (!data.hasReport())
      return result;

    report = data.getReport();
    field  = new Field(m_Prefix + Field.SEPARATOR + suffix, DataType.NUMERIC);
    report.addField(field);
    if (report.hasValue(field))
      result = report.getDoubleValue(field);
    report.setValue(field, value);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  protected abstract Heatmap processData(Heatmap data);

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractFeatureGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractFeatureGenerator shallowCopy(boolean expand) {
    return (AbstractFeatureGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of generators.
   *
   * @return		the generator classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(AbstractFeatureGenerator.class);
  }

  /**
   * Instantiates the generator with the given options.
   *
   * @param classname	the classname of the generator to instantiate
   * @param options	the options for the generator
   * @return		the instantiated generator or null if an error occurred
   */
  public static AbstractFeatureGenerator forName(String classname, String[] options) {
    AbstractFeatureGenerator	result;

    try {
      result = (AbstractFeatureGenerator) OptionUtils.forName(AbstractFeatureGenerator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the generator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			generator to instantiate
   * @return		the instantiated generator
   * 			or null if an error occurred
   */
  public static AbstractFeatureGenerator forCommandLine(String cmdline) {
    return (AbstractFeatureGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
