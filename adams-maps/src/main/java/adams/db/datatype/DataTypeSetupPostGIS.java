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
 * DataTypeSetupPostGIS.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.db.datatype;

import adams.core.logging.LoggingHelper;
import net.postgis.jdbc.PGbox3d;
import net.postgis.jdbc.PGgeometry;
import org.postgresql.PGConnection;

/**
 <!-- globalinfo-start -->
 * Configures some PostGIS specific data types.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DataTypeSetupPostGIS
  extends AbstractDataTypeSetup {

  /** for serialization. */
  private static final long serialVersionUID = -5023819809851831759L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures some PostGIS specific data types.";
  }

  /**
   * Configures the data types.
   * 
   * @return		null if OK, otherwise error message
   */
  @Override
  public String setupDataTypes(java.sql.Connection conn) {
    String	result;
    
    result = null;
    
    try {
      ((PGConnection) conn).addDataType("geometry", PGgeometry.class);
      ((PGConnection) conn).addDataType("box3d", PGbox3d.class);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to add data types!", e);
    }
    
    return result;
  }
}
