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
 * MongoDbFindDocuments.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.FlowContextHandler;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Interface for MongoDB collection update schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MongoDbFindDocuments
  extends OptionHandler, FlowContextHandler, QuickInfoSupporter {

  /**
   * Filters the collection.
   *
   * @param coll	the collection to filter
   * @return		the documents matching the query
   * @throws IllegalStateException	if check fails
   */
  public FindIterable<Document> find(MongoCollection coll);
}
