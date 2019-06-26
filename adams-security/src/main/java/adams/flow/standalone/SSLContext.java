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
 * SSLContext.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.core.KeyManagerFactoryProvider;
import adams.flow.core.SSLContextProvider;
import adams.flow.core.TrustManagerFactoryProvider;

/**
 <!-- globalinfo-start -->
 * Initializes an SSL context using the specified context.<br>
 * For protocols, see:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;specs&#47;security&#47;standard-names.html#sslcontext-algorithms<br>
 * Requires adams.flow.core.KeyManagerFactoryProvider and adams.flow.core.TrustManagerFactoryProvider standalones to be present.<br>
 * You don't have to use these actors if you enable the 'useDefaultContext' option.
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
 * &nbsp;&nbsp;&nbsp;default: SSLContext
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
 * <pre>-protocol &lt;java.lang.String&gt; (property: protocol)
 * &nbsp;&nbsp;&nbsp;The protocol to use, eg TLSv1, TLSv1.1 or TLSv1.2.
 * &nbsp;&nbsp;&nbsp;default: TLSv1.2
 * </pre>
 *
 * <pre>-use-default-context &lt;boolean&gt; (property: useDefaultContext)
 * &nbsp;&nbsp;&nbsp;If enabled, the default SSL context is used (doesn't require the keymanager
 * &nbsp;&nbsp;&nbsp;&#47;trustmanager).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SSLContext
  extends AbstractStandalone
  implements SSLContextProvider {

  private static final long serialVersionUID = 3010371119440218991L;

  /** the protocol to use. */
  protected String m_Protocol;

  /** whether to use default context. */
  protected boolean m_UseDefaultContext;

  /** the KeyManager instance to use. */
  protected transient KeyManagerFactoryProvider m_KeyManager;

  /** the TrustManager instance to use. */
  protected transient TrustManagerFactoryProvider m_TrustManager;

  /** the SSL context. */
  protected transient javax.net.ssl.SSLContext m_SSLContext;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Initializes an SSL context using the specified context.\n"
      + "For protocols, see:\n"
      + "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#sslcontext-algorithms\n"
      + "Requires " + Utils.classToString(KeyManagerFactoryProvider.class) + " and "
      + Utils.classToString(TrustManagerFactoryProvider.class) + " standalones to be present.\n"
      + "You don't have to use these actors if you enable the 'useDefaultContext' option.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "protocol", "protocol",
      "TLSv1.2");

    m_OptionManager.add(
      "use-default-context", "useDefaultContext",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  	result;

    if (m_UseDefaultContext)
      result = "default context";
    else
      result = QuickInfoHelper.toString(this, "protocol", m_Protocol);

    return result;
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
   * Sets whether to use the default SSL context.
   *
   * @param value	true if to use default context
   */
  public void setUseDefaultContext(boolean value) {
    m_UseDefaultContext = value;
    reset();
  }

  /**
   * Returns whether to use the default SSL context.
   *
   * @return		true if to use default context
   */
  public boolean getUseDefaultContext() {
    return m_UseDefaultContext;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useDefaultContextTipText() {
    return "If enabled, the default SSL context is used (doesn't require the keymanager/trustmanager).";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (!m_UseDefaultContext) {
      if (result == null) {
	m_KeyManager = (KeyManagerFactoryProvider) ActorUtils.findClosestType(this, KeyManagerFactoryProvider.class, true);
	if (m_KeyManager == null)
	  result = "Failed to locate " + Utils.classToString(KeyManagerFactoryProvider.class) + " actor!";
      }

      if (result == null) {
	m_TrustManager = (TrustManagerFactoryProvider) ActorUtils.findClosestType(this, TrustManagerFactoryProvider.class, true);
	if (m_TrustManager == null)
	  result = "Failed to locate " + Utils.classToString(TrustManagerFactoryProvider.class) + " actor!";
      }
    }

    return result;
  }

  /**
   * Returns the SSLContext instance.
   *
   * @return		the instance, null if not available
   */
  @Override
  public javax.net.ssl.SSLContext getSSLContext() {
    return m_SSLContext;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    m_SSLContext = null;
    try {
      if (m_UseDefaultContext) {
        getLogger().warning("Using default SSL context!");
        m_SSLContext = javax.net.ssl.SSLContext.getDefault();
      }
      else {
	m_SSLContext = javax.net.ssl.SSLContext.getInstance(m_Protocol);
	m_SSLContext.init(
	  m_KeyManager.getKeyManagerFactory().getKeyManagers(),
	  m_TrustManager.getTrustManagerFactory().getTrustManagers(), null);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to instantiate SSL context!", e);
    }

    return result;
  }
}
