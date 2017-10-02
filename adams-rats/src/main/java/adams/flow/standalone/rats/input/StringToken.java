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
 * StringToken.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

/**
 <!-- globalinfo-start -->
 * Outputs the specified token string.<br>
 * Only to be used in conjunction with Rats that operate in manual mode.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-token &lt;java.lang.String&gt; (property: token)
 * &nbsp;&nbsp;&nbsp;The string token to output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToken
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = -4640490350537786881L;

  /** the string to forward. */
  protected String m_Token;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs the specified token string.\n"
	+ "Only to be used in conjunction with Rats that operate in manual mode.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "token", "token",
      "");
  }

  /**
   * Sets the string token to output.
   *
   * @param value	the token
   */
  public void setToken(String value) {
    m_Token = value;
    reset();
  }

  /**
   * Returns the string token to output.
   *
   * @return		the token
   */
  public String getToken() {
    return m_Token;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tokenTipText() {
    return "The string token to output.";
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		always true
   */
  @Override
  public boolean hasPendingOutput() {
    return true;
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return m_Token;
  }

  /**
   * Performs the actual reception of data.
   * <br><br>
   * Does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    return null;
  }
}
