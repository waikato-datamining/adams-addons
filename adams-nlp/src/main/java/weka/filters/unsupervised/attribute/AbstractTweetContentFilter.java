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

/**
 * AbstractTweetContentFilter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.WekaException;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Ancestor for filters that parse tweet content.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTweetContentFilter
  extends SimpleBatchFilter {

  private static final long serialVersionUID = -7287415147864106887L;

  public static final String ATT_NAME = "att-name";

  /** the name of the string attribute to process. */
  protected String m_AttributeName = getDefaultAttributeName();

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, attributeNameTipText(), getDefaultAttributeName(), ATT_NAME);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setAttributeName(WekaOptionUtils.parse(options, ATT_NAME, getDefaultAttributeName()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, ATT_NAME, getAttributeName());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default attribute name.
   *
   * @return		the default
   */
  protected String getDefaultAttributeName() {
    return "content";
  }

  /**
   * Sets the name of the string attribute to process.
   *
   * @param value	the name
   */
  public void setAttributeName(String value) {
    m_AttributeName = value;
    reset();
  }

  /**
   * Returns the name of the string attribute to process.
   *
   * @return		the name
   */
  public String getAttributeName() {
    return m_AttributeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeNameTipText() {
    return "The name of the string attribute to process.";
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  public boolean allowAccessToFullInputFormat() {
    return true;
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to override
   * this method to enable capabilities.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capability.NO_CLASS);
    result.disable(Capability.RELATIONAL_CLASS);
    result.disable(Capability.RELATIONAL_ATTRIBUTES);
    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Checks the input format.
   *
   * @param inputFormat the input format to check
   * @throws Exception in case the input format is invalid
   */
  protected void checkInputFormat(Instances inputFormat) throws Exception {
    if (inputFormat.attribute(m_AttributeName) == null)
      throw new WekaException("String attribute not found: " + m_AttributeName);
  }
}
