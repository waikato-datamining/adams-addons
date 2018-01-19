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
 * AlternativeUrlSupporter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;


/**
 * Interface for webservice clients that allow the specification of an
 * alternative URL.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AlternativeUrlSupporter
  extends RESTClient {
  
  /**
   * Sets whether to use the alternative URL.
   * 
   * @param value	whether to use the alternative URL
   */
  public void setUseAlternativeURL(boolean value);
  
  /**
   * Returns whether to use the alternative URL used for the service.
   * 
   * @return		true if to use alternative URL
   */
  public boolean getUseAlternativeURL();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAlternativeURLTipText();

  /**
   * Returns the default URL for the service.
   * 
   * @return		the URL
   */
  public String getDefaultAlternativeURL();
  
  /**
   * Sets the alternative URL to use.
   * 
   * @param value	the URL to use
   */
  public void setAlternativeURL(String value);
  
  /**
   * Returns the alternative URL used for the service.
   * 
   * @return		the URL
   */
  public String getAlternativeURL();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeURLTipText();
}
