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
 * SSLConnectionFactory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.connection;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.flow.control.Flow;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.dialog.PasswordDialog;
import com.rabbitmq.client.ConnectionFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * For encrypting a connection with SSL.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SSLConnectionFactory
  extends AbstractConnectionFactory {

  private static final long serialVersionUID = 7600989409175939L;

  /** the base connection factory. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /** the keystore type (KeyManager). */
  protected String m_KeyManagerKeystoreType;

  /** the location of the keystore (KeyManager). */
  protected PlaceholderFile m_KeyManagerKeystoreFile;

  /** the passphrase for the keystore file (KeyManager). */
  protected BasePassword m_KeyManagerKeystorePassphrase;

  /** the algorithm to use (KeyManager). */
  protected String m_KeyManagerAlgorithm;

  /** the key manager factory in use. */
  protected transient KeyManagerFactory m_KeyManagerFactory;

  /** the keystore type (TrustManager). */
  protected String m_TrustManagerKeystoreType;

  /** the location of the keystore (TrustManager). */
  protected PlaceholderFile m_TrustManagerKeystoreFile;

  /** the passphrase for the keystore file (TrustManager). */
  protected BasePassword m_TrustManagerKeystorePassphrase;

  /** the algorithm to use (TrustManager). */
  protected String m_TrustManagerAlgorithm;

  /** the trust manager factory in use. */
  protected transient TrustManagerFactory m_TrustManagerFactory;

  /** the actual password to use. */
  protected BasePassword m_ActualPassphrase;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the protocol to use. */
  protected String m_Protocol;

  /** the SSL context. */
  protected transient javax.net.ssl.SSLContext m_SSLContext;

  /** whether to use hostname verification (if SSL on). */
  protected boolean m_HostnameVerification;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For encrypting a connection with SSL.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection-factory", "connectionFactory",
      new GuestConnectionFactory());

    m_OptionManager.add(
      "keymanager-keystore-type", "keyManagerKeystoreType",
      "PKCS12");

    m_OptionManager.add(
      "keymanager-keystore-file", "keyManagerKeystoreFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "keymanager-keystore-passphrase", "keyManagerKeystorePassphrase",
      new BasePassword(""), false);

    m_OptionManager.add(
      "keymanager-algorithm", "keyManagerAlgorithm",
      "SunX509");

    m_OptionManager.add(
      "trustmanager-keystore-type", "trustManagerKeystoreType",
      "JKS");

    m_OptionManager.add(
      "trustmanager-keystore-file", "trustManagerKeystoreFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "trustmanager-keystore-passphrase", "trustManagerKeystorePassphrase",
      new BasePassword(""), false);

    m_OptionManager.add(
      "trustmanager-algorithm", "trustManagerAlgorithm",
      "SunX509");

    m_OptionManager.add(
      "prompt-for-password", "promptForPassword",
      false);

    m_OptionManager.add(
      "stop-if-canceled", "stopFlowIfCanceled",
      false);

    m_OptionManager.add(
      "custom-stop-message", "customStopMessage",
      "");

    m_OptionManager.add(
      "stop-mode", "stopMode",
      StopMode.GLOBAL);

    m_OptionManager.add(
      "protocol", "protocol",
      "TLSv1.2");

    m_OptionManager.add(
      "hostname-verification", "hostnameVerification",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_KeyManagerFactory   = null;
    m_TrustManagerFactory = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> options;

    result = QuickInfoHelper.toString(this, "connectionFactory", m_ConnectionFactory, "connection: ");
    result += QuickInfoHelper.toString(this, "keyManagerKeystoreType", m_KeyManagerKeystoreType, ", KM type: ");
    result += QuickInfoHelper.toString(this, "keyManagerKeystoreFile", m_KeyManagerKeystoreFile, ", KM file: ");
    result += QuickInfoHelper.toString(this, "keyManagerAlgorithm", m_KeyManagerAlgorithm, ", KM algorithm: ");
    result += QuickInfoHelper.toString(this, "trustManagerKeystoreType", m_TrustManagerKeystoreType, ", TM type: ");
    result += QuickInfoHelper.toString(this, "trustManagerKeystoreFile", m_TrustManagerKeystoreFile, ", TM file: ");
    result += QuickInfoHelper.toString(this, "trustManagerAlgorithm", m_TrustManagerAlgorithm, ", TM algorithm: ");
    result += QuickInfoHelper.toString(this, "protocol", m_Protocol, ", protocol: ");
    options = new ArrayList<>();
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "hostnameVerification", m_HostnameVerification, "hostname verification"));
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the base connection factory to encrypt.
   *
   * @param value	the factory
   */
  public void setConnectionFactory(AbstractConnectionFactory value) {
    m_ConnectionFactory = value;
    reset();
  }

  /**
   * Returns the base connection factory to encrypt.
   *
   * @return		the factory
   */
  public AbstractConnectionFactory getConnectionFactory() {
    return m_ConnectionFactory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionFactoryTipText() {
    return "The base connection factory to encrypt.";
  }

  /**
   * Sets the type of the keystore (KeyManager).
   *
   * @param value	the type
   */
  public void setKeyManagerKeystoreType(String value) {
    m_KeyManagerKeystoreType = value;
    reset();
  }

  /**
   * Returns the type of the keystore (KeyManager).
   *
   * @return		the type
   */
  public String getKeyManagerKeystoreType() {
    return m_KeyManagerKeystoreType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyManagerKeystoreTypeTipText() {
    return "The type of the keystore, eg PKCS12 or JKS (KeyManager).";
  }

  /**
   * Sets the location of the keystore file (KeyManager).
   *
   * @param value	the key file
   */
  public void setKeyManagerKeystoreFile(PlaceholderFile value) {
    m_KeyManagerKeystoreFile = value;
    reset();
  }

  /**
   * Returns the location of the keystore file (KeyManager).
   *
   * @return		the key file
   */
  public PlaceholderFile getKeyManagerKeystoreFile() {
    return m_KeyManagerKeystoreFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyManagerKeystoreFileTipText() {
    return "The location of the keystore (KeyManager).";
  }

  /**
   * Sets the passphrase for the keystore file, ignored if empty (KeyManager).
   *
   * @param value	the passphrase
   */
  public void setKeyManagerKeystorePassphrase(BasePassword value) {
    m_KeyManagerKeystorePassphrase = value;
    reset();
  }

  /**
   * Returns the passphrase for the keystore file, ignored if empty (KeyManager).
   *
   * @return		the passphrase
   */
  public BasePassword getKeyManagerKeystorePassphrase() {
    return m_KeyManagerKeystorePassphrase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyManagerKeystorePassphraseTipText() {
    return "The passphrase for the keystore file, ignored if empty (KeyManager).";
  }

  /**
   * Sets the algorithm to use (KeyManager).
   *
   * @param value	the algorithm
   */
  public void setKeyManagerAlgorithm(String value) {
    m_KeyManagerAlgorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use (KeyManager).
   *
   * @return		the algorithm
   */
  public String getKeyManagerAlgorithm() {
    return m_KeyManagerAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyManagerAlgorithmTipText() {
    return "The algorithm to use (KeyManager).";
  }

  /**
   * Sets the type of the keystore (TrustManager).
   *
   * @param value	the type
   */
  public void setTrustManagerKeystoreType(String value) {
    m_TrustManagerKeystoreType = value;
    reset();
  }

  /**
   * Returns the type of the keystore (TrustManager).
   *
   * @return		the type
   */
  public String getTrustManagerKeystoreType() {
    return m_TrustManagerKeystoreType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trustManagerKeystoreTypeTipText() {
    return "The type of the keystore, eg PKCS12 or JKS (TrustManager).";
  }

  /**
   * Sets the location of the keystore file (TrustManager).
   *
   * @param value	the key file
   */
  public void setTrustManagerKeystoreFile(PlaceholderFile value) {
    m_TrustManagerKeystoreFile = value;
    reset();
  }

  /**
   * Returns the location of the keystore file (TrustManager).
   *
   * @return		the key file
   */
  public PlaceholderFile getTrustManagerKeystoreFile() {
    return m_TrustManagerKeystoreFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trustManagerKeystoreFileTipText() {
    return "The location of the keystore (TrustManager).";
  }

  /**
   * Sets the passphrase for the keystore file, ignored if empty (TrustManager).
   *
   * @param value	the passphrase
   */
  public void setTrustManagerKeystorePassphrase(BasePassword value) {
    m_TrustManagerKeystorePassphrase = value;
    reset();
  }

  /**
   * Returns the passphrase for the keystore file, ignored if empty (TrustManager).
   *
   * @return		the passphrase
   */
  public BasePassword getTrustManagerKeystorePassphrase() {
    return m_TrustManagerKeystorePassphrase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trustManagerKeystorePassphraseTipText() {
    return "The passphrase for the keystore file, ignored if empty (TrustManager).";
  }

  /**
   * Sets the algorithm to use (TrustManager).
   *
   * @param value	the algorithm
   */
  public void setTrustManagerAlgorithm(String value) {
    m_TrustManagerAlgorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use (TrustManager).
   *
   * @return		the algorithm
   */
  public String getTrustManagerAlgorithm() {
    return m_TrustManagerAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trustManagerAlgorithmTipText() {
    return "The algorithm to use (TrustManager).";
  }

  /**
   * Sets whether to prompt for a password if none currently provided.
   *
   * @param value	true if to prompt for a password
   */
  public void setPromptForPassword(boolean value) {
    m_PromptForPassword = value;
    reset();
  }

  /**
   * Returns whether to prompt for a password if none currently provided.
   *
   * @return		true if to prompt for a password
   */
  public boolean getPromptForPassword() {
    return m_PromptForPassword;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String promptForPasswordTipText() {
    return
      "If enabled, the user gets prompted "
        + "for enter a password if none has been provided in the setup.";
  }

  /**
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String customStopMessageTipText() {
    return
      "The custom stop message to use in case a user cancelation stops the "
        + "flow (default is the full name of the actor)";
  }

  /**
   * Sets the stop mode.
   *
   * @param value	the mode
   */
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stopModeTipText() {
    return "The stop mode to use.";
  }

  /**
   * Sets the protocol to use.
   *
   * @param value	the protocol
   */
  public void setProtocol(String value) {
    m_Protocol = value;
    reset();
  }

  /**
   * Returns the protocol to use.
   *
   * @return		the protocol
   */
  public String getProtocol() {
    return m_Protocol;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String protocolTipText() {
    return "The protocol to use, eg TLSv1, TLSv1.1 or TLSv1.2.";
  }

  /**
   * Sets whether to perform hostname verification.
   *
   * @param value	true if to verify
   */
  public void setHostnameVerification(boolean value) {
    m_HostnameVerification = value;
    reset();
  }

  /**
   * Returns whether to perform hostname verification.
   *
   * @return		true if to verify
   */
  public boolean getHostnameVerification() {
    return m_HostnameVerification;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostnameVerificationTipText() {
    return "If enabled, hostnames get verified.";
  }

  /**
   * Returns whether a flow context is required.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return m_PromptForPassword || m_ConnectionFactory.requiresFlowContext();
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteract() {
    boolean		result;
    PasswordDialog	dlg;

    dlg = new PasswordDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dlg.setLabelPassword("Keystore passphrase");
    dlg.setLocationRelativeTo(m_FlowContext.getParentComponent());
    ((Flow) m_FlowContext.getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) m_FlowContext.getRoot()).deregisterWindow(dlg);
    result = (dlg.getOption() == PasswordDialog.APPROVE_OPTION);

    if (result)
      m_ActualPassphrase = dlg.getPassword();

    return result;
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    boolean		result;
    BasePassword	password;

    result   = false;
    password = ConsoleHelper.enterPassword("Please enter keystore passphrase (" + m_FlowContext.getName() + "):");
    if (password != null) {
      result           = true;
      m_ActualPassphrase = password;
    }

    return result;
  }

  /**
   * Generates the connection factory object.
   *
   * @param errors	for collecting errors
   * @return		the factory, null in case of error
   */
  @Override
  protected ConnectionFactory doGenerate(MessageCollection errors) {
    ConnectionFactory	result;
    String		msg;
    KeyStore 		keystore;
    FileInputStream 	fis;

    msg    = null;
    result = m_ConnectionFactory.generate(errors);
    if (result == null)
      return null;

    // keymanager
    if (m_KeyManagerFactory == null) {
      m_ActualPassphrase = m_KeyManagerKeystorePassphrase;

      // prompt?
      if (m_PromptForPassword && (m_KeyManagerKeystorePassphrase.getValue().length() == 0)) {
	if (!getFlowContext().isHeadless()) {
	  if (!doInteract()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(getFlowContext(), m_StopMode, "Flow canceled: " + getFlowContext().getFullName());
	      else
		StopHelper.stop(getFlowContext(), m_StopMode, m_CustomStopMessage);
	      msg = getFlowContext().getStopMessage();
	    }
	  }
	}
	else if (supportsHeadlessInteraction()) {
	  if (!doInteractHeadless()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(getFlowContext(), m_StopMode, "Flow canceled: " + getFlowContext().getFullName());
	      else
		StopHelper.stop(getFlowContext(), m_StopMode, m_CustomStopMessage);
	      msg = getFlowContext().getStopMessage();
	    }
	  }
	}
      }

      if (msg == null) {
	fis = null;
	try {
	  keystore = KeyStore.getInstance(m_KeyManagerKeystoreType);
	  fis = new FileInputStream(m_KeyManagerKeystoreFile.getAbsolutePath());
	  keystore.load(fis, m_ActualPassphrase.getValue().toCharArray());

	  m_KeyManagerFactory = KeyManagerFactory.getInstance(m_KeyManagerAlgorithm);
	  m_KeyManagerFactory.init(keystore, m_ActualPassphrase.getValue().toCharArray());
	}
	catch (Exception e) {
	  msg = LoggingHelper.handleException(this, "Failed to initialize the KeyManagerFactory!", e);
	}
	finally {
	  FileUtils.closeQuietly(fis);
	}
      }
      if (msg != null)
	errors.add(msg);
      if (!errors.isEmpty())
	return null;
    }

    // trustmanager
    if (m_TrustManagerFactory == null) {
      m_ActualPassphrase = m_TrustManagerKeystorePassphrase;

      // prompt?
      if (m_PromptForPassword && (m_TrustManagerKeystorePassphrase.getValue().length() == 0)) {
	if (!getFlowContext().isHeadless()) {
	  if (!doInteract()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(getFlowContext(), m_StopMode, "Flow canceled: " + getFlowContext().getFullName());
	      else
		StopHelper.stop(getFlowContext(), m_StopMode, m_CustomStopMessage);
	      msg = getFlowContext().getStopMessage();
	    }
	  }
	}
	else if (supportsHeadlessInteraction()) {
	  if (!doInteractHeadless()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(getFlowContext(), m_StopMode, "Flow canceled: " + getFlowContext().getFullName());
	      else
		StopHelper.stop(getFlowContext(), m_StopMode, m_CustomStopMessage);
	      msg = getFlowContext().getStopMessage();
	    }
	  }
	}
      }

      if (msg == null) {
	fis = null;
	try {
	  keystore = KeyStore.getInstance(m_TrustManagerKeystoreType);
	  fis = new FileInputStream(m_TrustManagerKeystoreFile.getAbsolutePath());
	  keystore.load(fis, m_ActualPassphrase.getValue().toCharArray());

	  m_TrustManagerFactory = TrustManagerFactory.getInstance(m_TrustManagerAlgorithm);
	  m_TrustManagerFactory.init(keystore);
	}
	catch (Exception e) {
	  msg = LoggingHelper.handleException(this, "Failed to initialize the TrustManagerFactory!", e);
	}
	finally {
	  FileUtils.closeQuietly(fis);
	}
      }
    }
    if (msg != null)
      errors.add(msg);
    if (!errors.isEmpty())
      return null;

    // SSL context
    m_SSLContext = null;
    try {
      m_SSLContext = javax.net.ssl.SSLContext.getInstance(m_Protocol);
      m_SSLContext.init(
        m_KeyManagerFactory.getKeyManagers(),
	m_TrustManagerFactory.getTrustManagers(), null);
      result.useSslProtocol(m_SSLContext);
      if (m_HostnameVerification)
	result.enableHostnameVerification();
    }
    catch (Exception e) {
      msg = LoggingHelper.handleException(this, "Failed to instantiate SSL context!", e);
    }
    if (msg != null)
      errors.add(msg);
    if (!errors.isEmpty())
      return null;

    return result;
  }
}
