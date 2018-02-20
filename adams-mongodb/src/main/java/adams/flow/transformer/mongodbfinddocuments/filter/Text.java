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
 * Text.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import adams.core.QuickInfoHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * Performs a text search.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Text
  extends AbstractMongoDbDocumentFilter {

  private static final long serialVersionUID = 651928977478177617L;

  /** the text to look for. */
  protected String m_Search;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a text search.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "search", "search",
      "");
  }

  /**
   * Sets the search string.
   *
   * @param value	the search string
   */
  public void setSearch(String value) {
    m_Search = value;
    reset();
  }

  /**
   * Returns the search string.
   *
   * @return 		the search string
   */
  public String getSearch() {
    return m_Search;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String searchTipText() {
    return "The search string.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "search", m_Search, "search: ");
  }

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  @Override
  protected Bson doConfigure() {
    return Filters.text(m_Search);
  }
}
