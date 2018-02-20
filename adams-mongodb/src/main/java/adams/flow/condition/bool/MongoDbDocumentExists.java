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
 * MongoDbCollectionExists.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.db.MongoDbUrl;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Iterator;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Returns 'true' if the specified collection exists in the current database context.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-collection &lt;java.lang.String&gt; (property: collection)
 * &nbsp;&nbsp;&nbsp;The name of the collection to check.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbDocumentExists
  extends AbstractMongoDbBooleanCondition {

  private static final long serialVersionUID = 6846151121506219847L;

  /** the name of the collection. */
  protected String m_Collection;

  /** the ID to check. */
  protected String m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns 'true' if a document with that ID exists.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "collection", "collection",
      "");

    m_OptionManager.add(
      "ID", "ID",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "collection", m_Collection, "coll: ");
    result += QuickInfoHelper.toString(this, "ID", m_ID, ", ID: ");

    return result;
  }

  /**
   * Sets the name of the collection to check.
   *
   * @param value	the name
   */
  public void setCollection(String value) {
    m_Collection = value;
    reset();
  }

  /**
   * Returns the name of the collection to check.
   *
   * @return 		the name
   */
  public String getCollection() {
    return m_Collection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String collectionTipText() {
    return "The name of the collection to check.";
  }

  /**
   * Sets the ID of the document to check.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the document to check.
   *
   * @return 		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the document to check.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean			result;
    MongoDbUrl 			url;
    MongoCollection		coll;
    FindIterable<Document> 	docs;
    Iterator<Document> 		iter;

    result = false;

    try {
      url  = new MongoDbUrl(m_DatabaseConnection.getURL());
      coll = m_DatabaseConnection.getConnection(true).getDatabase(url.uriValue().getDatabase()).getCollection(m_Collection);
      docs = coll.find(Filters.eq("_id", m_ID));
      iter = docs.iterator();
      while (iter.hasNext()) {
        iter.next();
        result = true;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to locate document in collection '" + m_Collection + "' with ID '" + m_ID + "'!", e);
    }

    return result;
  }
}
