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
 * AbstractCQETable.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.env.CQETableDefinition;
import adams.env.Environment;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for CQEngine tables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the managed type of object
 */
public abstract class AbstractCQETable<T>
  extends LoggingObject {

  private static final long serialVersionUID = 8540313627448845885L;

  /** the name of the props file. */
  public final static String FILENAME = "CQETable.props";

  /** the properties file. */
  protected static Properties m_Properties;

  /** the static logger. */
  protected static Logger LOGGER = LoggingHelper.getConsoleLogger(AbstractCQETable.class);

  /** the SQL parser. */
  protected static SQLParser m_Parser;

  /** name of the table. */
  protected String m_TableName;

  /** whether debugging is turned on. */
  protected boolean m_Debug;

  /** connection to database. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** the object collection. */
  protected IndexedCollection<T> m_Collection;

  /**
   * Constructor.
   *
   * @param dbcon	the database context to use
   * @param tableName	the name of the table
   */
  public AbstractCQETable(AbstractDatabaseConnection dbcon, String tableName) {
    super();

    m_TableName          = tableName;
    m_DatabaseConnection = dbcon;
    m_Collection         = newCollection();
    m_Debug              = getProperties().getBoolean(getClass().getName() + ".Debug", false);

    if (m_Parser == null)
      m_Parser = newParser();

    updatePrefix();
  }

  /**
   * Get name of table.
   *
   * @return	table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Returns a new instance of the parser to use.
   *
   * @return		the parser
   */
  protected abstract SQLParser<T> newParser();

  /**
   * Returns the parser in use.
   *
   * @return		the parser
   */
  public SQLParser<T> getParser() {
    return m_Parser;
  }

  /**
   * Returns a new instance of the collection to use.
   *
   * @return		the collection
   */
  protected abstract IndexedCollection<T> newCollection();

  /**
   * Returns the collection in use.
   *
   * @return		the collection
   */
  public IndexedCollection<T> getCollection() {
    return m_Collection;
  }

  /**
   * Clears the collection.
   */
  public void clear() {
    m_Collection.clear();
  }

  /**
   * Updates the prefix of the console object output streams.
   */
  protected void updatePrefix() {
    String	prefix;

    prefix   = getClass().getName() + "(" + getDatabaseConnection().toStringShort() + "/" + getDatabaseConnection().hashCode() + ")";
    m_Logger = LoggingHelper.getLogger(prefix);
    m_Logger.setLevel(getDebug() ? Level.INFO : Level.OFF);
  }

  /**
   * Backquotes the regular expression and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the regular expression to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(BaseRegExp s) {
    return backquote(s.getValue());
  }

  /**
   * Backquotes the string and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the string to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(String s) {
    String	result;

    result = Utils.backQuoteChars(s);
    if (!result.startsWith("'"))
      result = "'" + result + "'";

    return result;
  }

  /**
   * Returns the database connection this table is for.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets whether debugging is enabled, outputs more on the console.
   *
   * @param value	if true debugging is enabled
   */
  public void setDebug(boolean value) {
    m_Debug = value;
    getLogger().setLevel(value ? Level.INFO : Level.WARNING);
  }

  /**
   * Returns whether debugging is enabled.
   *
   * @return		true if debugging is enabled
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Returns true if this table holds data that satisfies 'condition'.
   *
   * @param condition  boolean SQL eg "JOBNO=100 AND SAMPLENO=2"
   * @return  true if condition holds for tablename
   */
  public boolean isThere(String condition) {
    ResultSet<T> 	rs;
    Iterator<T>		iter;

    try{
      rs   = select(condition);
      iter = rs.iterator();
      if (!iter.hasNext()) {
	closeAll(rs);
	return false;
      }
      else{
	closeAll(rs);
	return true;
      }
    }
    catch(Exception e) {
      return false;
    }
  }

  /**
   * Returns true if this table holds data that satisfies the query condition.
   *
   * @param query	the query to use
   * @return  		true if condition holds for tablename
   */
  public boolean isThere(Query<T> query) {
    ResultSet<T>	rs;
    Iterator<T>		iter;

    try {
      rs   = getCollection().retrieve(query);
      iter = rs.iterator();
      if (!iter.hasNext()) {
	closeAll(rs);
	return false;
      }
      else{
	closeAll(rs);
	return true;
      }
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Do a select on all columns for all data in, with condition.
   *
   * @param where	condition
   * @return		resultset of data
   * @throws Exception	if SQL fails to parse
   */
  public ResultSet<T> select(String where) throws Exception {
    return doSelect(false, where);
  }

  /**
   * Do a select distinct on all columns for all data, with
   * condition.
   *
   * @param where	condition
   * @return		resultset of data
   * @throws Exception	if SQL fails to parse
   */
  public ResultSet<T> selectDistinct(String where) throws Exception {
    return doSelect(true, where);
  }

  /**
   * Do a select on given columns for all data, with condition.
   * Can be distinct.
   *
   * @param distinct	whether values in columns has to be distinct
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception	if SQL fails to parse
   */
  protected ResultSet<T> doSelect(boolean distinct, String where) throws Exception {
    String	query;

    // select
    query = "SELECT ";
    if (distinct)
      query += "DISTINCT ";
    query += "*";

    // from
    query += " FROM " + getTableName();

    // where
    if ((where != null) && (where.length() > 0)) {
      if (   !where.trim().toUpperCase().startsWith("LIMIT ")
	  && !where.trim().toUpperCase().startsWith("ORDER ") )
	query += " WHERE";
      query += " " + where;
    }

    getLogger().info("doSelect: " + query);

    try {
      return getParser().retrieve(getCollection(), query);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to execute query: " + query, e);
      throw e;
    }
  }

  /**
   * Removes all objects in the result set.
   *
   * @param rs		the result of objects to remove
   * @return		true if successfully removed
   */
  protected boolean remove(ResultSet<T> rs) {
    boolean	result;
    Iterator<T>	iter;
    List<T> 	objs;

    objs = new ArrayList<>();
    iter = rs.iterator();
    while (iter.hasNext())
      objs.add(iter.next());
    closeAll(rs);

    if (getDebug())
      getLogger().info("Removing " + objs.size() + " object(s)");

    try {
      result = getCollection().removeAll(objs);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to remove objects!", e);
      return false;
    }

    if (getDebug()) {
      getLogger().info("Removed: " + result);
      getLogger().info("Size: " + getCollection().size());
    }

    return result;
  }

  /**
   * Close objects related to this ResultSet.
   *
   * @param r  The ResultSet to clean up after
   */
  public static void closeAll(ResultSet r) {
    if (r != null) {
      try {
	r.close();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing resultset", e);
      }
    }
  }

  /**
   * Checks whether the table is enabled.
   *
   * @param table	the CQE table class
   * @return		true if enabled
   */
  public static boolean isEnabled(Class table) {
    return getProperties().getBoolean(table.getName() + ".Enabled", false);
  }

  /**
   * Returns the properties. Loads them if necessary.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(CQETableDefinition.KEY);

    return m_Properties;
  }
}
