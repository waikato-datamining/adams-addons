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
 * AbstractRESTPluginWithDatabaseConnection.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.dropwizard;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionUser;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Ancestor for REST plugins that require a database connection.
 * <br>
 * Call {@link #initDatabase()} in each REST method that requires
 * a database connection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRESTPluginWithDatabaseConnection
  extends AbstractRESTPluginWithFlowContext
  implements DatabaseConnectionUser {

  private static final long serialVersionUID = 1118849579499069171L;

  /** the database to use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Resets the plugin.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnection = null;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    super.setFlowContext(value);
    m_DatabaseConnection = null;
  }

  /**
   * Initializes the database from the flow context.
   */
  protected void initDatabase() {
    if (m_FlowContext == null)
      throw new IllegalStateException("No flow context, cannot initialize database connection!");
    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = ActorUtils.getDatabaseConnection(
	m_FlowContext, AbstractDatabaseConnection.class, DatabaseConnection.getSingleton());
      if (m_DatabaseConnection == null)
	throw new IllegalStateException("Failed to initialize database connection!");
    }
  }
}
