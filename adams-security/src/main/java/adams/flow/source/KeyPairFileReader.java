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
 * KeyPairFileReader.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 <!-- globalinfo-start -->
 * Reads private and public keys in DER format from the specified files and forwards them as pair.<br>
 * For algorithm types, see:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;specs&#47;security&#47;standard-names.html#keygenerator-algorithms
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.security.KeyPair<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: KeyPairFileReader
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
 * <pre>-type &lt;java.lang.String&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of the key pair algorithm, eg RSA.
 * &nbsp;&nbsp;&nbsp;default: RSA
 * </pre>
 *
 * <pre>-private-key &lt;adams.core.io.PlaceholderFile&gt; (property: privateKey)
 * &nbsp;&nbsp;&nbsp;The file containing the private key.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-public-key &lt;adams.core.io.PlaceholderFile&gt; (property: publicKey)
 * &nbsp;&nbsp;&nbsp;The file containing the public key.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class KeyPairFileReader
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -2399319754051534729L;

  /** the algorithm. */
  protected String m_Type;

  /** the private key file. */
  protected PlaceholderFile m_PrivateKey;

  /** the public key file. */
  protected PlaceholderFile m_PublicKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads private and public keys in DER format from the specified "
      + "files and forwards them as pair.\n"
      + "For algorithm types, see:\n"
      + "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keygenerator-algorithms";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      "RSA");

    m_OptionManager.add(
      "private-key", "privateKey",
      new PlaceholderFile());

    m_OptionManager.add(
      "public-key", "publicKey",
      new PlaceholderFile());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "privateKey", m_PrivateKey, ", private: ");
    result += QuickInfoHelper.toString(this, "publicKey", m_PublicKey, ", public: ");

    return result;
  }

  /**
   * Sets the type of the key pair algorithm to use.
   *
   * @param value	the type
   */
  public void setType(String value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the algorithm to use.
   *
   * @return		the type
   */
  public String getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the key pair algorithm, eg RSA.";
  }

  /**
   * Sets the private key file to read.
   *
   * @param value	the file
   */
  public void setPrivateKey(PlaceholderFile value) {
    m_PrivateKey = value;
    reset();
  }

  /**
   * Returns the private key file to read.
   *
   * @return		the file
   */
  public PlaceholderFile getPrivateKey() {
    return m_PrivateKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String privateKeyTipText() {
    return "The file containing the private key.";
  }

  /**
   * Sets the public key file to read.
   *
   * @param value	the file
   */
  public void setPublicKey(PlaceholderFile value) {
    m_PublicKey = value;
    reset();
  }

  /**
   * Returns the public key file to read.
   *
   * @return		the file
   */
  public PlaceholderFile getPublicKey() {
    return m_PublicKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String publicKeyTipText() {
    return "The file containing the public key.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{java.security.KeyPair.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    byte[]		privateKey;
    byte[]		publicKey;
    KeyFactory 		keyFactory;
    X509EncodedKeySpec 	pubSpec;
    PublicKey 		pubKey;
    PKCS8EncodedKeySpec privSpec;
    PrivateKey 		privKey;

    result = null;

    privateKey = null;
    try {
      privateKey = Files.readAllBytes(m_PrivateKey.getAbsoluteFile().toPath());
    }
    catch (Exception e) {
      result = handleException("Failed to read private key: " + m_PrivateKey, e);
    }
    
    publicKey = null;
    if (result == null) {
      try {
	publicKey = Files.readAllBytes(m_PublicKey.getAbsoluteFile().toPath());
      }
      catch (Exception e) {
	result = handleException("Failed to read public key: " + m_PublicKey, e);
      }
    }

    if (result == null) {
      try {
	keyFactory    = KeyFactory.getInstance(m_Type);
	pubSpec       = new X509EncodedKeySpec(publicKey);
	pubKey        = keyFactory.generatePublic(pubSpec);
	privSpec      = new PKCS8EncodedKeySpec(privateKey);
        privKey       = keyFactory.generatePrivate(privSpec);
        m_OutputToken = new Token(new java.security.KeyPair(pubKey, privKey));
      }
      catch (Exception e) {
	result = handleException("Failed to decode keys as " + m_Type, e);
      }
    }

    return result;
  }
}
