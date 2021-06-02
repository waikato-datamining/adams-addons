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
 * CNTKSpreadSheetReader.java
 * Copyright (C) 2017-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.io.output.CNTKSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 <!-- globalinfo-start -->
 * Reads datasets in CNTK text file format.<br>
 * For more details, see:<br>
 * Microsoft. CNTK. URL https:&#47;&#47;docs.microsoft.com&#47;en-us&#47;cognitive-toolkit&#47;brainscript-cntktextformat-reader.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 *
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKSpreadSheetReader
  extends AbstractSpreadSheetReader
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 6732892846887308208L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads datasets in CNTK text file format.\n"
	+ "For more details, see:\n"
	+ getTechnicalInformation();
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
    return new CNTKSpreadSheetWriter().getTechnicalInformation();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new CNTKSpreadSheetWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new CNTKSpreadSheetWriter().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return new CNTKSpreadSheetWriter();
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }

  /**
   * Removes any comments from the data.
   *
   * @param line	the data to process
   * @return		the cleaned up data
   */
  protected String removeComments(String line) {
    StringBuilder	result;
    boolean		comment;
    int			i;
    char		c;

    if (!line.contains("#"))
      return line;

    comment = false;
    result  = new StringBuilder();
    for (i = 0; i < line.length(); i++) {
      c = line.charAt(i);
      if (c == '#')
        comment = true;
      else if (c == '|')
        comment = false;
      if (!comment)
        result.append(c);
    }

    return result.toString();
  }

  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    SpreadSheet			result;
    Map<String,SpreadSheet> 	sheets;
    SpreadSheet			sheet;
    Row				row;
    BufferedReader		breader;
    boolean			sparse;
    String			line;
    int				lineNo;
    String[]			sections;
    String			rowID;
    List<String>		rowIDs;
    boolean			hasRowIDs;
    Pattern			pattID;
    List<String> 		names;
    String			name;
    String[]			cells;
    int				i;
    int				index;
    double			value;
    String[]			parts;
    boolean			initHeader;

    if (r instanceof BufferedReader)
      breader = (BufferedReader) r;
    else
      breader = new BufferedReader(r);

    result    = null;
    sheets    = null;
    sparse    = false;
    lineNo    = 0;
    pattID    = Pattern.compile("^[0-9]+.*");
    names     = new ArrayList<>();
    rowIDs    = new ArrayList<>();
    hasRowIDs = false;
    try {
      while (((line = breader.readLine()) != null) && !isStopped()) {
        lineNo++;

        // remove comments
	line = removeComments(line);

        // structure analyzed?
	if (sheets == null) {
	  sheets = new HashMap<>();
	  sparse = line.contains(":");
	}

	// row ID?
	rowID = null;
	if (pattID.matcher(line).matches()) {
	  hasRowIDs = true;
	  rowID = line.substring(0, line.indexOf("|")).trim();
	  line = line.substring(line.indexOf("|") + 1);
	}
	rowIDs.add(rowID);

	// split into sections
	sections = line.split("\\|");
	for (String section: sections) {
	  // name of section
	  if (section.contains(" "))
	    name = section.substring(0, section.indexOf(" ")).trim();
	  else
	    name = section.trim();
	  section = section.substring(name.length()).trim();

	  // add row
	  if (!sheets.containsKey(name)) {
	    initHeader = true;
	    sheet = new DefaultSpreadSheet();
	    sheet.setName(name);
	    names.add(name);
	    if (sparse)
	      sheet.setDataRowClass(SparseDataRow.class);
	    sheets.put(name, sheet);
	  }
	  else {
	    initHeader = false;
	    sheet = sheets.get(name);
	  }
	  while (sheet.getRowCount() < lineNo)
	    sheet.addRow();

	  // process cells
	  if (section.isEmpty())
	    continue;
	  cells = section.split(" ");
	  for (i = 0; i < cells.length; i++) {
	    if (cells[i].contains(":")) {
	      parts = cells[i].split(":");
	      index = Integer.parseInt(parts[0]);
	      value = Double.parseDouble(parts[0]);
	    }
	    else {
	      index = i;
	      value = Double.parseDouble(cells[i]);
	    }
	    if (initHeader) {
	      while (sheet.getColumnCount() - 1 < index)
		sheet.insertColumn(sheet.getColumnCount(), name + "-" + sheet.getColumnCount());
	    }
	    row = sheet.getRow(sheet.getRowCount() - 1);
	    row.addCell(index).setContent(value);
	  }
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read line " + lineNo + "!", e);
      sheets = null;
    }

    if (sheets != null) {
      // row IDs
      if (hasRowIDs) {
        sheet = new DefaultSpreadSheet();
        sheet.setName("Row IDs");
        sheet.getHeaderRow().addCell("r").setContent("ID");
        for (String id: rowIDs)
          sheet.addRow().addCell("r").setContent(id);
        name = "rowids-" + System.currentTimeMillis();
        names.add(0, name);
        sheets.put(name, sheet);
      }
      // merge sheets
      for (String n: names) {
        if (result == null)
	  result = sheets.get(n);
	else
	  result.mergeWith(sheets.get(n));
      }
    }

    return result;
  }

  /**
   * Runs the reader from the command-line.
   *
   * Use the option {@link #OPTION_INPUT} to specify the input file.
   * If the option {@link #OPTION_OUTPUT} is specified then the read sheet
   * gets output as .csv files in that directory.
   *
   * @param args	the command-line options to use
   */
  public static void main(String[] args) {
    runReader(Environment.class, CNTKSpreadSheetReader.class, args);
  }
}
