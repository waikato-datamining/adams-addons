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
 * SpreadSheetToDL4JDataSet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import gnu.trove.set.hash.TIntHashSet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

/**
 <!-- globalinfo-start -->
 * Converts a spreadsheet into a DL4J DataSet.<br>
 * Assumes no missing values to be present.<br>
 * Only converts numeric columns.<br>
 * Nominal columns&#47;classes need to be binarized first.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-class-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: classColumns)
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader to use for loading the data before converting it 
 * &nbsp;&nbsp;&nbsp;into a DL4J DataSet.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision$
 */
public class SpreadSheetToDL4JDataSet
  extends AbstractConversion {

  private static final long serialVersionUID = 1970704417619148081L;

  /** the columns with the class attribute. */
  protected SpreadSheetColumnRange m_ClassColumns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Converts a spreadsheet into a DL4J DataSet.\n"
	+ "Assumes only numeric cells and no missing values to be present.\n"
	+ "Nominal columns/classes need to be binarized first.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-columns", "classColumns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.LAST));
  }

  /**
   * Sets the columns to use as class attributes.
   *
   * @param value	the range
   */
  public void setClassColumns(SpreadSheetColumnRange value) {
    m_ClassColumns = value;
    reset();
  }

  /**
   * Returns the columns to use as class attributes.
   *
   * @return 		the range
   */
  public SpreadSheetColumnRange getClassColumns() {
    return m_ClassColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String classColumnsTipText() {
    return "The spreadsheet reader to use for loading the data before converting it into a DL4J DataSet.";
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
    return DataSet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet 	sheet;
    INDArray 		data;
    double[][] 		outcomes;
    int[] 		classes;
    TIntHashSet 	classesSet;
    int			i;
    int			j;
    double[] 		independent;
    int 		index;
    Row 		current;

    sheet      = (SpreadSheet) m_Input;
    m_ClassColumns.setData(sheet);
    classes    = m_ClassColumns.getIntIndices();
    classesSet = new TIntHashSet(classes);
    data       = Nd4j.ones(sheet.getRowCount(), sheet.getColumnCount() - classes.length);
    outcomes   = new double[sheet.getRowCount()][classes.length];

    for (i = 0; i < sheet.getRowCount(); i++) {
      independent = new double[sheet.getColumnCount() - classes.length];
      index       = 0;
      current     = sheet.getRow(i);
      for (j = 0; j < sheet.getColumnCount(); j++) {
	if (!classesSet.contains(j))
          independent[index++] = current.getCell(j).toDouble();
      }
      for (j = 0; j < classes.length; j++)
	outcomes[i][j] = current.getCell(classes[j]).toDouble();
      data.putRow(i, Nd4j.create(independent));
    }

    return new DataSet(data, Nd4j.create(outcomes));
  }
}
