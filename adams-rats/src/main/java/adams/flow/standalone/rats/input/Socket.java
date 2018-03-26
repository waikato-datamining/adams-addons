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
 * Socket.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.net.PortManager;
import adams.flow.core.RunnableWithLogging;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 <!-- globalinfo-start -->
 * Listens on the specified port for incoming data.<br>
 * Can either output raw byte arrays or strings (using the specified encoding).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-max-buffer &lt;int&gt; (property: maxBuffer)
 * &nbsp;&nbsp;&nbsp;The maximum number of items to buffer.
 * &nbsp;&nbsp;&nbsp;default: 65535
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to listen on.
 * &nbsp;&nbsp;&nbsp;default: 8000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-timeout &lt;int&gt; (property: timeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milli-second for waiting on new client connections.
 * &nbsp;&nbsp;&nbsp;default: 3000
 * &nbsp;&nbsp;&nbsp;minimum: 100
 * </pre>
 *
 * <pre>-output-string &lt;boolean&gt; (property: outputString)
 * &nbsp;&nbsp;&nbsp;If enabled, a string with the specified encoding is generated from the incoming
 * &nbsp;&nbsp;&nbsp;byte array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding for sending the data.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Socket
  extends AbstractBufferedRatInput {

  private static final long serialVersionUID = 3258626251085265978L;

  /** the port to listen on. */
  protected int m_Port;

  /** the timeout for the socket. */
  protected int m_Timeout;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to output a string. */
  protected boolean m_OutputString;

  /** the socket in use. */
  protected transient ServerSocket m_Server;

  /** the current client socket. */
  protected transient java.net.Socket m_Client;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Listens on the specified port for incoming data.\n"
	+ "Can either output raw byte arrays or strings (using the specified encoding).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "port", "port",
      8000, 1, 65535);

    m_OptionManager.add(
      "timeout", "timeout",
      3000, 100, null);

    m_OptionManager.add(
      "output-string", "outputString",
      false);

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to listen on.";
  }

  /**
   * Sets the timeout in milli-second to wait for new connections.
   *
   * @param value	the timeout in msec
   */
  public void setTimeout(int value) {
    if (getOptionManager().isValid("timeout", value)) {
      m_Timeout = value;
      reset();
    }
  }

  /**
   * Returns the timeout in milli-second to wait for new connections.
   *
   * @return		the timeout in msec
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeoutTipText() {
    return "The timeout in milli-second for waiting on new client connections.";
  }

  /**
   * Sets whether to output a string.
   *
   * @param value	true if to output a string
   */
  public void setOutputString(boolean value) {
    m_OutputString = value;
    reset();
  }

  /**
   * Returns whether to output a string.
   *
   * @return		true if to output a string
   */
  public boolean getOutputString() {
    return m_OutputString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputStringTipText() {
    return "If enabled, a string with the specified encoding is generated from the incoming byte array.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding for sending the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "port", m_Port, "listening on ");
    result += QuickInfoHelper.toString(this, "outputString", (m_OutputString ? "string" : "byte[]"), ", outputting: ");
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");

    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return		the type of data
   */
  @Override
  public Class generates() {
    if (m_OutputString)
      return String.class;
    else
      return byte[].class;
  }

  /**
   * Performs the actual reception of data.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;
    RunnableWithLogging	run;

    result = null;

    if (m_Server == null) {
      try {
	m_Server = new ServerSocket(m_Port);
	m_Server.setSoTimeout(m_Timeout);
	PortManager.getSingleton().bind(this, m_Port);
      }
      catch (Exception e) {
	result   = handleException("Failed to listen on port: " + m_Port, e);
	m_Server = null;
      }
    }

    if (m_Server != null) {
      run = new RunnableWithLogging() {
	@Override
	protected void doRun() {
	  while ((m_Server != null) && !isStopped() && !m_Server.isClosed()) {
	    try {
	      m_Client = m_Server.accept();
	      InputStream in = m_Client.getInputStream();
	      TByteList bytes = new TByteArrayList();
	      int b;
	      while (!isStopped() && ((b = in.read()) != -1))
		bytes.add((byte) b);
	      if (m_Client != null)
		m_Client.close();
	      m_Client = null;
	      if (!isStopped()) {
		if (m_OutputString)
		  bufferData(new String(bytes.toArray(), m_Encoding.charsetValue()));
		else
		  bufferData(bytes.toArray());
	      }
	    }
	    catch (SocketTimeoutException stoe) {
	      // ignored
	    }
	    catch (SocketException se) {
	      if (!isStopped())
		handleException("Failed to accept connection!", se);
	    }
	    catch (Exception e) {
	      handleException("Failed to accept connection!", e);
	    }
	  }
	}
      };
      new Thread(run).start();
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Server != null) {
      try {
	m_Server.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Server = null;
    }
    if (m_Client != null) {
      try {
	m_Client.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Client = null;
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Server != null) {
      try {
	if (!m_Server.isClosed())
	  m_Server.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Server = null;
    }

    super.cleanUp();
  }
}
