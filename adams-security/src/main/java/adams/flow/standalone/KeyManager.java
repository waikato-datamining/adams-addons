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
 * KeyManager.java
 * Copyright (C) 2019-2023 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ActorUtils;
import adams.flow.core.KeyManagerFactoryProvider;
import adams.flow.core.OptionalPasswordPrompt;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Initializes a KeyManagerFactory instance using the specified keystore file and algorithm.<br>
 * For keystore types, please refer to:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;specs&#47;security&#47;standard-names.html#keystore-types
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: KeyManager
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-keystore-type &lt;java.lang.String&gt; (property: keystoreType)
 * &nbsp;&nbsp;&nbsp;The type of the keystore, eg PKCS12 or JKS.
 * &nbsp;&nbsp;&nbsp;default: PKCS12
 * </pre>
 *
 * <pre>-keystore-file &lt;adams.core.io.PlaceholderFile&gt; (property: keystoreFile)
 * &nbsp;&nbsp;&nbsp;The location of the keystore.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-keystore-passphrase &lt;adams.core.base.BasePassword&gt; (property: keystorePassphrase)
 * &nbsp;&nbsp;&nbsp;The passphrase for the keystore file, ignored if empty.
 * </pre>
 *
 * <pre>-prompt-for-password &lt;boolean&gt; (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled, the user gets prompted for enter a password if none has been
 * &nbsp;&nbsp;&nbsp;provided in the setup.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-algorithm &lt;java.lang.String&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: SunX509
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class KeyManager
  extends AbstractStandalone
  implements OptionalPasswordPrompt, KeyManagerFactoryProvider {

  private static final long serialVersionUID = 3990761211470952210L;

  /** the keystore type. */
  protected String m_KeystoreType;

  /** the location of the keystore. */
  protected PlaceholderFile m_KeystoreFile;

  /** the passphrase for the keystore file. */
  protected BasePassword m_KeystorePassphrase;

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

  /** the algorithm to use. */
  protected String m_Algorithm;

  /** the key manager factory in use. */
  protected transient KeyManagerFactory m_KeyManagerFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Initializes a KeyManagerFactory instance using the specified keystore file and algorithm.\n"
      + "For keystore types, please refer to:\n"
      + "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keystore-types";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "keystore-type", "keystoreType",
      "PKCS12");

    m_OptionManager.add(
      "keystore-file", "keystoreFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "keystore-passphrase", "keystorePassphrase",
      new BasePassword("")).dontOutputDefaultValue();

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
      "algorithm", "algorithm",
      "SunX509");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_KeyManagerFactory = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    result = QuickInfoHelper.toString(this, "keystoreType", m_KeystoreType, "type: ");
    result += QuickInfoHelper.toString(this, "keystoreFile", m_KeystoreFile, ", file: ");
    options = new ArrayList<>();
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    result += QuickInfoHelper.flatten(options);
    result += QuickInfoHelper.toString(this, "algorithm", m_Algorithm, ", algorithm: ");

    return result;
  }

  /**
   * Sets the type of the keystore.
   *
   * @param value	the type
   */
  public void setKeystoreType(String value) {
    m_KeystoreType = value;
    reset();
  }

  /**
   * Returns the type of the keystore.
   *
   * @return		the type
   */
  public String getKeystoreType() {
    return m_KeystoreType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keystoreTypeTipText() {
    return "The type of the keystore, eg PKCS12 or JKS.";
  }

  /**
   * Sets the location of the keystore file.
   *
   * @param value	the key file
   */
  public void setKeystoreFile(PlaceholderFile value) {
    m_KeystoreFile = value;
    reset();
  }

  /**
   * Returns the location of the keystore file.
   *
   * @return		the key file
   */
  public PlaceholderFile getKeystoreFile() {
    return m_KeystoreFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keystoreFileTipText() {
    return "The location of the keystore.";
  }

  /**
   * Sets the passphrase for the keystore file, ignored if empty.
   *
   * @param value	the passphrase
   */
  public void setKeystorePassphrase(BasePassword value) {
    m_KeystorePassphrase = value;
    reset();
  }

  /**
   * Returns the passphrase for the keystore file, ignored if empty.
   *
   * @return		the passphrase
   */
  public BasePassword getKeystorePassphrase() {
    return m_KeystorePassphrase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keystorePassphraseTipText() {
    return "The passphrase for the keystore file, ignored if empty.";
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
  @Override
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  @Override
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stopModeTipText() {
    return "The stop mode to use.";
  }

  /**
   * Sets the algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(String value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use.
   *
   * @return		the algorithm
   */
  public String getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use.";
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteract() {
    m_ActualPassphrase = ActorUtils.promptPassword(this, "Keystore passphrase");
    if (m_ActualPassphrase == null)
      return INTERACTION_CANCELED;
    else
      return null;
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
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteractHeadless() {
    String		result;
    BasePassword	password;

    result   = INTERACTION_CANCELED;
    password = ConsoleHelper.enterPassword("Please enter keystore passphrase (" + getName() + "):");
    if (password != null) {
      result             = null;
      m_ActualPassphrase = password;
    }

    return result;
  }

  /**
   * Returns the KeyManagerFactory instance.
   *
   * @return		the instance, null if none available
   */
  @Override
  public KeyManagerFactory getKeyManagerFactory() {
    return m_KeyManagerFactory;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    KeyStore 		keystore;
    FileInputStream	fis;
    String		msg;

    result = null;

    if (m_KeyManagerFactory == null) {
      m_ActualPassphrase = m_KeystorePassphrase;

      // prompt?
      if (m_PromptForPassword && (m_KeystorePassphrase.getValue().length() == 0)) {
	if (!isHeadless()) {
	  msg = doInteract();
	  if (msg != null) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	      else
		StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	      result = getStopMessage();
	    }
	  }
	}
	else if (supportsHeadlessInteraction()) {
	  msg = doInteractHeadless();
	  if (msg != null) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	      else
		StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	      result = getStopMessage();
	    }
	  }
	}
      }

      if (result == null) {
	fis = null;
	try {
	  keystore = KeyStore.getInstance(m_KeystoreType);
	  fis      = new FileInputStream(m_KeystoreFile.getAbsolutePath());
	  keystore.load(fis, m_ActualPassphrase.getValue().toCharArray());

	  m_KeyManagerFactory = KeyManagerFactory.getInstance(m_Algorithm);
	  m_KeyManagerFactory.init(keystore, m_ActualPassphrase.getValue().toCharArray());
	}
	catch (Exception e) {
	  result = handleException("Failed to initialize the KeyManagerFactory!", e);
	}
	finally {
	  FileUtils.closeQuietly(fis);
	}
      }
    }

    return result;
  }
}
