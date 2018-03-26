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

package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.base.BaseHostname;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Just outputs the data to the specified socket.<br>
 * Any incoming data that isn't a byte array gets converted to a string and its bytes (using the specified encoding) are then transmitted.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-address &lt;adams.core.base.BaseHostname&gt; (property: address)
 * &nbsp;&nbsp;&nbsp;The address to connect to.
 * &nbsp;&nbsp;&nbsp;default: 127.0.0.1:8000
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
  extends AbstractRatOutput {

  private static final long serialVersionUID = -3054206514339300174L;

  /** the address to open. */
  protected BaseHostname m_Address;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to close the socket after sending data. */
  protected boolean m_CloseAfterSend;

  /** the socket to use. */
  protected transient java.net.Socket m_Socket;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Just outputs the data to the specified socket.\n"
      + "Any incoming data that isn't a byte array gets converted to a string "
      + "and its bytes (using the specified encoding) are then transmitted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "address", "address",
      new BaseHostname("127.0.0.1:8000"));

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "close-after-send", "closeAfterSend",
      true);
  }

  /**
   * Sets the address.
   *
   * @param value 	the address
   */
  public void setAddress(BaseHostname value) {
    m_Address = value;
    reset();
  }

  /**
   * Returns the address.
   *
   * @return 		the address
   */
  public BaseHostname getAddress() {
    return m_Address;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addressTipText() {
    return "The address to connect to.";
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
   * Sets whether to close the socket after sending the data.
   *
   * @param value	true if to close socket
   */
  public void setCloseAfterSend(boolean value) {
    m_CloseAfterSend = value;
    reset();
  }

  /**
   * Returns whether to close the socket after sending the data.
   *
   * @return		true if to close socket
   */
  public boolean getCloseAfterSend() {
    return m_CloseAfterSend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String closeAfterSendTipText() {
    return "If enabled, the socket will get closed after sending the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "address", m_Address);
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");
    result += QuickInfoHelper.toString(this, "closeAfterSend", (m_CloseAfterSend ? "close after send" : "keep open"), ", ");

    return result;
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class, byte[].class, Byte[].class};
  }

  /**
   * Performs the actual transmission.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    byte[]	bytes;

    result = null;

    if (m_Socket == null) {
      try {
	m_Socket = new java.net.Socket(m_Address.hostnameValue(), m_Address.portValue());
      }
      catch (Exception e) {
	result   = handleException("Failed to open socket to: " + m_Address, e);
	m_Socket = null;
      }
    }

    if (m_Socket != null) {
      try {
	if (m_Input instanceof byte[])
	  bytes = (byte[]) m_Input;
	else if (m_Input instanceof Byte[])
	  bytes = StatUtils.toByteArray((Byte[]) m_Input);
	else
	  bytes = ("" + m_Input).getBytes(m_Encoding.charsetValue());
	m_Socket.getOutputStream().write(bytes);
	m_Socket.getOutputStream().flush();
	if (m_CloseAfterSend) {
	  m_Socket.close();
	  m_Socket = null;
	}
      }
      catch (Exception e) {
	result = handleException("Failed to send data!", e);
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Socket != null) {
      try {
	m_Socket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Socket = null;
    }
    super.cleanUp();
  }
}
