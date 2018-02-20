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
 * AbstractMongoDbTransformer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.db.DatabaseConnectionUser;
import adams.db.MongoDbConnection;
import adams.flow.core.MongoDbActorUtils;

/**
 * Abstract ancestor for transformers that query a MongoDB.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMongoDbTransformer
  extends AbstractTransformer
  implements DatabaseConnectionUser {

  /** for serialization. */
  private static final long serialVersionUID = 2514053117324145611L;

  /** the database connection. */
  protected MongoDbConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }

  /**
   * Returns the default database connection.
   *
   * @return 		the default database connection
   */
  protected adams.db.MongoDbConnection getDefaultDatabaseConnection() {
    return MongoDbConnection.getSingleton();
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected adams.db.MongoDbConnection getDatabaseConnection() {
    return MongoDbActorUtils.getDatabaseConnection(
      this, adams.flow.standalone.DatabaseConnectionProvider.class, getDefaultDatabaseConnection());
  }

  /**
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpDatabaseConnection() {
    String	result;

    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    return result;
  }

  /**
   * Performs the actual database query.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected abstract String queryDatabase();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = setUpDatabaseConnection();

    if (result == null)
      result = queryDatabase();

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
