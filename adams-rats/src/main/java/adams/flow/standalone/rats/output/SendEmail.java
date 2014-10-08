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
 * SendEmail.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.net.AbstractSendEmail;
import adams.core.net.EmailHelper;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SMTPConnection;

/**
 <!-- globalinfo-start -->
 * Transmitter for sending emails.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-send-email &lt;adams.core.net.AbstractSendEmail&gt; (property: sendEmail)
 * &nbsp;&nbsp;&nbsp;The engine for sending the emails.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.JavaMailSendEmail
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendEmail
  extends AbstractRatOutput {
  
  /** for serialization. */
  private static final long serialVersionUID = 8536200128302047375L;
  
  /** for sending the emails. */
  protected AbstractSendEmail m_SendEmail;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Transmitter for sending emails.\n"
      + (EmailHelper.isEnabled() ? "" : "Email support not enabled, check email setup!");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "send-email", "sendEmail",
	    EmailHelper.getDefaultSendEmail());
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (!EmailHelper.isEnabled())
      return "No email support enabled, check email setup!";

    result = QuickInfoHelper.toString(this, "sendEmail", m_SendEmail.getClass(), "send: ");;
    
    return result;
  }

  /**
   * Sets the object for sending emails.
   *
   * @param value	the object
   */
  public void setSendEmail(AbstractSendEmail value) {
    m_SendEmail = value;
    reset();
  }

  /**
   * Returns the object for sending emails.
   *
   * @return 		the object
   */
  public AbstractSendEmail getSendEmail() {
    return m_SendEmail;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String sendEmailTipText() {
    return "The engine for sending the emails.";
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{adams.core.net.Email.class};
  }

  /**
   * Hook method for performing checks.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String 	result;
    
    result = super.check();

    if (result == null) {
      if (!EmailHelper.isEnabled())
	result = "No email support enabled, check email setup!";
    }
    
    return result;
  }

  /**
   * Initializes the SMTP session if required.
   * 
   * @throws Exception		if initialization fails
   */
  protected void initSession() throws Exception {
    SMTPConnection	conn;
    
    if (m_SendEmail.requiresSmtpSessionInitialization()) {
      conn = (SMTPConnection) ActorUtils.findClosestType(getOwner(), SMTPConnection.class, true);
      if (conn != null)
	conn.initializeSmtpSession(m_SendEmail);
      else
	m_SendEmail.initializeSmtpSession(
	    EmailHelper.getSmtpServer(), 
	    EmailHelper.getSmtpPort(), 
	    EmailHelper.getSmtpStartTLS(), 
	    EmailHelper.getSmtpUseSSL(), 
	    EmailHelper.getSmtpTimeout(), 
	    EmailHelper.getSmtpRequiresAuthentication(), 
	    EmailHelper.getSmtpUser(), 
	    EmailHelper.getSmtpPassword());
    }
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String			result;
    adams.core.net.Email	email;
    
    result = null;
    email  = (adams.core.net.Email) m_Input;

    try {
      initSession();
      if (!m_SendEmail.sendMail(email))
	result = "Failed to send email, check console output!";
    }
    catch (Exception e) {
      result = handleException("Failed to send email: ", e);
    }
    
    return result;
  }
}
