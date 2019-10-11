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
 * Remove.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbdocumentupdate;

import adams.core.base.BaseString;
import adams.core.logging.LoggingHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/**
 * Removes the specified keys from the document.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Remove
  extends AbstractMongoDbDocumentUpdate {

  private static final long serialVersionUID = 3771202579365692102L;

  /** the keys to remove. */
  protected BaseString[] m_Keys;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the specified keys from the document.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "keys",
      new BaseString[0]);
  }

  /**
   * Sets the keys to remove.
   *
   * @param value	the keys
   */
  public void setKeys(BaseString[] value) {
    m_Keys = value;
    reset();
  }

  /**
   * Returns the keys to remove.
   *
   * @return 		the keys
   */
  public BaseString[] getKeys() {
    return m_Keys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keysTipText() {
    return "The keys to remove.";
  }

  /**
   * Updates the document.
   *
   * @param doc		the document to update
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String doUpdate(MongoCollection coll, Document doc) {
    String	result;

    result = null;

    try {
      for (BaseString key: m_Keys)
        doc.remove(key.getValue());
      coll.deleteOne(Filters.eq("_id", doc.get("_id")));
      coll.insertOne(doc);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to remove key(s)!", e);
    }

    return result;
  }
}
