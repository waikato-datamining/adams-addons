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
 * MongoDbActorUtils.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Helper class for actors, with additional support for MongoDB.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbActorUtils
  extends ActorUtils {

  /** the debugging level. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(ActorUtils.class);

  /**
   * Returns the database connection object to use.
   *
   * @param actor	the actor start the search from (towards the root)
   * @param cls		the DatabaseConnection actor class to look for
   * @param defCon	the default database connection, in case none is found
   * 			in the flow
   * @return		the connection object to use
   */
  public static adams.db.MongoDbConnection getDatabaseConnection(Actor actor, Class cls, adams.db.MongoDbConnection defCon) {
    Object						closest;
    adams.db.MongoDbConnection			result;

    closest = ActorUtils.findClosestType(actor, cls, true);
    if (closest != null) {
      if (closest instanceof adams.flow.standalone.MongoDbConnection) {
	result = ((adams.flow.standalone.MongoDbConnection) closest).getConnection();
	LOGGER.fine("Database connection found: " + result + "\n" + LoggingHelper.getStackTrace(20));
      }
      else  if (closest instanceof adams.flow.standalone.MongoDbConnection) {
	result = ((adams.flow.standalone.MongoDbConnection) closest).getConnection();
	LOGGER.fine("Database connection found: " + result + "\n" + LoggingHelper.getStackTrace(20));
      }
      else {
	result = defCon;
	LOGGER.warning("Unhandled actor type '" + closest.getClass().getName() + "', using default connection: " + defCon + "\n" + LoggingHelper.getStackTrace(20));
      }
    }
    else {
      result = defCon;
      LOGGER.info("No database connection found, using default: " + defCon + "\n" + LoggingHelper.getStackTrace(20));
    }
    if (!result.isConnected() && result.getConnectOnStartUp()) {
      try {
	result.connect();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE,
	    "Failed to enable database connection (" + cls.getName() + ") for actor " + actor.getFullName() + ":", e);
      }
    }

    return result;
  }
}
