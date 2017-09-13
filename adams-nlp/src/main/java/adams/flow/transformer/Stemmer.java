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
 * Stemmer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;


import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.transformer.stemmer.NullStemmer;

/**
 <!-- globalinfo-start -->
 * Applies the stemmer to the incoming word&#47;words.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: Stemmer
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
 * <pre>-stemmer &lt;adams.flow.transformer.stemmer.Stemmer&gt; (property: stemmer)
 * &nbsp;&nbsp;&nbsp;The stemmer to apply.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.stemmer.NullStemmer
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11957 $
 */
public class Stemmer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4128320094517359349L;
  
  /** the tokenizer to use. */
  protected adams.flow.transformer.stemmer.Stemmer m_Stemmer;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the stemmer to the incoming word/words.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stemmer", "stemmer",
      new NullStemmer());
  }
  
  /**
   * Sets the stemmer to use.
   *
   * @param value	the stemmer
   */
  public void setStemmer(adams.flow.transformer.stemmer.Stemmer value) {
    m_Stemmer = value;
    reset();
  }

  /**
   * Returns the stemmer to use.
   *
   * @return		the stemmer
   */
  public adams.flow.transformer.stemmer.Stemmer getStemmer() {
    return m_Stemmer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stemmerTipText() {
    return "The stemmer to apply.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "tokenizer", m_Stemmer, "tokenizer: ");
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
    return new Class[]{String.class, String[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String 		singleOut;
    String[] 		multiIn;
    String[]		multiOut;
    int			i;

    result = null;

    if (m_InputToken.getPayload() instanceof String) {
      singleOut = m_Stemmer.stem((String) m_InputToken.getPayload());
      m_OutputToken = new Token(singleOut);
    }
    else if (m_InputToken.getPayload() instanceof String[]) {
      multiIn  = (String[]) m_InputToken.getPayload();
      multiOut = new String[multiIn.length];
      for (i = 0; i < multiIn.length; i++)
	multiOut[i] = m_Stemmer.stem(multiIn[i]);
      m_OutputToken = new Token(multiOut);
    }
    else {
      result = "Unhandled input: " + Utils.classToString(m_InputToken.getPayload());
    }

    return result;
  }
}
