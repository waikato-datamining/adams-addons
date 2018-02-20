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
 * MongoDbDocumentUpdate.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbdocumentupdate;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.FlowContextHandler;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Interface for MongoDB document update schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MongoDbDocumentUpdate
  extends OptionHandler, FlowContextHandler, QuickInfoSupporter {

  /**
   * Updates the document.
   *
   * @param coll	the collection the document belongs to
   * @param doc		the document to update
   * @return		null if successful, otherwise the error message
   */
  public String update(MongoCollection coll, Document doc);
}
