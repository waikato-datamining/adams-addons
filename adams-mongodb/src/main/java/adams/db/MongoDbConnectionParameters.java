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
 * MongoDbConnectionParameters.java
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.CloneHandler;
import adams.core.base.BasePassword;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class for connection information.
 *
 *  @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbConnectionParameters
  implements Serializable, Comparable<MongoDbConnectionParameters>, CloneHandler<MongoDbConnectionParameters> {

  /** for serialization. */
  private static final long serialVersionUID = -1414581492377334939L;

  /** the class parameter. */
  public final static String PARAM_CLASS = "Class";

  /** the URL parameter. */
  public final static String PARAM_URL = "URL";

  /** the user parameter. */
  public final static String PARAM_USER = "User";

  /** the password parameter. */
  public final static String PARAM_PASSWORD = "Password";

  /** the authentication database parameter. */
  public final static String PARAM_AUTHDB = "AuthDB";

  /** the logging level parameter. */
  public final static String PARAM_LOGGINGLEVEL = "LoggingLevel";

  /** the connect on startup parameter. */
  public final static String PARAM_CONNECTONSTARTUP = "ConnectOnStartup";

  /** the URL. */
  protected String m_URL;

  /** the user. */
  protected String m_User;

  /** the password. */
  protected BasePassword m_Password;

  /** the authentication database. */
  protected String m_AuthDB;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** whether to connect on startup. */
  protected boolean m_ConnectOnStartUp;

  /**
   * Initializes the container.
   */
  public MongoDbConnectionParameters() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_URL              = "";
    m_User             = "";
    m_Password         = new BasePassword();
    m_AuthDB           = "";
    m_LoggingLevel     = LoggingLevel.OFF;
    m_ConnectOnStartUp = false;
  }

  /**
   * Returns the URL.
   *
   * @return		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the user.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the password.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the authentication database.
   *
   * @return		the db
   */
  public String getAuthDB() {
    return m_AuthDB;
  }

  /**
   * Returns the logging level.
   *
   * @return		the logging level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns whether to connect on startup.
   *
   * @return		true if to connect on startup
   */
  public boolean getConnectOnStartUp() {
    return m_ConnectOnStartUp;
  }

  /**
   * Returns the available parameter keys.
   *
   * @return		the parameter keys
   */
  public List<String> parameters() {
    List<String>	result;

    result = new ArrayList<>();

    result.add(PARAM_CLASS);
    result.add(PARAM_URL);
    result.add(PARAM_USER);
    result.add(PARAM_PASSWORD);
    result.add(PARAM_AUTHDB);
    result.add(PARAM_LOGGINGLEVEL);
    result.add(PARAM_CONNECTONSTARTUP);

    return result;
  }

  /**
   * Returns the parameter for the specified key.
   *
   * @param key		the key of the parameter to retrieve
   * @return		the associated value, null if not available
   */
  public String getParameter(String key) {
    if (key.equals(PARAM_CLASS))
      return getClass().getName();
    if (key.equals(PARAM_URL))
      return m_URL;
    if (key.equals(PARAM_USER))
      return m_User;
    if (key.equals(PARAM_PASSWORD))
      return m_Password.stringValue();
    if (key.equals(PARAM_AUTHDB))
      return m_AuthDB;
    if (key.equals(PARAM_LOGGINGLEVEL))
      return "" + m_LoggingLevel;
    if (key.equals(PARAM_CONNECTONSTARTUP))
      return "" + m_ConnectOnStartUp;

    return null;
  }

  /**
   * Returns the parameter for the specified key.
   *
   * @param key		the key of the parameter to retrieve
   * @param value	the associated value
   */
  public void setParameter(String key, String value) {
    if (key.equals(PARAM_CLASS))
      ;  // ignored
    if (key.equals(PARAM_URL))
      m_URL = value;
    else if (key.equals(PARAM_USER))
      m_User = value;
    else if (key.equals(PARAM_PASSWORD))
      m_Password = new BasePassword(value);
    else if (key.equals(PARAM_AUTHDB))
      m_AuthDB = value;
    else if (key.equals(PARAM_LOGGINGLEVEL))
      m_LoggingLevel = LoggingLevel.valueOf(value);
    else if (key.equals(PARAM_CONNECTONSTARTUP))
      m_ConnectOnStartUp = Boolean.parseBoolean(value);
  }

  /**
   * Returns a new (empty) instance of a ConnectionParameters object.
   *
   * @return		the empty instance
   */
  protected MongoDbConnectionParameters newInstance() {
    return new MongoDbConnectionParameters();
  }

  /**
   * Returns a copy of itself.
   *
   * @return		the copy
   */
  public MongoDbConnectionParameters getClone() {
    MongoDbConnectionParameters result;

    result = newInstance();
    for (String key: parameters())
      result.setParameter(key, getParameter(key));

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(MongoDbConnectionParameters o) {
    int		result;
    Object	oThis;
    Object	oOther;

    if (o == null)
      return 1;

    result = 0;
    for (String key: parameters()) {
      oThis  = getParameter(key);
      oOther = o.getParameter(key);
      if (oOther == null) {
	result = 1;
      }
      else {
	if (oThis instanceof Comparable)
	  result = ((Comparable) oThis).compareTo(oOther);
      }
      if (result != 0)
        break;
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MongoDbConnectionParameters)
      return (compareTo((MongoDbConnectionParameters) obj) == 0);
    else
      return false;
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * "url \t user \t password \t authdb" string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return (m_URL + "\t" + m_User + "\t" + m_Password + "\t" + m_AuthDB).hashCode();
  }

  /**
   * Returns a shortened URL.
   *
   * @return		the shortened URL
   */
  @Override
  public String toString() {
    return m_URL.replaceAll(".*\\/\\/", "");
  }

  /**
   * Returns the instance of a new database connection object.
   *
   * @param dbcon	the database connection object class to instantiate
   * @return		the new database connection object
   */
  public MongoDbConnection toDatabaseConnection(Class dbcon) {
    MongoDbConnection	result;

    try {
      result = (MongoDbConnection) dbcon.newInstance();
      result.setURL(getURL());
      result.setUser(getUser());
      result.setPassword(getPassword());
      result.setAuthDB(getAuthDB());
      result.setLoggingLevel(getLoggingLevel());
      result.setConnectOnStartUp(getConnectOnStartUp());
      MongoDbConnection.getDatabaseManager().add(result);
    }
    catch (Exception e) {
      System.err.println("Failed to create MongoDB connection object:");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Creates a new object based on the classname, falls back to the default
   * class, if instantiation fails.
   *
   * @param classname	the class to instantiate
   * @return		the new object
   */
  public static MongoDbConnectionParameters forName(String classname) {
    MongoDbConnectionParameters result;

    try {
      result = (MongoDbConnectionParameters) ClassManager.getSingleton().forName(classname).newInstance();
    }
    catch (Exception e) {
      result = new MongoDbConnectionParameters();
    }

    return result;
  }
}