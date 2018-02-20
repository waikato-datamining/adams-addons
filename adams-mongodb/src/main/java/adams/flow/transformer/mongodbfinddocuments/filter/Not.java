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
 * Not.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import adams.core.QuickInfoHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * Inverts the matching of the base filter.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Not
  extends AbstractMongoDbDocumentFilter {

  private static final long serialVersionUID = 651928977478177617L;

  /** the base filter. */
  protected MongoDbDocumentFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inverts the matching of the base filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new Equal());
  }

  /**
   * Sets the base filter to invert.
   *
   * @param value	the filter
   */
  public void setFilter(MongoDbDocumentFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the base filter to invert.
   *
   * @return 		the filter
   */
  public MongoDbDocumentFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to invert the matching sense.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", m_Filter, "! ");
  }

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  @Override
  protected Bson doConfigure() {
    return Filters.not(m_Filter.configure());
  }
}
