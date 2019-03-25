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
 * JOOQResultToSpreadSheet.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DataRowTypeHandler;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Reader;
import adams.db.SQLUtils;
import org.jooq.Result;

import java.sql.ResultSet;

/**
 <!-- globalinfo-start -->
 * Converts a jOOQ query Result to a SpreadSheet object.
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
 * <pre>-time-with-msec &lt;boolean&gt; (property: timeWithMsec)
 * &nbsp;&nbsp;&nbsp;If enabled, time is output with msec, otherwise just with with sec.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9591 $
 */
public class JOOQResultToSpreadSheet
  extends AbstractConversion
  implements DataRowTypeHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1978448247862661404L;

  /** the type mapper to use. */
  protected AbstractTypeMapper m_TypeMapper;

  /** the data row type to use. */
  protected DataRow m_DataRowType;

  /** the reader currently in use. */
  protected Reader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a jOOQ query Result to a SpreadSheet object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type-mapper", "typeMapper",
      new DefaultTypeMapper());

    m_OptionManager.add(
      "data-row-type", "dataRowType",
      new DenseDataRow());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "dataRowType", m_DataRowType, "row type: ");
  }

  /**
   * Sets the type mapper to use.
   *
   * @param value	the mapper
   */
  public void setTypeMapper(AbstractTypeMapper value) {
    m_TypeMapper = value;
    reset();
  }

  /**
   * Returns the type mapper in use.
   *
   * @return		the mapper
   */
  public AbstractTypeMapper  getTypeMapper() {
    return m_TypeMapper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeMapperTipText() {
    return "The type mapper to use for mapping spreadsheet and SQL types.";
  }

  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRow value) {
    m_DataRowType = value;
    reset();
  }

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRow getDataRowType() {
    return m_DataRowType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText() {
    return "The type of row to use for the data.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Result.class;
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
    SpreadSheet	result;
    Result	jooq;
    ResultSet	rs;
    
    jooq     = (Result) m_Input;
    rs       = jooq.intoResultSet();
    m_Reader = new Reader(m_TypeMapper, m_DataRowType.getClass());
    result   = m_Reader.read(rs);
    m_Reader = null;
    SQLUtils.closeAll(rs);
    
    return result;
  }
  
  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Reader != null)
      m_Reader.stopExecution();
    
    super.stopExecution();
  }
}
