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
 * AbstractMongoDbBooleanCondition.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.db.MongoDbConnection;
import adams.flow.core.Actor;
import adams.flow.core.MongoDbActorUtils;
import adams.flow.core.Token;

/**
 * Ancestor for MongoDB conditions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMongoDbBooleanCondition
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -2563430699960393494L;

  /** the database connection. */
  protected MongoDbConnection m_DatabaseConnection;

  /** whether the DB connection has been updated. */
  protected boolean m_DatabaseConnectionUpdated;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = getDefaultDatabaseConnection();
  }

  /**
   * Resets the converter.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnectionUpdated = false;
  }

  /**
   * Returns the default database connection.
   *
   * @return 		the default database connection
   */
  protected MongoDbConnection getDefaultDatabaseConnection() {
    return MongoDbConnection.getSingleton();
  }

  /**
   * Returns the database connection from the flow.
   *
   * @param actor	the actor to use for determining the connection
   * @return		the connection
   */
  protected MongoDbConnection getConnection(Actor actor) {
    return MongoDbActorUtils.getDatabaseConnection(
	actor, adams.flow.standalone.DatabaseConnectionProvider.class, getDefaultDatabaseConnection());  }

  /**
   * Uses the token to determine the evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		null if OK, otherwise error message
   */
  protected String preEvaluate(Actor owner, Token token) {
    String	result;

    result = super.preEvaluate(owner, token);

    if (result == null) {
      if (!m_DatabaseConnectionUpdated) {
	m_DatabaseConnectionUpdated = true;
	if (owner instanceof Actor)
	  m_DatabaseConnection = getConnection(owner);
      }
    }

    return result;
  }
}
