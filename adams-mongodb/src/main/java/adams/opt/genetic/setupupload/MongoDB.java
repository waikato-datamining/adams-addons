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
 * MongoDB.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.setupupload;

import adams.core.Shortening;
import adams.core.logging.LoggingHelper;
import adams.db.MongoDbConnection;
import adams.db.MongoDbUrl;
import adams.flow.core.MongoDbActorUtils;
import adams.opt.genetic.AbstractGeneticAlgorithm;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Map;

/**
 * Stores the setup information in the specified MongoDB collection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDB
  extends AbstractSetupUpload {

  private static final long serialVersionUID = 1825847990988418348L;

  public static final String KEY_SUCCESSFUL = "successful";

  /** the database connection. */
  protected MongoDbConnection m_DatabaseConnection;

  /** the database instance. */
  protected MongoDatabase m_Database;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Stores the setup information in the MongoDB collection (aka experiment).\n"
	+ "Uses the database available through the current flow context.\n"
	+ "If the collection is not present, it gets automatically created.\n"
	+ "On completion of the algorithm run, a document with the key '" + KEY_SUCCESSFUL + "' "
	+ "gets inserted with an associated value of 'true' or 'false' depending on "
	+ "whether the algorithm run was successful. The fitness value is not present in this case.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnection = null;
    m_Database           = null;
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return true;
  }

  /**
   * Initializes the database connection, if necessary.
   *
   * @return		null if successful or already initialized, otherwise error message
   */
  protected String initDatabase() {
    if (m_DatabaseConnection != null)
      return null;
    m_DatabaseConnection = MongoDbActorUtils.getDatabaseConnection(
      getFlowContext(), adams.flow.standalone.DatabaseConnectionProvider.class, new MongoDbConnection());
    if (m_DatabaseConnection == null)
      return "Failed to initialize database connection!";
    return null;
  }

  /**
   * Initializes the table.
   *
   * @return		null if successful or already initialized, otherwise error message
   */
  protected String initCollection() {
    String		result;
    MongoDbUrl		url;
    MongoClient 	mongoclient;
    String		database;

    result = null;

    try {
      url      = new MongoDbUrl(m_DatabaseConnection.getURL());
      database = url.uriValue().getDatabase();
      mongoclient = m_DatabaseConnection.getConnection(true);
      if (mongoclient == null) {
	result = "Failed to obtain connection: " + m_DatabaseConnection.getURL();
      }
      else {
	m_Database = mongoclient.getDatabase(database);
	try {
	  m_Database.getCollection(m_Experiment);
	}
	catch (IllegalArgumentException e) {
	  m_Database.createCollection(m_Experiment);
	}
      }
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to load collection: " + m_Experiment, e);
    }

    return result;
  }

  /**
   * Before Starting the uploads, ie the genetic algorithm run.
   *
   * @param algorithm	the algorithm initiating the run
   */
  protected void doStart(AbstractGeneticAlgorithm algorithm) {
  }

  /**
   * Uploads the setup.
   *
   * @param setup	the setup data to upload
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpload(Map<String, Object> setup) {
    String		result;
    MongoCollection	coll;
    Document 		doc;
    String		bsonKey;

    result = initDatabase();

    if (result == null)
      result = initCollection();

    // prepare statement
    coll = null;
    if (result == null) {
      try {
	coll = m_Database.getCollection(m_Experiment);
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to get collection: " + m_Experiment, e);
      }
    }

    // store setup
    if (coll != null) {
      doc = new Document("_id", setup.get(KEY_MEASURE) + "|" + setup.get(KEY_FITNESS));
      for (String key : setup.keySet()) {
        bsonKey = key.replace(".", "_");
	doc.put(bsonKey, setup.get(key));
	if (isLoggingEnabled())
	  getLogger().info("Inserting name=" + key + ", value=" + Shortening.shortenEnd("" + setup.get(key), 30));
      }
      try {
	coll.insertOne(doc);
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to insert setup document!", e);
      }
    }

    return result;
  }

  /**
   * Finishing up the genetic algorithm run.
   *
   * @param algorithm		the algorithm that initiated the run
   * @param error  		null if successful, otherwise error message
   * @param params              the parameters to store
   */
  @Override
  protected void doFinish(AbstractGeneticAlgorithm algorithm, String error, Map<String,Object> params) {
    params.put(KEY_SUCCESSFUL, error);
    upload(params);
  }
}
