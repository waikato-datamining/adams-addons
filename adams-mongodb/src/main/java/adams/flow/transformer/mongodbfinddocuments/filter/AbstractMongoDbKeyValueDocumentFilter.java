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
 * AbstractMongoDbKeyValueDocumentFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import adams.core.QuickInfoHelper;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;

/**
 * Ancestor for filters that use a key-value pair.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMongoDbKeyValueDocumentFilter
  extends AbstractMongoDbDocumentFilter {

  private static final long serialVersionUID = 9100548349662348823L;

  /** the key. */
  protected String m_Key;

  /** the value. */
  protected String m_Value;

  /** the value conversion. */
  protected ConversionFromString m_ValueConversion;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "");

    m_OptionManager.add(
      "value", "value",
      "");

    m_OptionManager.add(
      "value-conversion", "valueConversion",
      new StringToString());
  }

  /**
   * Sets the key for the filter.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key for the filter.
   *
   * @return 		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The key for the filter.";
  }

  /**
   * Sets the value for the filter.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value for the filter.
   *
   * @return 		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value for the filter.";
  }

  /**
   * Sets the conversion for turning the value string into the actual type.
   *
   * @param value	the conversion
   */
  public void setValueConversion(ConversionFromString value) {
    m_ValueConversion = value;
    reset();
  }

  /**
   * Returns the conversion for turning the value string into the actual type.
   *
   * @return 		the conversion
   */
  public ConversionFromString getValueConversion() {
    return m_ValueConversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String valueConversionTipText() {
    return "For converting the value string into the actual type.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String 	result;

    result = QuickInfoHelper.toString(this, "key", m_Key, "key: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");
    result += QuickInfoHelper.toString(this, "valueConversion", m_ValueConversion, ", conv: ");

    return result;
  }

  /**
   * Checks setup before configuring filter.
   *
   * @return		null if OK, otherwise error message
   */
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_Key.isEmpty())
        result = "No key supplied!";
    }

    return result;
  }
}
