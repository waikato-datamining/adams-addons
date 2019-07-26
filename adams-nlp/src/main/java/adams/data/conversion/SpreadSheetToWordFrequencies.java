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
 * SpreadSheetToWordFrequencies.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import com.kennycason.kumo.WordFrequency;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet with two columns for words and frequencies into an array of word frequency objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-column-word &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnWord)
 * &nbsp;&nbsp;&nbsp;The column containing the words.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-column-frequency &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnFrequency)
 * &nbsp;&nbsp;&nbsp;The column containing the frequencies.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetToWordFrequencies
  extends AbstractConversion {

  private static final long serialVersionUID = -5177248020254339908L;

  /** the column with the words. */
  protected SpreadSheetColumnIndex m_ColumnWord;

  /** the column with the frequencies. */
  protected SpreadSheetColumnIndex m_ColumnFrequency;

  @Override
  public String globalInfo() {
    return "Turns a spreadsheet with two columns for words and frequencies into an array of word frequency objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column-word", "columnWord",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "column-frequency", "columnFrequency",
      new SpreadSheetColumnIndex("2"));
  }

  /**
   * Sets the column containing the words.
   *
   * @param value	the column
   */
  public void setColumnWord(SpreadSheetColumnIndex value) {
    m_ColumnWord = value;
    reset();
  }

  /**
   * Returns the column containing the words.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnWord() {
    return m_ColumnWord;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnWordTipText() {
    return "The column containing the words.";
  }

  /**
   * Sets the column containing the frequencies.
   *
   * @param value	the column
   */
  public void setColumnFrequency(SpreadSheetColumnIndex value) {
    m_ColumnFrequency = value;
    reset();
  }

  /**
   * Returns the column containing the frequencies.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnFrequency() {
    return m_ColumnFrequency;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnFrequencyTipText() {
    return "The column containing the frequencies.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return WordFrequency[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		sheet;
    List<WordFrequency> result;
    int			colWord;
    int			colFreq;

    result = new ArrayList<>();
    sheet  = (SpreadSheet) m_Input;
    m_ColumnWord.setData(sheet);
    colWord = m_ColumnWord.getIntIndex();
    if (colWord == -1)
      throw new IllegalStateException("Failed to locate word column: " + m_ColumnWord.getIndex());
    m_ColumnFrequency.setData(sheet);
    colFreq = m_ColumnFrequency.getIntIndex();
    if (colFreq == -1)
      throw new IllegalStateException("Failed to locate frequency column: " + m_ColumnFrequency.getIndex());

    for (Row row: sheet.rows()) {
      if (row.hasCell(colWord) && !row.getCell(colWord).isMissing() && row.hasCell(colFreq) && !row.getCell(colFreq).isMissing()) {
        result.add(
          new WordFrequency(
            row.getCell(colWord).getContent(),
	    row.getCell(colFreq).toDouble().intValue()));
      }
    }

    return result;
  }
}
