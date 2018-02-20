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
 * AbstractMongoDbDocumentFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import adams.core.option.AbstractOptionHandler;
import org.bson.conversions.Bson;

/**
 * Ancestor for filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMongoDbDocumentFilter
  extends AbstractOptionHandler
  implements MongoDbDocumentFilter {

  private static final long serialVersionUID = 4515883315240461022L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks setup before configuring filter.
   *
   * @return		null if OK, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  protected abstract Bson doConfigure();

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  public Bson configure() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);

    return doConfigure();
  }
}
