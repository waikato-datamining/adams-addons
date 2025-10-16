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
 * IMAP.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.input;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.source.imapsource.AbstractIMAPOperation;
import adams.flow.source.imapsource.ListFolders;
import adams.flow.standalone.IMAPConnection;
import jodd.mail.ReceivedEmail;

/**
 <!-- globalinfo-start -->
 * Executes the specified IMAP operation and forwards the generated output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-operation &lt;adams.flow.source.imapsource.AbstractIMAPOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The IMAP operation to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.imapsource.ListFolders
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class IMAP
  extends AbstractRatInput {

  private static final long serialVersionUID = -6305664069979677034L;

  /** the operation to execute. */
  protected AbstractIMAPOperation m_Operation;

  /** the IMAP connection. */
  protected transient IMAPConnection m_Connection;

  /** the messages that were received. */
  protected transient ReceivedEmail[] m_Data;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified IMAP operation and forwards the generated output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new ListFolders());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Connection = null;
  }

  /**
   * Sets the IMAP operation to perform.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractIMAPOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the IMAP operation to perform.
   *
   * @return 		the operation
   */
  public AbstractIMAPOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The IMAP operation to perform.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation, "operation: ");
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return the type of data
   */
  @Override
  public Class generates() {
    return m_Operation.generates();
  }

  /**
   * Performs the actual reception of data.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;
    MessageCollection errors;
    Object		output;

    result = null;
    m_Data = null;

    if (m_Connection == null) {
      m_Connection = (IMAPConnection) ActorUtils.findClosestType(m_Owner, IMAPConnection.class, true);
      if (m_Connection == null)
	result = "Failed to locate an instance of: " + Utils.classToString(IMAPConnection.class);
    }

    if (result == null) {
      try {
	errors = new MessageCollection();
	output = m_Operation.execute(m_Connection, errors);
	if (!errors.isEmpty())
	  result = errors.toString();
	else if (output != null)
	  m_Data = (ReceivedEmail[]) output;
      }
      catch (Exception e) {
	result = handleException("Failed to execute IMAP operation: " + m_Operation, e);
      }
    }

    return result;
  }

  /**
   * Checks whether any output can be collected.
   *
   * @return true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Data != null);
  }

  /**
   * Returns the received data.
   *
   * @return the data
   */
  @Override
  public Object output() {
    Object	result;

    result = m_Data;
    m_Data = null;

    return result;
  }
}
