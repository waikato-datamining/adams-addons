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
 * EditDistance.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Computes the edit distance between the supplied base string and the one passing through, outputting the distance.<br>
 * If a string array of length two is passing through, the 1st element is considered the base string instead and the distance computed to the 2nd element.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: EditDistance
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-base &lt;java.lang.String&gt; (property: base)
 * &nbsp;&nbsp;&nbsp;The base string to compare against.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-allow-transposition &lt;boolean&gt; (property: allowTransposition)
 * &nbsp;&nbsp;&nbsp;If enabled, transposition is allowed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EditDistance
  extends AbstractTransformer {

  private static final long serialVersionUID = 8672966207037874116L;

  /** the base string to use for computing the distance. */
  protected String m_Base;

  /** whether to allow transposition. */
  protected boolean m_AllowTransposition;

  /** the distance. */
  protected transient com.aliasi.spell.EditDistance m_Distance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the edit distance between the supplied base string and the one passing through, outputting the distance.\n"
	     + "If a string array of length two is passing through, the 1st element is considered the base string instead and the distance computed to the 2nd element.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "base", "base",
      "");

    m_OptionManager.add(
      "allow-transposition", "allowTransposition",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Distance = null;
  }

  /**
   * Sets the base string to compare against.
   *
   * @param value	the base string
   */
  public void setBase(String value) {
    m_Base = value;
    reset();
  }

  /**
   * Returns the base string to compare against.
   *
   * @return		the base string
   */
  public String getBase() {
    return m_Base;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String baseTipText() {
    return "The base string to compare against.";
  }

  /**
   * Sets whether transposition is allowed.
   *
   * @param value	true if allowed
   */
  public void setAllowTransposition(boolean value) {
    m_AllowTransposition = value;
    reset();
  }

  /**
   * Returns whether transposition is allowed.
   *
   * @return		true if allowed
   */
  public boolean getAllowTransposition() {
    return m_AllowTransposition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowTranspositionTipText() {
    return "If enabled, transposition is allowed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "base", (m_Base.isEmpty() ? "-none-" : m_Base), "compare with: ");
    result += QuickInfoHelper.toString(this, "allowTransposition", (m_AllowTransposition ? "transposition allowed" : "no transposition"), ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Double.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	other;
    String[]	strings;
    double	distance;

    result = null;

    if (m_Distance == null)
      m_Distance = new com.aliasi.spell.EditDistance(m_AllowTransposition);

    if (m_InputToken.hasPayload(String.class)) {
      other = (String) m_InputToken.getPayload();
      distance = m_Distance.distance(m_Base, other);
      m_OutputToken = new Token(distance);
    }
    else if (m_InputToken.hasPayload(String[].class)) {
      strings = m_InputToken.getPayload(String[].class);
      if (strings.length == 2) {
	distance = m_Distance.distance(strings[0], strings[1]);
	m_OutputToken = new Token(distance);
      }
      else {
	result = "Expected a string array of length 2, but got: " + strings.length;
      }
    }
    else {
      result = m_InputToken.unhandledData();
    }

    return result;
  }
}
