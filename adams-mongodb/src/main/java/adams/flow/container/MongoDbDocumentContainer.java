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
 * MongoDbDocumentContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for a MongoDB document with the collection that it belongs to.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbDocumentContainer
  extends AbstractContainer {

  private static final long serialVersionUID = -3008555190103834834L;

  /** the key for the collection. */
  public final static String VALUE_COLLECTION = "Collection";

  /** the key for the document. */
  public final static String VALUE_DOCUMENT = "Document";

  /**
   * Default constructor.
   */
  public MongoDbDocumentContainer() {
    super();
  }

  /**
   * Initializes the container with the collection and document.
   *
   * @param coll	the collection
   * @param doc		the document from this collection
   */
  public MongoDbDocumentContainer(MongoCollection coll, Document doc) {
    this();

    store(VALUE_COLLECTION, coll);
    store(VALUE_DOCUMENT,   doc);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_COLLECTION, "MongoDB collection", MongoCollection.class);
    addHelp(VALUE_DOCUMENT, "MongoDB document", Document.class);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_COLLECTION);
    result.add(VALUE_DOCUMENT);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_COLLECTION) && hasValue(VALUE_DOCUMENT);
  }
}
