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
 * WebsocketOutput.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output;

import adams.flow.core.FlowContextHandler;
import adams.flow.websocket.client.SimpleSendGenerator;
import adams.flow.websocket.client.WebSocketClientGenerator;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Allows to send data to webservices using the supplied client.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-generator &lt;adams.flow.websocket.client.WebSocketClientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The client generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.websocket.client.SimpleSendGenerator
 * </pre>
 * 
 * <pre>-disconnect &lt;boolean&gt; (property: disconnect)
 * &nbsp;&nbsp;&nbsp;If enabled, the connection will get closed immediately after sending the 
 * &nbsp;&nbsp;&nbsp;data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebsocketOutput
  extends AbstractRatOutput {

  private static final long serialVersionUID = 7678239063778818441L;

  /** the client generator. */
  protected WebSocketClientGenerator m_Generator;

  /** whether to disconnect immediately. */
  protected boolean m_Disconnect;

  /** the client instance. */
  protected transient com.pusher.java_websocket.client.WebSocketClient m_Client;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to send data to webservices using the supplied client.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new SimpleSendGenerator());

    m_OptionManager.add(
      "disconnect", "disconnect",
      false);
  }

  /**
   * Sets the client generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(WebSocketClientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the client generator to use
   *
   * @return 		the generator
   */
  public WebSocketClientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The client generator to use.";
  }

  /**
   * Sets whether to immediately disconnect after sending the data.
   *
   * @param value	true if to disconnect immediately
   */
  public void setDisconnect(boolean value) {
    m_Disconnect = value;
    reset();
  }

  /**
   * Returns whether to immediately disconnect after sending the data.
   *
   * @return 		true if to disconnect immediately
   */
  public boolean getDisconnect() {
    return m_Disconnect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String disconnectTipText() {
    return "If enabled, the connection will get closed immediately after sending the data.";
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, byte[].class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    
    result = null;
    
    if (m_Client == null) {
      try {
	m_Client = m_Generator.generateClient();
	if (m_Client instanceof FlowContextHandler)
	  ((FlowContextHandler) m_Client).setFlowContext(getOwner());
      }
      catch (Exception e) {
	result = handleException("Failed to generate websocket client!", e);
      }
    }

    if (result == null) {
      try {
	if (!m_Client.connectBlocking()) {
	  result = "Failed to establish connection!";
	}
	else {
	  if (m_Input instanceof String)
	    m_Client.send((String) m_Input);
	  else
	    m_Client.send((byte[]) m_Input);

	  // close immediately?
	  if (m_Disconnect) {
	    try {
	      m_Client.closeBlocking();
	    }
	    catch (Exception e) {
	      getLogger().log(Level.SEVERE, "Error closing websocket client!", e);
	    }
	  }
	}
      }
      catch (InterruptedException e) {
	// ignored
      }
      catch (Exception e) {
	result = handleException("Failed to connect/send data!", e);
      }
    }
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Client != null) {
      if (m_Client.isOpen()) {
	try {
	  m_Client.closeBlocking();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Error closing websocket client!", e);
	}
	m_Client = null;
      }
    }
    super.cleanUp();
  }
}
