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
 * OpenAPISupporter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.rest;

/**
 * Interface for classes that support OpenAPI.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OpenAPISupporter {

  /**
   * Sets whether to enable OpenAPI documentation.
   *
   * @param value	true if to enable
   */
  public void setEnableOpenAPI(boolean value);

  /**
   * Returns whether to enable OpenAPI documentation.
   *
   * @return		true if to enable
   */
  public boolean getEnableOpenAPI();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enableOpenAPITipText();
}
