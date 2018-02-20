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
 * MongoDbUrl.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.base.AbstractBaseString;
import com.mongodb.MongoClientURI;

/**
 * Encapsulates a MongoDB URL and performs some minimal checks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbUrl
  extends AbstractBaseString {

  public static final String DEFAULT_URL = "mongodb://localhost:27017/somedatabase";

  private static final long serialVersionUID = 7278269302456796872L;

  /**
   * Initializes with a default URL.
   */
  public MongoDbUrl() {
    this(DEFAULT_URL);
  }

  /**
   * Initializes the object with the URL to parse.
   *
   * @param s		the URL to parse
   */
  public MongoDbUrl(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    String[]	parts;

    if ((value == null) || value.isEmpty())
      return false;

    parts = value.split(":");
    if (parts.length < 2)
      return false;

    // starts with 'mongodb'?
    if (!parts[0].equals("mongodb"))
      return false;

    return true;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Format: 'mongodb:<server url>/database'";
  }

  /**
   * Whether this object should have favorites support.
   *
   * @return		true if to support favorites
   */
  @Override
  public boolean hasFavoritesSupport() {
    return true;
  }

  /**
   * Returns the URL as URI.
   *
   * @return		the URI
   */
  public MongoClientURI uriValue() {
    return new MongoClientURI(getValue());
  }

  /**
   * Determines the host from the URL.
   *
   * @return		the host
   */
  public String hostValue() {
    String	result;

    result = getValue().replace("mongodb://", "");
    result = result.replaceAll("\\/.*", "");

    return result;
  }

  /**
   * Determines the port from the URL.
   *
   * @return		the port
   */
  public int portValue() {
    int		result;
    String 	portStr;

    result  = 27017;
    portStr = getValue().replace("mongodb://", "");
    if (portStr.contains(":")) {
      portStr = portStr.replaceAll(".*\\:", "");
      portStr = portStr.replaceAll("([0-9]+).*", "$1");
      try {
        result = Integer.parseInt(portStr);
      }
      catch (Exception e) {
        System.err.println("Failed to parse: " + portStr);
        e.printStackTrace();
      }
    }

    return result;
  }
}
