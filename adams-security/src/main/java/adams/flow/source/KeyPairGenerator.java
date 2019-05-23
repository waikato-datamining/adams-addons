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
 * KeyPairGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

import java.security.SecureRandom;

/**
 <!-- globalinfo-start -->
 * Generates a private&#47;public key pair.<br>
 * For algorithm types, see:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;specs&#47;security&#47;standard-names.html#keygenerator-algorithms<br>
 * For PRNG types, see:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;specs&#47;security&#47;standard-names.html#securerandom-number-generation-algorithms
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
 * &nbsp;&nbsp;&nbsp;default: KeyPairGenerator
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
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of the key in bits.
 * &nbsp;&nbsp;&nbsp;default: 4096
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-prng &lt;java.lang.String&gt; (property: PRNG)
 * &nbsp;&nbsp;&nbsp;The pseudo random number generator to use, leave empty to let the system
 * &nbsp;&nbsp;&nbsp;choose a strong one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class KeyPairGenerator
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 262803436200996578L;

  /** the algorithm. */
  protected String m_Type;

  /** the key size. */
  protected int m_Size;

  /** the pseudo random number generator to use. */
  protected String m_PRNG;

  @Override
  public String globalInfo() {
    return "Generates a private/public key pair.\n"
      + "For algorithm types, see:\n"
      + "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keygenerator-algorithms\n"
      + "For PRNG types, see:\n"
      + "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#securerandom-number-generation-algorithms";
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
      "size", "size",
      4096, 1, null);

    m_OptionManager.add(
      "prng", "PRNG",
      "");
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
    result += QuickInfoHelper.toString(this, "size", m_Size, ", size: ");
    result += QuickInfoHelper.toString(this, "PRNG", (m_PRNG.isEmpty() ? "strong, determined by system" : m_PRNG), ", prng: ");

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
   * Sets the key size in bits.
   *
   * @param value	the size
   */
  public void setSize(int value) {
    m_Size = value;
    reset();
  }

  /**
   * Returns the key size in bits.
   *
   * @return		the size
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the key in bits.";
  }

  /**
   * Sets the pseudo random number generator to use.
   *
   * @param value	the generator
   */
  public void setPRNG(String value) {
    m_PRNG = value;
    reset();
  }

  /**
   * Returns the pseudo random number generator in use.
   *
   * @return		the generator
   */
  public String getPRNG() {
    return m_PRNG;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String PRNGTipText() {
    return "The pseudo random number generator to use, leave empty to let the system choose a strong one.";
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
    String				result;
    java.security.KeyPairGenerator 	generator;

    result = null;

    try {
      generator = java.security.KeyPairGenerator.getInstance(m_Type);
      generator.initialize(m_Size, (m_PRNG.isEmpty() ? SecureRandom.getInstanceStrong() : SecureRandom.getInstance(m_PRNG)));
      m_OutputToken = new Token(generator.generateKeyPair());
    }
    catch (Exception e) {
      result = handleException("Failed to generate key pair!", e);
    }

    return result;
  }
}
