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
 * AppendStorage.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbdocumentupdate;

import adams.core.MessageCollection;
import adams.core.base.BaseKeyValuePair;
import adams.core.logging.LoggingHelper;
import adams.data.conversion.Conversion;
import adams.data.conversion.ObjectToObject;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/**
 * Appends the document with the specified key-value pairs from storage.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AppendStorage
  extends AbstractMongoDbDocumentUpdate
  implements MongoDbDocumentAppend {

  private static final long serialVersionUID = 3771202579365692102L;

  /** the key-value pairs to add. */
  protected BaseKeyValuePair[] m_KeyValuePairs;

  /** the storage item conversion. */
  protected Conversion m_ValueConversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the document with the specified key-value pairs, with the values representing storage names.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-value", "keyValuePairs",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "value-conversion", "valueConversion",
      new ObjectToObject());
  }

  /**
   * Sets the key-value pairs to add.
   *
   * @param value	the pairs
   */
  public void setKeyValuePairs(BaseKeyValuePair[] value) {
    m_KeyValuePairs = value;
    reset();
  }

  /**
   * Returns the key-value pairs to add.
   *
   * @return 		the pairs
   */
  public BaseKeyValuePair[] getKeyValuePairs() {
    return m_KeyValuePairs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyValuePairsTipText() {
    return "The key-value pairs to add (the value represents a storage name).";
  }

  /**
   * Sets the conversion for turning the storage value into the actual type.
   *
   * @param value	the conversion
   */
  public void setValueConversion(Conversion value) {
    m_ValueConversion = value;
    reset();
  }

  /**
   * Returns the conversion for turning the storage value into the actual type.
   *
   * @return 		the conversion
   */
  public Conversion getValueConversion() {
    return m_ValueConversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String valueConversionTipText() {
    return "For converting the storage value into the actual type.";
  }

  /**
   * Updates the document.
   *
   * @param doc		the document to update
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String doUpdate(MongoCollection coll, Document doc) {
    String		result;
    Storage 		storage;
    MessageCollection 	errors;
    Object		val;
    String		msg;

    result = null;

    storage = getFlowContext().getStorageHandler().getStorage();
    errors  = new MessageCollection();
    try {
      for (BaseKeyValuePair pair: m_KeyValuePairs) {
        val = storage.get(new StorageName(pair.getPairValue()));
        m_ValueConversion.setInput(val);
        msg = m_ValueConversion.convert();
        if (msg != null) {
          errors.add("Failed to convert storage item from " + pair + " using " + m_ValueConversion + "\n" + msg);
	}
	else {
          val = m_ValueConversion.getOutput();
	  doc.append(pair.getPairKey(), val);
	}
      }
      if (errors.isEmpty()) {
	coll.deleteOne(Filters.eq("_id", doc.get("_id")));
	coll.insertOne(doc);
      }
    }
    catch (Exception e) {
      errors.add(LoggingHelper.handleException(this, "Failed to update document!", e));
    }

    if (!errors.isEmpty())
      result = errors.toString();

    return result;
  }
}
