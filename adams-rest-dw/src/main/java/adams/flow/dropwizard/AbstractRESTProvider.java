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
 * AbstractRESTProvider.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.dropwizard;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.standalone.DropwizardRESTServer;
import com.github.fracpete.javautils.struct.Struct2;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

import java.util.logging.Level;

/**
 * Ancestor for servers providing webservices.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRESTProvider
  extends AbstractOptionHandler
  implements RESTProvider {

  /** for serialization. */
  private static final long serialVersionUID = 5989094825183495544L;

  /** the YAML config file to use. */
  protected PlaceholderFile m_ConfigFile;

  /** the owning actor. */
  protected Actor m_Owner;

  /** whether the webservice is running. */
  protected boolean m_Running;

  /** the application. */
  protected Application m_Application;

  /** the environment in use. */
  protected Environment m_Environment;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "config-file", "configFile",
      new PlaceholderFile());
  }

  /**
   * Sets the YAML config file to use.
   *
   * @param value	the file
   */
  @Override
  public void setConfigFile(PlaceholderFile value) {
    m_ConfigFile = value;
    reset();
  }

  /**
   * Returns the YAML config file in use.
   *
   * @return		the file
   */
  @Override
  public PlaceholderFile getConfigFile() {
    return m_ConfigFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String configFileTipText() {
    return "The YAML config file to use for the application, see here for details "
	     + "on the format: https://www.dropwizard.io/en/stable/manual/configuration.html";
  }

  /**
   * Sets the actor that executes this webservice.
   *
   * @param value	the owner
   */
  @Override
  public void setFlowContext(Actor value) {
    m_Owner = value;
  }

  /**
   * Returns the owning actor.
   *
   * @return		the owner
   */
  @Override
  public Actor getFlowContext() {
    return m_Owner;
  }

  /**
   * Performs some initial checks before starting the service.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }

  /**
   * Logs an error message if a valid global log actor has been set up.
   *
   * @param msg		the message to log
   * @param id		an optional ID of the data token that failed in the web service
   */
  public void log(String msg, String id) {
    if (m_Owner instanceof DropwizardRESTServer)
      ((DropwizardRESTServer) m_Owner).log(msg, id);
  }

  /**
   * Performs the actual start of the service.
   *
   * @return 		the tuple of application and environment
   * @throws Exception	if start fails
   */
  protected abstract Struct2<Application,Environment> doStart() throws Exception;

  /**
   * Starts the service.
   *
   * @return 		null if successful, otherwise error message
   */
  public String start() {
    String				result;
    String				msg;
    Struct2<Application,Environment> 	service;

    try {
      check();
      service       = doStart();
      m_Application = service.value1;
      m_Environment = service.value2;
      result        = null;
      m_Running     = true;
    }
    catch (Exception e) {
      msg = "Failed to start service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + LoggingHelper.throwableToString(e);
    }

    return result;
  }

  /**
   * Returns whether the service is running.
   *
   * @return		true if running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Performs the actual stop of the service.
   *
   * @throws Exception	if stopping fails
   */
  protected void doStop() throws Exception {
    if (m_Environment != null)
      m_Environment.getApplicationContext().getServer().stop();
  }

  /**
   * Stops the service.
   *
   * @return		null if successful, otherwise error message
   */
  public String stop() {
    String	result;
    String	msg;

    try {
      doStop();
      m_Application = null;
      m_Environment = null;
      result        = null;
    }
    catch (Exception e) {
      msg = "Failed to stop service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + LoggingHelper.throwableToString(e);
    }

    m_Running = false;

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}
