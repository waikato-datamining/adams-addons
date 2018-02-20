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
 * Filtered.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.mongodbfinddocuments.filter.Equal;
import adams.flow.transformer.mongodbfinddocuments.filter.MongoDbDocumentFilter;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Returns all documents in the collection that match the filter.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Filtered
  extends AbstractMongoDbFindDocuments {

  private static final long serialVersionUID = -8119121394992753776L;

  /** the filter to use. */
  protected MongoDbDocumentFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns all documents in the collection that match the supplied filter.";
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
   * Sets the filter to apply.
   *
   * @param value	the filter
   */
  public void setFilter(MongoDbDocumentFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter to apply.
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
    return "The filter to apply.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
  }

  /**
   * Filters the collection.
   *
   * @param coll	the collection to filter
   * @return		the documents matching the query
   */
  @Override
  protected FindIterable<Document> doFind(MongoCollection coll) {
    return coll.find(m_Filter.configure());
  }
}
