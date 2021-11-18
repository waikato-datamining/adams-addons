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
 * MongoDbConnection.java
 * Copyright (C) 2018-2021 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.CloneHandler;
import adams.core.CompareUtils;
import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;
import adams.env.AbstractEnvironment;
import adams.env.Environment;
import adams.env.MongoDbConnectionDefinition;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

/**
 * DatabaseConnection manages the interface to the database back-end.
 *
 *  @author  dale (dale at waikato dot ac dot nz)
 */
public class MongoDbConnection
  extends LoggingObject
  implements OptionHandler, DatabaseConnectionParameterHandler,
             Comparable<MongoDbConnection>, CloneHandler<MongoDbConnection> {

  /** for serialization. */
  private static final long serialVersionUID = -3625820307854172417L;

  /** the prefix in the props file for connections. */
  public final static String PREFIX_CONNECTION = "connection";

  /** the suffix in the props file for the number of connections. */
  public final static String SUFFIX_COUNT = "count";

  /** the props file. */
  public final static String FILENAME = "MongoDbConnection.props";

  /** keeping track of environment instances. */
  protected static HashMap<Class, AbstractEnvironment> m_Environments;
  static {
    m_Environments = new HashMap<>();
  }

  /** for managing the database connections. */
  private static MongoDbManager m_DatabaseManager;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /** the listeners in case of connect/disconnect. */
  protected transient HashSet<DatabaseConnectionChangeListener> m_ChangeListeners;

  /** the properties for database access. */
  protected Properties m_Properties;

  /** for keeping track of unsuccessful connection attempts. */
  protected Hashtable<String,Integer> m_FailedConnectAttempts;

  /** the URL to connect to the database. */
  protected String m_URL;

  /** database username. */
  protected String m_User;

  /** database password. */
  protected BasePassword m_Password;

  /** the authentication database. */
  protected String m_AuthDB;

  /** whether to connect on startup. */
  protected boolean m_ConnectOnStartUp;

  /** the maximum number of allowed connection attempts for a driver/URL/user/pw
   * combination. */
  protected int m_MaxConnectAttempts;

  /** the number of seconds to wait before trying to reconnect. */
  protected int m_ReconnectWait;

  /** The database connection. */
  protected transient MongoClient m_Connection;

  /** whether we could connect successfully. */
  protected boolean m_ConnectionOK;

  /** stores the last connection error. */
  protected String m_LastConnectionError;

  /** the database manager this connection belongs to. */
  protected MongoDbManager m_Owner;

  /** the environment to use. */
  protected transient AbstractEnvironment m_Environment;

  /**
   * Constructor, uses the default settings.
   */
  public MongoDbConnection() {
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
    finishInit();
  }

  /**
   * Local Database Constructor. Initialise the driver, and attempt
   * connection to the database specified in the URL, with the given username
   * and password.
   *
   * @param url         the URL
   * @param user        the user to connect with
   * @param password    the password for the user
   * @param authDb	the authentication database
   */
  public MongoDbConnection(String url, String user, BasePassword password, String authDb) {
    this();

    setURL(url);
    setUser(user);
    setPassword(password);
    setAuthDB(authDb);
    setLoggingLevel(getDefaultLoggingLevel());

    if (getConnectOnStartUp()) {
      try{
	connect();
      }
      catch (Exception e) {
	m_LastConnectionError = e.toString();
	getLogger().log(Level.SEVERE, "Failed to connect", e);
      }

      if (m_ConnectionOK)
	m_LastConnectionError = "";
    }
  }

  /**
   * initializes member variables.
   */
  protected void initialize() {
    m_Connection            = null;
    m_FailedConnectAttempts = new Hashtable<>();
    m_ConnectionOK          = false;
    m_LastConnectionError   = "";
    m_Owner                 = null;
  }

  /**
   * Finishes the initialization.
   */
  protected void finishInit() {
  }

  /**
   * Sets the database manager that owns this connection.
   *
   * @param value	the manager
   */
  public void setOwner(MongoDbManager value) {
    m_Owner = value;
  }

  /**
   * Returns the current database manager that owns this connection.
   *
   * @return		the manager
   */
  public MongoDbManager getOwner() {
    return m_Owner;
  }

  /**
   * Returns the change listeners data structure. Initializes it if necessary.
   *
   * @return		the change listeners
   */
  protected HashSet<DatabaseConnectionChangeListener> getChangeListeners() {
    if (m_ChangeListeners == null)
      m_ChangeListeners = new HashSet<>();
    return m_ChangeListeners;
  }

  /**
   * Returns a new instance of the option manager.
   *
   * @return		the manager to use
   */
  protected OptionManager newOptionManager() {
    return new OptionManager(this);
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    m_OptionManager = newOptionManager();

    m_OptionManager.add(
	"logging-level", "loggingLevel",
	getDefaultLoggingLevel());

    m_OptionManager.add(
	"url", "URL",
	getDefaultURL(), false);

    m_OptionManager.add(
	"user", "user",
	getDefaultUser(), false);

    m_OptionManager.add(
	"password", "password",
	getDefaultPassword(), false);

    m_OptionManager.add(
	"auth-db", "authDB",
	getDefaultAuthDB(), false);

    m_OptionManager.add(
	"max-attempts", "maxConnectAttempts",
	getDefaultMaxConnectAttempts());

    m_OptionManager.add(
	"reconnect-wait", "reconnectWait",
	getDefaultReconnectWait());

    m_OptionManager.add(
	"connect-on-startup", "connectOnStartUp",
	getDefaultConnectOnStartUp());
  }

  /**
   * Returns the option manager.
   *
   * @return		the manager
   */
  @Override
  public OptionManager getOptionManager() {
    if (m_OptionManager == null)
      defineOptions();

    return m_OptionManager;
  }

  /**
   * Cleans up the options.
   */
  @Override
  public void cleanUpOptions() {
    if (m_OptionManager != null) {
      m_OptionManager.cleanUp();
      m_OptionManager = null;
    }
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  @Override
  public void destroy() {
    cleanUpOptions();
  }

  /**
   * Returns the properties key to use for retrieving the properties.
   *
   * @return		the key
   */
  protected String getDefinitionKey() {
    return MongoDbConnectionDefinition.KEY;
  }

  /**
   * Creates a new instance of the environment object that we require.
   *
   * @return		the instance
   */
  protected AbstractEnvironment createEnvironment() {
    return new Environment();
  }

  /**
   * Returns the environment instance to use, creates it if necessary.
   *
   * @return		the instance
   */
  protected synchronized AbstractEnvironment getEnvironment() {
    AbstractEnvironment   env;

    // already cached?
    if (m_Environment == null)
      m_Environment = m_Environments.get(getClass());

    // create new one?
    if (m_Environment == null) {
      env = createEnvironment();
      m_Environments.put(getClass(), env);
      m_Environment = env;
    }

    return m_Environment;
  }

  /**
   * Reads the properties.
   *
   * @return		the properties
   * @see		#getDefinitionKey()
   */
  protected synchronized Properties readProperties() {
    return getEnvironment().read(getDefinitionKey());
  }

  /**
   * Returns the properties, loads them on demand.
   *
   * @return		the properties
   */
  public synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = readProperties();

    return m_Properties;
  }

  /**
   * Returns the logging level specified in the props file.
   *
   * @return		the default logging level
   */
  public LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.valueOf(getProperties().getProperty(MongoDbConnectionParameters.PARAM_LOGGINGLEVEL, LoggingLevel.WARNING.toString()));
  }

  /**
   * Sets the default logging level in the props file.
   *
   * @param value	the default logging level
   */
  public void setDefaultLoggingLevel(LoggingLevel value) {
    getProperties().setProperty(MongoDbConnectionParameters.PARAM_LOGGINGLEVEL, value.toString());
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    getLogger().setLevel(value.getLevel());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelTipText() {
    return "The logging level; use FINE or more to have the most detailed output.";
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    super.configureLogger();
    if (getLoggingLevel() != null)
      getLogger().setLevel(getLoggingLevel().getLevel());
  }

  /**
   * Returns the database URL specified in the props file.
   *
   * @return		the default URL, if any
   */
  public String getDefaultURL() {
    return getProperties().getProperty(MongoDbConnectionParameters.PARAM_URL, "");
  }

  /**
   * Sets the default database URL in the props file.
   *
   * @param value	the default URL
   */
  public void setDefaultURL(String value) {
    getProperties().setProperty(MongoDbConnectionParameters.PARAM_URL, value);
  }

  /**
   * Returns the currently set database URL.
   *
   * @return		the current URL
   */
  @Override
  public String getURL() {
    return m_URL;
  }

  /**
   * Sets the database URL to use (only if not connected).
   *
   * @param value	the URL to use
   */
  @Override
  public void setURL(String value) {
    if (isConnected())
      return;

    if (value == null)
      return;
    if (value.equals(""))
      return;

    m_URL = value;
    m_ConnectionOK = false;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String URLTipText() {
    return "The database URL to connect to.";
  }

  /**
   * Returns the user specified in the props file.
   *
   * @return		the default user, if any
   */
  public String getDefaultUser() {
    return getProperties().getProperty(MongoDbConnectionParameters.PARAM_USER, "");
  }

  /**
   * Sets the default user in the props file.
   *
   * @param value	the default user
   */
  public void setDefaultUser(String value) {
    getProperties().setProperty(MongoDbConnectionParameters.PARAM_USER, value);
  }

  /**
   * Returns the currently set database user.
   *
   * @return		the current user
   */
  @Override
  public String getUser() {
    return m_User;
  }

  /**
   * Sets the database user to use (only if not connected).
   *
   * @param value	the user to use
   */
  @Override
  public void setUser(String value) {
    if (isConnected())
      return;

    m_User = value;
    m_ConnectionOK      = false;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String userTipText() {
    return "The name of the database user.";
  }

  /**
   * Returns the password specified in the props file.
   *
   * @return		the default password, if any
   */
  public BasePassword getDefaultPassword() {
    return new BasePassword(getProperties().getProperty(MongoDbConnectionParameters.PARAM_PASSWORD, ""));
  }

  /**
   * Sets the default password in the props file.
   *
   * @param value	the default password
   */
  public void setDefaultPassword(BasePassword value) {
    getProperties().setProperty(MongoDbConnectionParameters.PARAM_PASSWORD, value.stringValue());
  }

  /**
   * Returns the currently set database password.
   *
   * @return		the current password
   */
  @Override
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Sets the database password to use (only if not connected).
   *
   * @param value	the password to use
   */
  @Override
  public void setPassword(BasePassword value) {
    if (isConnected())
      return;

    m_Password = value;
    m_ConnectionOK      = false;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String passwordTipText() {
    return "The password of the database user.";
  }

  /**
   * Returns the authentication database specified in the props file.
   *
   * @return		the default database, if any
   */
  public String getDefaultAuthDB() {
    return getProperties().getProperty(MongoDbConnectionParameters.PARAM_AUTHDB, "");
  }

  /**
   * Sets the default authentication database in the props file.
   *
   * @param value	the default database
   */
  public void setDefaultAuthDB(String value) {
    getProperties().setProperty(MongoDbConnectionParameters.PARAM_AUTHDB, value);
  }

  /**
   * Returns the currently set authentication database.
   *
   * @return		the database
   */
  public String getAuthDB() {
    return m_AuthDB;
  }

  /**
   * Sets the authentication database to use (only if not connected).
   *
   * @param value	the database
   */
  public void setAuthDB(String value) {
    if (isConnected())
      return;

    m_AuthDB       = value;
    m_ConnectionOK = false;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String authDBTipText() {
    return "The name of the authentication database.";
  }

  /**
   * Returns the maximum number of connection attempts for a driver/URL/user/pw
   * combination.
   *
   * @return		the max number
   */
  public int getMaxConnectAttempts() {
    return m_MaxConnectAttempts;
  }

  /**
   * Sets the maximum number of connection attempts for a driver/URL/user/pw
   * combination (only if not connected).
   *
   * @param value	the max number
   */
  public void setMaxConnectAttempts(int value) {
    if (isConnected())
      return;

    m_MaxConnectAttempts = value;
    m_ConnectionOK        = false;
  }

  /**
   * Returns the maximum number of connection attempts.
   *
   * @return		the default maximum number of attempts
   */
  public int getDefaultMaxConnectAttempts() {
    return getProperties().getInteger("MaxConnectAttempts", 1);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxConnectAttemptsTipText() {
    return "The maximum number of connection attempts for a driver/URL/user/pw combination.";
  }

  /**
   * Returns the number of seconds to wait before trying to reconnect.
   *
   * @return		the number of seconds
   */
  public int getReconnectWait() {
    return m_ReconnectWait;
  }

  /**
   * Sets the number of seconds to wait before trying to reconnect.
   *
   * @param value	the number of seconds
   */
  public void setReconnectWait(int value) {
    if (isConnected())
      return;

    m_ReconnectWait = value;
    m_ConnectionOK   = false;
  }

  /**
   * Returns the number of seconds to wait before trying to reconnect.
   *
   * @return		the default number of seconds
   */
  public int getDefaultReconnectWait() {
    return getProperties().getInteger("ReconnectWait", 10);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reconnectWaitTipText() {
    return "The number of seconds to wait before trying to reconnect.";
  }

  /**
   * Returns whether to automatically connect on startup, i.e., when
   * obtaining the singleton for the first time.
   *
   * @return		true if to connect automatically on startup
   */
  public boolean getDefaultConnectOnStartUp() {
    return getProperties().getBoolean(MongoDbConnectionParameters.PARAM_CONNECTONSTARTUP, false);
  }

  /**
   * Sets the default for whether to connect on startup.
   *
   * @param value	the default
   */
  public void setDefaultConnectOnStartUp(boolean value) {
    getProperties().setBoolean(MongoDbConnectionParameters.PARAM_CONNECTONSTARTUP, value);
  }

  /**
   * Returns whether to use connect on start-up.
   *
   * @return		true if connecting on startup
   */
  public boolean getConnectOnStartUp() {
    return m_ConnectOnStartUp;
  }

  /**
   * Sets whether to connect on start-up (only if not connected).
   *
   * @param value	true if to connect on startup
   */
  public void setConnectOnStartUp(boolean value) {
    if (isConnected())
      return;

    m_ConnectOnStartUp = value;
    m_ConnectionOK     = false;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectOnStartUpTipText() {
    return "Whether to connect on startup.";
  }

  /**
   * Returns the current connection parameters.
   *
   * @return		the parameters
   */
  public MongoDbConnectionParameters getCurrentConnection() {
    MongoDbConnectionParameters	result;

    result = newConnectionParameters();
    result.setParameter(MongoDbConnectionParameters.PARAM_URL, getURL());
    result.setParameter(MongoDbConnectionParameters.PARAM_USER, getUser());
    result.setParameter(MongoDbConnectionParameters.PARAM_PASSWORD, getPassword().stringValue());
    result.setParameter(MongoDbConnectionParameters.PARAM_AUTHDB, getAuthDB());
    result.setParameter(MongoDbConnectionParameters.PARAM_LOGGINGLEVEL, "" + getLoggingLevel());
    result.setParameter(MongoDbConnectionParameters.PARAM_CONNECTONSTARTUP, "" + getConnectOnStartUp());

    return result;
  }

  /**
   * Generates a key for the failed connect attempt hashtable.
   *
   * @param url		the URL
   * @param user	the database user
   * @param password	the database password
   * @param authDB	the authentication database
   * @return		the generated key for the hashtable
   */
  protected String getFailedConnectAttemptKey(String url, String user, BasePassword password, String authDB) {
    String	result;

    result = url + "\t" + user + "\t" + password + "\t" + authDB;

    return result;
  }

  /**
   * Returns the failed attempt count for the given connection.
   *
   * @param url		the URL
   * @param user	the database user
   * @param password	the database password
   * @param authDB	the authentication database
   * @return		the current count
   */
  protected int getFailedConnectAttempt(String url, String user, BasePassword password, String authDB) {
    String	key;
    int		result;

    key = getFailedConnectAttemptKey(url, user, password, authDB);

    // get current count
    if (!m_FailedConnectAttempts.containsKey(key))
      result = 0;
    else
      result = m_FailedConnectAttempts.get(key);

    return result;
  }

  /**
   * Increments the failed attempt for the given connection.
   *
   * @param url		the URL
   * @param user	the database user
   * @param password	the database password
   * @param authDB 	the authenticaton database
   */
  protected void incFailedConnectAttempt(String url, String user, BasePassword password, String authDB) {
    String	key;
    int		count;

    key = getFailedConnectAttemptKey(url, user, password, authDB);

    // get current count
    count = getFailedConnectAttempt(url, user, password, authDB);

    // increment count
    count++;

    // store count again
    m_FailedConnectAttempts.put(key, count);

    m_LastConnectionError = "Failed connection attempt: URL=" + url + ", user=" + user + ", pw=" + password + ", authdb=" + authDB;
  }

  /**
   * Resets the failed attempt for the given connection.
   *
   * @param url		the URL
   * @param user	the database user
   * @param password	the database password
   * @param authDB 	the authentication database
   */
  protected void resetFailedConnectAttempt(String url, String user, BasePassword password, String authDB) {
    String	key;

    key = getFailedConnectAttemptKey(url, user, password, authDB);

    // reset count
    m_FailedConnectAttempts.put(key, 0);

    m_LastConnectionError = "";
  }

  /**
   * Returns the last error that happened when connecting to the database.
   *
   * @return		the last error, "" if non occurred
   */
  public String getLastConnectionError() {
    return m_LastConnectionError;
  }

  /**
   * Return db connection.
   * @param keepTrying	continue attempting to connect
   * @return	new Connection
   */
  public synchronized MongoClient getConnection(boolean keepTrying) {
    if (!m_ConnectionOK) {
      if (keepTrying) {
	if (!tryConnection())
	  return null;
      }
      else {
	return null;
      }
    }
    return m_Connection;
  }

  /**
   * Tries to reconnect.
   *
   * @return	true if successful
   */
  protected boolean tryConnection() {
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().log(Level.FINE, "tryConnection request originated from:\n" + LoggingHelper.getStackTrace(-1));
    while (!m_ConnectionOK) {
      if (m_LastConnectionError.indexOf("CommunicationsException") > -1) {
	return false;
      }
      else if (getFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB) >= m_MaxConnectAttempts) {
	m_LastConnectionError = "Too many failed connection attempts: URL=" + m_URL + ", user=" + m_User + ", pw=" + m_Password;
	return false;
      }
      else {
	try {
	  connect();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to connect", e);
	}
	if (!m_ConnectionOK) {
	  try {
	    for (int i = 0; i < m_ReconnectWait * 10; i++) {
	      wait(100);
	      if (getFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB) >= m_MaxConnectAttempts) {
		m_LastConnectionError = "Too many failed connection attempts: URL=" + m_URL + ", user=" + m_User + ", pw=" + m_Password;
		return false;
	      }
	    }
	  }
	  catch (InterruptedException e) {
	    getLogger().log(Level.SEVERE, "Interrupted", e);
	  }
	}
      }
    }
    return true;
  }

  /**
   * Returns if connection to database is OK.
   *
   * @return 		true if connection OK
   */
  public boolean isConnectionOK() {
    return m_ConnectionOK;
  }

  /**
   * Is database connected?
   *
   * @return 		database connected?
   */
  public synchronized boolean isConnected() {
    boolean 	result;

    result = (m_Connection != null);
    if (result) {
      try {
        m_Connection.getAddress();
	result = true;
      }
      catch (Exception e) {
	result = false;
      }
      if (!result)
	m_Connection = null;
    }

    return result;
  }

  /**
   * Redo connection to database.
   *
   * @return	success?
   */
  public synchronized boolean retryConnect() {
    disconnect();

    try {
      return connect();
    }
    catch (Exception e) {
     return false;
    }
  }

  /**
   * Try to make a connection to the database, with the url,username and password.
   *
   * @return  connect OK
   * @throws Exception	if connection cannot be instantiated
   */
  public synchronized boolean connect() throws Exception {
    MongoCredential 	cred;
    MongoDbUrl 		url;

    m_LastConnectionError = "";
    getLogger().info("connecting: " + m_URL);
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().log(Level.FINE, "Connection request originated from:\n" + LoggingHelper.getStackTrace(-1));
    if (!isConnected()) {
      // have we already exceeded the number of attempts?
      if (getFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB) >= getMaxConnectAttempts()) {
	m_ConnectionOK = false;
	m_LastConnectionError = "Maximum number of connection attempts reached: URL=" + m_URL + ", user=" + m_User + ", pw=" + m_Password.getMaskedValue();
	getLogger().severe(m_LastConnectionError);
	return false;
      }

      try {
        cred = null;
        url  = new MongoDbUrl(m_URL);
        if (!m_User.isEmpty())
	  cred = MongoCredential.createCredential(m_User, url.uriValue().getDatabase(), m_Password.stringValue().toCharArray());
        if (cred == null)
	  m_Connection = new MongoClient(url.uriValue());
        else
          m_Connection = new MongoClient(new ServerAddress(url.hostValue(), url.portValue()), MongoClientOptions.builder().build());
      }
      catch(Exception e) {
	m_Connection          = null;
	m_LastConnectionError = e.toString();
      }

      if (m_Connection == null) {
	getLogger().severe(
	    "Cannot connect: " + m_LastConnectionError + "\n"
		+ "- URL: " + m_URL + "\n"
		+ "- user: " + m_User + "\n"
		+ "- pw: " + m_Password.getMaskedValue() + "\n");
	incFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB);
	return false;
      }
    }

    m_ConnectionOK = isConnected();
    if (!m_ConnectionOK)
      incFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB);
    else
      resetFailedConnectAttempt(m_URL, m_User, m_Password, m_AuthDB);
    notifyChangeListeners(new DatabaseConnectionChangeEvent(this, EventType.CONNECT));

    return(m_ConnectionOK);
  }

  /**
   * Attempt disconnect from db.
   *
   * @return success?
   */
  public synchronized boolean disconnect() {
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().log(Level.FINE, "Disconnect request originated from:", LoggingHelper.getStackTrace(-1));
    if (m_Connection != null) {
      getLogger().info("disconnecting: " + m_URL);
      try {
	m_Connection.close();
      }
      catch(Exception e) {
	getLogger().severe("Failed to close connection");
	m_Connection = null;
	return false;
      }
      m_Connection = null;
    }

    m_ConnectionOK = false;

    notifyChangeListeners(new DatabaseConnectionChangeEvent(this, EventType.DISCONNECT));

    return true;
  }

  /**
   * Returns a new instance of a ConnectionParameters object.
   *
   * @return		the new instance
   */
  public MongoDbConnectionParameters newConnectionParameters() {
    return new MongoDbConnectionParameters();
  }

  /**
   * Returns the default connection parameter object.
   *
   * @return		the default
   */
  public MongoDbConnectionParameters getDefaultConnection() {
    MongoDbConnectionParameters	result;

    result = newConnectionParameters();
    result.setParameter(MongoDbConnectionParameters.PARAM_URL, getDefaultURL());
    result.setParameter(MongoDbConnectionParameters.PARAM_USER, getDefaultUser());
    result.setParameter(MongoDbConnectionParameters.PARAM_PASSWORD, getDefaultPassword().stringValue());
    result.setParameter(MongoDbConnectionParameters.PARAM_AUTHDB, getDefaultAuthDB());
    result.setParameter(MongoDbConnectionParameters.PARAM_LOGGINGLEVEL, "" + getDefaultLoggingLevel());
    result.setParameter(MongoDbConnectionParameters.PARAM_CONNECTONSTARTUP, "" + getDefaultConnectOnStartUp());

    return result;
  }

  /**
   * Returns the stored connections.
   *
   * @return		the connections
   */
  public List<MongoDbConnectionParameters> getConnections() {
    List<MongoDbConnectionParameters>	result;
    MongoDbConnectionParameters		conn;
    Properties				props;
    int					count;
    int					i;
    Enumeration<String>			keys;
    String				key;
    String				prefix;

    result = new ArrayList<>();

    // default one
    result.add(getDefaultConnection());

    // others
    props = getProperties();
    if (props.hasKey(PREFIX_CONNECTION + "." + SUFFIX_COUNT)) {
      count = props.getInteger(PREFIX_CONNECTION + "." + SUFFIX_COUNT, 0);
      for (i = 0; i < count; i++) {
	prefix = PREFIX_CONNECTION  + "." + i + ".";
	conn   = MongoDbConnectionParameters.forName(props.getProperty(prefix + ConnectionParameters.PARAM_CLASS));
	keys   = props.propertyNames(PREFIX_CONNECTION  + "\\." + i + "\\." + ".*");
	while (keys.hasMoreElements()) {
	  key = keys.nextElement();
	  conn.setParameter(key.substring(prefix.length()), props.getProperty(key));
	}
	if (!result.contains(conn))
	  result.add(conn);
      }
    }

    return result;
  }

  /**
   * Adds the given connection to the props file.
   *
   * @param conn	the connection to add
   * @return		true if successfully added
   */
  public boolean addConnection(MongoDbConnectionParameters conn) {
    boolean				result;
    List<MongoDbConnectionParameters>	connections;
    Properties				props;
    int					i;
    String 				value;

    // insert connection as most recent
    connections = getConnections();
    if (connections.contains(conn))
      connections.remove(conn);
    connections.add(0, conn);

    // update properties file
    props = getProperties();
    props.removeWithPrefix(PREFIX_CONNECTION);
    props.setInteger(PREFIX_CONNECTION + "." + SUFFIX_COUNT, connections.size());
    for (i = 0; i < connections.size(); i++) {
      props.setProperty(PREFIX_CONNECTION  + "." + i + "." + ConnectionParameters.PARAM_CLASS, conn.getClass().getName());
      for (String param: connections.get(i).parameters()) {
	value = connections.get(i).getParameter(param);
        props.setProperty(PREFIX_CONNECTION  + "." + i + "." + param, value);
      }
    }

    result = updateConnections();
    if (!result)
      System.err.println("Error adding connection: " + conn);

    return result;
  }

  /**
   * Sets the given connection as the default one.
   *
   * @param conn	the connection to use as default
   * @return		true if successfully set as default
   */
  public synchronized boolean makeDefaultConnection(MongoDbConnectionParameters conn) {
    boolean	result;

    setDefaultURL(conn.getURL());
    setDefaultUser(conn.getUser());
    setDefaultPassword(conn.getPassword());
    setDefaultAuthDB(conn.getAuthDB());
    setDefaultLoggingLevel(conn.getLoggingLevel());
    setDefaultConnectOnStartUp(conn.getConnectOnStartUp());

    result = updateConnections();
    if (!result) {
      System.err.println("Error setting default connection: " + conn);
    }
    else {
      if (getOwner() != null)
	getOwner().setDefault(getDefaultConnection().toDatabaseConnection(getClass()));
    }

    return result;
  }

  /**
   * Updates the props file in the project's home directory.
   *
   * @return		true if sucessfully updated
   */
  public boolean updateConnections() {
    return getEnvironment().write(getDefinitionKey(), getProperties());
  }

  /**
   * Outputs the change listeners if debugging is on.
   */
  protected void outputChangeListeners() {
    getLogger().fine("DB change listeners: #" + getChangeListeners().size());
    for (DatabaseConnectionChangeListener list: getChangeListeners())
      getLogger().fine("  " + list.getClass().getName());
  }

  /**
   * Adds a listener for connect/disconnect events to the internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(DatabaseConnectionChangeListener l) {
    getChangeListeners().add(l);
    if (isLoggingEnabled())
      outputChangeListeners();
  }

  /**
   * Removes a listener for connect/disconnect events from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(DatabaseConnectionChangeListener l) {
    getChangeListeners().remove(l);
    if (isLoggingEnabled())
      outputChangeListeners();
  }

  /**
   * Notifies all listeners with the given event.
   *
   * @param e		the event to send to the listeners
   */
  public synchronized void notifyChangeListeners(DatabaseConnectionChangeEvent e) {
    DatabaseConnectionChangeListener[]	listeners;
    int					count;
    long				start;

    count     = 0;
    listeners = getChangeListeners().toArray(new DatabaseConnectionChangeListener[getChangeListeners().size()]);
    if (isLoggingEnabled())
      getLogger().fine("Notifying about: " + e.getType());
    for (DatabaseConnectionChangeListener listener: listeners) {
      count++;
      start = System.currentTimeMillis();
      listener.databaseConnectionStateChanged(e);
      if (isLoggingEnabled())
	getLogger().fine(
	    count + "/" + getChangeListeners().size() + ": "
	    + listener.getClass().getName()
	    + " (" + (System.currentTimeMillis() - start) + "ms)");
    }
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * Uses the database URL and user for comparison.
   *
   * @param o 	the object to be compared.
   * @return	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  @Override
  public int compareTo(MongoDbConnection o) {
    int		result;

    if (o == null)
      return 1;

    result = CompareUtils.compare(getURL(), o.getURL());

    if (result == 0)
      result = CompareUtils.compare(getUser(), o.getUser());

    return result;
  }

  /**
   * Checks wether this object is the same as the provided one.
   * Only uses the database URL for comparison.
   *
   * @param o		the object to compare with
   * @return		true if the same URL
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof MongoDbConnection)
      return (compareTo((MongoDbConnection) o) == 0);
    else
      return false;
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * "url \t user" string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return (getURL() + "\t" + getUser()).hashCode();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public synchronized MongoDbConnection getClone() {
    MongoDbConnection result;

    result = (MongoDbConnection) OptionUtils.shallowCopy(this, false);

    // non-option-related members
    if (isConnected()) {
      result.m_Connection          = m_Connection;
      result.m_ConnectionOK        = m_ConnectionOK;
      result.m_LastConnectionError = m_LastConnectionError;
    }

    result.getChangeListeners().addAll(getChangeListeners());
    if (getOwner() != null)
      getOwner().add(result);

    return result;
  }

  /**
   * Returns a short string representation of the connection.
   *
   * @return		a short string representation
   */
  public String toStringShort() {
    String	result;

    result = getURL().replaceAll(".*:\\/\\/", "").replaceAll("\\..*\\/", "/");

    return result;
  }

  /**
   * Returns a string representation of the connection object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = "url=" + getURL();
    result += ", user=" + getUser();
    result += ", password=" + getPassword();
    result += ", authDB=" + getAuthDB();
    result += ", connected=" + isConnected();
    result += ", #listeners=" + getChangeListeners().size();

    return result;
  }

  /**
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  public String toCommandLine() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Returns the database manager, instantiates it if necessary.
   *
   * @return		the manager
   */
  protected static synchronized MongoDbManager getMongoDatabaseManager() {
    if (m_DatabaseManager == null) {
      m_DatabaseManager = new MongoDbManager("adams");
      MongoDbConnection dbcon = new MongoDbConnection();
      m_DatabaseManager.setDefault(getSingleton(dbcon.getURL(), dbcon.getUser(), dbcon.getPassword(), dbcon.getAuthDB()));
    }
    return m_DatabaseManager;
  }

  /**
   * Returns the global database connection object. If not instantiated yet, it
   * will automatically try to connect to the database server.
   *
   * @param url		the database URL
   * @param user	the database user
   * @param password	the database password
   * @param authDB	the authentication database
   * @return		the singleton
   */
  public static synchronized MongoDbConnection getSingleton(String url, String user, BasePassword password, String authDB) {
    if (!getMongoDatabaseManager().has(url, user, password, authDB)) {
      getMongoDatabaseManager().add(new MongoDbConnection(url, user, password, authDB));
    }
    else {
      if (!getMongoDatabaseManager().get(url, user, password, authDB).isConnected()) {
	try {
          getMongoDatabaseManager().get(url, user, password, authDB).connect();
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    return getMongoDatabaseManager().get(url, user, password, authDB);
  }

  /**
   * Returns the global database connection object. If not instantiated yet, it
   * can automatically try to connect to the database server, depending on the
   * default in the props file (SUFFIX_CONNECTONSTARTUP).
   *
   * @return		the singleton
   * @see		#getConnectOnStartUp()
   */
  public static synchronized MongoDbConnection getSingleton() {
    return getMongoDatabaseManager().getDefault();
  }

  /**
   * Returns the database manager.
   *
   * @return		the manager
   */
  public static MongoDbManager getDatabaseManager() {
    return getMongoDatabaseManager();
  }
}
