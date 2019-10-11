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
 * AddDocument.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbcollectionupdate;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.flow.transformer.mongodbdocumentupdate.MongoDbDocumentAppend;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Adds a document.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AddDocument
  extends AbstractMongoDbCollectionUpdate {

  private static final long serialVersionUID = 3771202579365692102L;

  /** the ID for the document. */
  protected String m_ID;

  /** for appending key-value pairs. */
  protected MongoDbDocumentAppend[] m_Updates;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Adds a new document with the given ID to the document.\n"
      + "The document gets filled with the specified key-value pairs.";
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

    m_OptionManager.add(
      "update", "updates",
      new MongoDbDocumentAppend[0]);
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
   * Sets the ID to use for the document.
   *
   * @param value	the ID, empty for automatically created one
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID to use for the document.
   *
   * @return 		the ID, empty for automatically created one
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
    return "The ID to use for the document.";
  }

  /**
   * Sets the document updaters to apply.
   *
   * @param value	the updaters
   */
  public void setUpdates(MongoDbDocumentAppend[] value) {
    m_Updates = value;
    reset();
  }

  /**
   * Returns the document updaters to apply.
   *
   * @return 		the updaters
   */
  public MongoDbDocumentAppend[] getUpdates() {
    return m_Updates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String updatesTipText() {
    return "The for updating the document.";
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
    Document	doc;

    result = null;

    try {
      if (m_ID.isEmpty())
	doc = new Document();
      else
	doc = new Document("_id", m_ID);

      coll.insertOne(doc);

      for (MongoDbDocumentAppend update: m_Updates) {
        update.setFlowContext(getFlowContext());
        result = update.update(coll, doc);
        if (result != null)
          break;
      }
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to add document!", e);
    }

    return result;
  }
}
