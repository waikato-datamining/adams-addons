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
 * CNTKSpreadSheetWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.data.io.input.CNTKSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Writer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Outputs spreadsheets in CNTK text file format.<br>
 * Requires all cells to be numeric (apart from row ID column).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-row-id &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: rowID)
 * &nbsp;&nbsp;&nbsp;The (optional) column to use for the row ID.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-input &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; [-input ...] (property: inputs)
 * &nbsp;&nbsp;&nbsp;The column ranges determining the inputs (eg for 'features' and 'class').
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-input-name &lt;adams.core.base.BaseString&gt; [-input-name ...] (property: inputNames)
 * &nbsp;&nbsp;&nbsp;The names of the inputs (eg 'features' and 'class').
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-sparse-format &lt;boolean&gt; (property: useSparseFormat)
 * &nbsp;&nbsp;&nbsp;If enabled, sparse format is used instead (ie 'index:value').
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-suppress-missing &lt;boolean&gt; (property: suppressMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, groups that contain at least one missing value get suppressed
 * &nbsp;&nbsp;&nbsp;completely.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKSpreadSheetWriter
  extends AbstractFormattedSpreadSheetWriter
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = -756713262393228509L;

  /** the optional row id column. */
  protected SpreadSheetColumnIndex m_RowID;

  /** the inputs. */
  protected SpreadSheetColumnRange[] m_Inputs;

  /** the names of the inputs. */
  protected BaseString[] m_InputNames;

  /** whether to output sparse format. */
  protected boolean m_UseSparseFormat;

  /** whether to suppress groups with missing values. */
  protected boolean m_SuppressMissing;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs spreadsheets in CNTK text file format.\n"
      + "Requires all cells to be numeric (apart from row ID column).\n"
      + "For more details, see:\n"
      + getTechnicalInformation();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row-id", "rowID",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "input", "inputs",
      new SpreadSheetColumnRange[0]);

    m_OptionManager.add(
      "input-name", "inputNames",
      new BaseString[0]);

    m_OptionManager.add(
      "use-sparse-format", "useSparseFormat",
      false);

    m_OptionManager.add(
      "suppress-missing", "suppressMissing",
      false);
  }

  /**
   * Sets the (optional) row ID column to use.
   *
   * @param value	the column
   */
  public void setRowID(SpreadSheetColumnIndex value) {
    m_RowID = value;
    reset();
  }

  /**
   * Returns the (optional) row ID column to use.
   *
   * @return 		the column
   */
  public SpreadSheetColumnIndex getRowID() {
    return m_RowID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowIDTipText() {
    return "The (optional) column to use for the row ID.";
  }

  /**
   * Sets the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @param value	the column
   */
  public void setInputs(SpreadSheetColumnRange[] value) {
    m_Inputs     = value;
    m_InputNames = (BaseString[]) Utils.adjustArray(m_InputNames, m_Inputs.length, new BaseString());
    reset();
  }

  /**
   * Returns the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @return 		the ranges
   */
  public SpreadSheetColumnRange[] getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputsTipText() {
    return "The column ranges determining the inputs (eg for 'features' and 'class').";
  }

  /**
   * Sets the names for the inputs.
   *
   * @param value	the names
   */
  public void setInputNames(BaseString[] value) {
    m_InputNames = value;
    m_Inputs     = (SpreadSheetColumnRange[]) Utils.adjustArray(m_Inputs, m_InputNames.length, new SpreadSheetColumnRange());
    reset();
  }

  /**
   * Returns the names for the inputs.
   *
   * @return 		the names
   */
  public BaseString[] getInputNames() {
    return m_InputNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputNamesTipText() {
    return "The names of the inputs (eg 'features' and 'class').";
  }

  /**
   * Sets whether to use sparse format.
   *
   * @param value	true if to use sparse format
   */
  public void setUseSparseFormat(boolean value) {
    m_UseSparseFormat = value;
    reset();
  }

  /**
   * Returns whether to use sparse format.
   *
   * @return 		true if to use sparse format
   */
  public boolean getUseSparseFormat() {
    return m_UseSparseFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSparseFormatTipText() {
    return "If enabled, sparse format is used instead (ie 'index:value').";
  }

  /**
   * Sets whether to suppress groups with missing values.
   *
   * @param value	true if to suppress missing
   */
  public void setSuppressMissing(boolean value) {
    m_SuppressMissing = value;
    reset();
  }

  /**
   * Returns whether to suppress groups with missing values.
   *
   * @return 		true if to suppress missing
   */
  public boolean getSuppressMissing() {
    return m_SuppressMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressMissingTipText() {
    return "If enabled, groups that contain at least one missing value get suppressed completely.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public SpreadSheetReader getCorrespondingReader() {
    return new CNTKSpreadSheetReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "CNTK Dataset";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"txt", "ctf"};
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.WRITER;
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Microsoft");
    result.setValue(Field.TITLE, "CNTK");
    result.setValue(Field.URL, "https://docs.microsoft.com/en-us/cognitive-toolkit/brainscript-cntktextformat-reader");

    return result;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    int		rowID;
    int[][]	inputs;
    int		i;
    int		n;
    int		m;
    int		r;
    Cell 	cell;
    String[]	names;
    Double	value;
    TIntList	canOutput;
    boolean	missing;

    if (m_Inputs.length == 0) {
      getLogger().severe("No input ranges defined!");
      return false;
    }

    // ensure same length
    m_InputNames = (BaseString[]) Utils.adjustArray(m_InputNames, m_Inputs.length, new BaseString());
    names = new String[m_InputNames.length];
    for (i = 0; i < m_InputNames.length; i++) {
      names[i] = m_InputNames[i].getValue();
      if (names[i].isEmpty())
	names[i] = "input-" + (i+1);
    }

    // determine columns
    m_RowID.setData(content);
    rowID = m_RowID.getIntIndex();
    if (isLoggingEnabled())
      getLogger().info("row ID col (0-based, ignored if -1): " + rowID);
    inputs = new int[m_Inputs.length][];
    if (isLoggingEnabled())
      getLogger().info("# of inputs: " + m_Inputs.length);
    for (i = 0; i < m_Inputs.length; i++) {
      m_Inputs[i].setData(content);
      inputs[i] = m_Inputs[i].getIntIndices();
      if (isLoggingEnabled())
	getLogger().info("input " + (i+1) + " (0-based): " + Utils.arrayToString(inputs[i]));
    }

    canOutput = new TIntArrayList();
    if (!m_SuppressMissing) {
      for (i = 0; i < m_Inputs.length; i++)
        canOutput.add(i);
    }

    // write data
    r = 0;
    for (Row row : content.rows()) {
      if (m_Stopped)
	return false;

      r++;
      // check which groups can be output when suppressing groups with missing values
      if (m_SuppressMissing) {
        canOutput.clear();
	for (i = 0; i < inputs.length; i++) {
	  missing = false;
	  for (n = 0; n < inputs[i].length; n++) {
	    cell = row.getCell(inputs[i][n]);
	    if ((cell == null) || cell.isMissing()) {
	      missing = true;
	      break;
	    }
	  }
	  if (!missing)
	    canOutput.add(i);
	}
      }
      // skip whole row?
      if (m_SuppressMissing && isLoggingEnabled())
        getLogger().fine("Row #" + r + " / inputs to output: " + canOutput);
      if (canOutput.size() == 0)
        continue;

      try {
	// ID
	if (rowID > -1) {
	  cell = row.getCell(rowID);
	  if ((cell != null) && !cell.isMissing()) {
	    writer.write(cell.getContent());
	    writer.write(" ");
	  }
	}

	// inputs
	for (m = 0; m < canOutput.size(); m++) {
	  i = canOutput.get(m);

	  // separator
	  writer.write("|");

	  // name
	  writer.write(names[i]);
	  writer.write(" ");

	  // values
	  for (n = 0; n < inputs[i].length; n++) {
	    cell = row.getCell(inputs[i][n]);
	    if ((cell == null) || cell.isMissing())
	      value = null;
	    else
	      value = cell.toDouble();

	    if (m_UseSparseFormat) {
	      if ((value != null) && (value == 0))
		continue;
	      writer.write("" + inputs[i][n]);
	      writer.write(":");
	    }
	    if (value == null)
	      writer.write(m_MissingValue);
	    else
	      writer.write(format(value));
	    writer.write(" ");
	  }
	}

	writer.write("\n");
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to write data: " + row, e);
	return false;
      }
    }

    return true;
  }
}
