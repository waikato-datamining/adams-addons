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

/**
 * AbstractCQETable.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.env.CQETableDefinition;
import adams.env.Environment;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.parser.sql.SQLParser;

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

  /** name of the table. */
  protected String m_TableName;

  /** whether debugging is turned on. */
  protected boolean m_Debug;

  /** connection to database. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** the SQL parser. */
  protected SQLParser<T> m_Parser;

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
    m_Parser             = newParser();
    m_Collection         = newCollection();
    m_Debug              = getProperties().getBoolean(getClass().getName() + ".Debug", false);

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
