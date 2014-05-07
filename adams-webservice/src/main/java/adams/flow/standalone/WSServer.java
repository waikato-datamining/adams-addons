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
 * WSServer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;


import java.util.HashSet;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionHandler;
import adams.db.LogEntry;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.webservice.WebServiceProvider;

import com.example.customerservice.flow.CustomerServiceWS;

/**
 <!-- globalinfo-start -->
 * Runs a webservice.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WSServer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-web-service &lt;adams.flow.webservice.WebServiceProvider&gt; (property: webService)
 * &nbsp;&nbsp;&nbsp;The webservice to provide.
 * &nbsp;&nbsp;&nbsp;default: com.example.customerservice.flow.CustomerServiceWS
 * </pre>
 * 
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not 
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WSServer
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 7347507489169005088L;
  
  /** the webservice to run. */
  protected WebServiceProvider m_WebService;

  /** the callable name. */
  protected CallableActorReference m_Log;

  /** the callable log actor. */
  protected AbstractActor m_LogActor;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Runs a webservice.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "web-service", "webService",
	    new CustomerServiceWS());

    m_OptionManager.add(
	    "log", "log",
	    new CallableActorReference("unknown"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the webservice to use.
   * 
   * @param value	the webservice to use
   */
  public void setWebService(WebServiceProvider value) {
    m_WebService = value;
    m_WebService.setOwner(this);
    reset();
  }
  
  /**
   * Returns the webservice in use.
   * 
   * @return		the webservice in use
   */
  public WebServiceProvider getWebService() {
    return m_WebService;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String webServiceTipText() {
    return "The webservice to provide.";
  }

  /**
   * Sets the name of the callable log actor to use.
   *
   * @param value 	the callable name
   */
  public void setLog(CallableActorReference value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the name of the callable log actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return "The name of the callable log actor to use (logging disabled if actor not found).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "webService", m_WebService.getClass());
    
    result += " on ";
    if (m_WebService instanceof OptionHandler)
      result += QuickInfoHelper.toString(((OptionHandler) m_WebService), "URL", m_WebService.getURL());
    else
      result += m_WebService.getURL();

    result += " [logging to '";
    result += QuickInfoHelper.toString(this, "log", getLog());
    result += "']";
    
    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getLog());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_LogActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  public AbstractActor getCallableActor() {
    return m_LogActor;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    HashSet<String>	variables;
    String		msg;
    Compatibility	comp;

    result = super.setUp();

    if (result == null) {
      m_LogActor = findCallableActor();
      if (m_LogActor == null) {
        msg = "Couldn't find callable log actor '" + getLog() + "' - logging disabled!";
        getLogger().severe(msg);
      }
      else {
	comp = new Compatibility();
	if (!comp.isCompatible(new Class[]{LogEntry.class}, ((InputConsumer) m_LogActor).accepts()))
	  result = "Log actor '" + getLog() + "' must accept " + LogEntry.class.getName() + "!";
	if (result == null) {
	  variables = findVariables(m_LogActor);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	}
      }
    }

    return result;
  }
  
  /**
   * Logs an error message if a valid callable log actor has been set up.
   * 
   * @param msg		the message to log
   * @param id		an optional ID of the data token that failed in the web service
   */
  public void log(String msg, String id) {
    LogEntry		log;
    Properties		props;
    String		result;

    if ((id != null) && (id.trim().length() == 0))
      id = null;
    
    // just log to console if not log actor
    if (m_LogActor == null) {
      getLogger().severe("LOG: " + ((id == null) ? "" : (id + " - ")) + msg);
      return;
    }
    
    // generate log container
    props = new Properties();
    props.setProperty(LogEntry.KEY_ERRORS, msg);
    if (id != null)
      props.setProperty(LogEntry.KEY_ID, id);

    log = new LogEntry();
    log.setType("WebService");
    log.setSource(getFullName());
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);
    
    try {
      synchronized(m_LogActor) {
	((InputConsumer) m_LogActor).input(new Token(log));
	result = m_LogActor.execute();
      }
      if (result != null)
	getLogger().severe("Failed to log message:\n" + log + "\n" + result);
    }
    catch (Exception e) {
      handleException("Failed to log message:\n" + log, e);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_WebService.setOwner(this);
    return m_WebService.start();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_WebService.stop();

    if (m_LogActor != null) {
      synchronized(m_LogActor) {
	m_LogActor.wrapUp();
      }
    }
    
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_LogActor != null) {
      m_LogActor.cleanUp();
      m_LogActor = null;
    }
  }
}
