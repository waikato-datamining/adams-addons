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
 * RemoveDocument.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbcollectionupdate;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Remove the document with the specified ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveDocument
  extends AbstractMongoDbCollectionUpdate {

  private static final long serialVersionUID = 3771202579365692102L;

  /** the ID of the document. */
  protected String m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the document with the given ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "ID", (m_ID.isEmpty() ? "-auto-" : m_ID), "ID: ");
  }

  /**
   * Sets the ID of the document to remove.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the document to remove.
   *
   * @return 		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the document to remove.";
  }

  /**
   * Updates the collection.
   *
   * @param coll	the collection to update
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String doUpdate(MongoCollection coll) {
    String	result;

    result = null;
    if (m_ID.isEmpty())
      result = "No ID supplied!";

    if (result == null) {
      try {
	coll.deleteOne(Filters.eq("_id", m_ID));
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to add document!", e);
      }
    }

    return result;
  }
}
