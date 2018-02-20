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
 * And.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * The intersection of the base filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class And
  extends AbstractMongoDbDocumentFilter {

  private static final long serialVersionUID = 651928977478177617L;

  /** the base filters. */
  protected MongoDbDocumentFilter[] m_Filters;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Intersects the results of the base filters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filters",
      new MongoDbDocumentFilter[0]);
  }

  /**
   * Sets the base filters to intersect.
   *
   * @param value	the filters
   */
  public void setFilters(MongoDbDocumentFilter[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the base filters to intersect.
   *
   * @return 		the filters
   */
  public MongoDbDocumentFilter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String filtersTipText() {
    return "The filters to intersect.";
  }

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  @Override
  protected Bson doConfigure() {
    Bson[]	base;
    int		i;

    base = new Bson[m_Filters.length];
    for (i = 0; i < m_Filters.length; i++)
      base[i] = m_Filters[i].configure();
    return Filters.and(base);
  }
}
