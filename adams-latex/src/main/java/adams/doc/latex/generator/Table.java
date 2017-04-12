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
 * Table.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;
import adams.core.management.LocaleHelper;
import adams.data.io.output.LatexSpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;

import java.io.StringWriter;
import java.util.Locale;

/**
 <!-- globalinfo-start -->
 * Inserts the spreadsheet obtained from storage as table.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If enabled, the code generation gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-var-expansion &lt;boolean&gt; (property: noVariableExpansion)
 * &nbsp;&nbsp;&nbsp;If enabled, variable expansion gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name for the lookup table in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: table
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-locale &lt;java.util.Locale&gt; (property: locale)
 * &nbsp;&nbsp;&nbsp;The locale to use for formatting the numbers.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-number-format &lt;java.lang.String&gt; (property: numberFormat)
 * &nbsp;&nbsp;&nbsp;The format for the numbers (see java.text.DecimalFormat), use empty string 
 * &nbsp;&nbsp;&nbsp;for default 'double' output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Table
  extends AbstractCodeGenerator
  implements StorageUser {

  private static final long serialVersionUID = -2504232052630130162L;

  /** the storage name of the spreadsheet to insert. */
  protected StorageName m_StorageName;

  /** The placeholder for missing values. */
  protected String m_MissingValue;

  /** the locale to use. */
  protected Locale m_Locale;

  /** The format for the numbers. */
  protected String m_NumberFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts the spreadsheet obtained from storage as table.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("table"));

    m_OptionManager.add(
      "missing", "missingValue",
      getDefaultMissingValue());

    m_OptionManager.add(
      "locale", "locale",
      LocaleHelper.getSingleton().getDefault());

    m_OptionManager.add(
      "number-format", "numberFormat",
      getDefaultNumberFormat());
  }

  /**
   * Sets the name for the lookup table in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the lookup table in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name for the lookup table in the internal storage.";
  }

  /**
   * Returns the default missing value.
   *
   * @return		the default for missing values
   */
  protected String getDefaultMissingValue() {
    return "";
  }

  /**
   * Sets the placeholder for missing values.
   *
   * @param value	the placeholder
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the current placeholder for missing values.
   *
   * @return		the placeholder
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String missingValueTipText() {
    return "The placeholder for missing values.";
  }

  /**
   * Sets the locale to use.
   *
   * @param value	the locale
   */
  public void setLocale(Locale value) {
    m_Locale = value;
    reset();
  }

  /**
   * Returns the locale in use.
   *
   * @return 		the locale
   */
  public Locale getLocale() {
    return m_Locale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localeTipText() {
    return "The locale to use for formatting the numbers.";
  }

  /**
   * Returns the default number format.
   *
   * @return		the default format
   */
  protected String getDefaultNumberFormat() {
    return "";
  }

  /**
   * Sets the number format.
   *
   * @param value	the format
   */
  public void setNumberFormat(String value) {
    m_NumberFormat = value;
    reset();
  }

  /**
   * Returns the number format.
   *
   * @return		the format
   */
  public String getNumberFormat() {
    return m_NumberFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String numberFormatTipText() {
    return "The format for the numbers (see java.text.DecimalFormat), use empty string for default 'double' output.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return !getSkip();
  }

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public String[] getRequiredPackages() {
    return new String[0];
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  @Override
  protected String doGenerate() {
    SpreadSheet			sheet;
    StringWriter 		swriter;
    LatexSpreadSheetWriter	lwriter;

    sheet   = (SpreadSheet) getFlowContext().getStorageHandler().getStorage().get(m_StorageName);
    swriter = new StringWriter();
    lwriter = new LatexSpreadSheetWriter();
    lwriter.setMissingValue(m_MissingValue);
    lwriter.setLocale(m_Locale);
    lwriter.setNumberFormat(m_NumberFormat);
    lwriter.write(sheet, swriter);

    return swriter.toString();
  }
}
