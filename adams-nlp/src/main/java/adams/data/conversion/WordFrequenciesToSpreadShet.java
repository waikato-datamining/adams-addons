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
 * WordFrequenciesToSpreadShet.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import com.kennycason.kumo.WordFrequency;

/**
 <!-- globalinfo-start -->
 * Turns word frequencies into a spreadsheet with two columns: Word, Frequency
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WordFrequenciesToSpreadShet
  extends AbstractConversion {

  private static final long serialVersionUID = -5177248020254339908L;

  /** the column name for the word column. */
  public final static String COL_WORD = "Word";

  /** the column name for the frequency column. */
  public final static String COL_FREQUENCY = "Frequency";

  @Override
  public String globalInfo() {
    return "Turns word frequencies into a spreadsheet with two columns: " + COL_WORD + ", " + COL_FREQUENCY;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return WordFrequency[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    WordFrequency[]	freqs;
    Row			row;

    freqs = (WordFrequency[]) m_Input;
    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    row.addCell("W").setContentAsString(COL_WORD);
    row.addCell("F").setContentAsString(COL_FREQUENCY);

    for (WordFrequency freq: freqs) {
      row = result.addRow();
      row.addCell("W").setContentAsString(freq.getWord());
      row.addCell("F").setContent(freq.getFrequency());
    }

    return result;
  }
}
