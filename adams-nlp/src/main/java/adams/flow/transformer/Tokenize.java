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
 * Tokenize.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;


import adams.core.QuickInfoHelper;
import adams.flow.transformer.tokenizer.AbstractTokenizer;
import adams.flow.transformer.tokenizer.StanfordPTBTokenizer;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Splits document strings into sentences using the specified document to sentences tokenizer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: DocumentToSentences
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the sentences as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-tokenizer &lt;adams.flow.transformer.tokenizer.AbstractTokenizer&gt; (property: tokenizer)
 * &nbsp;&nbsp;&nbsp;The tokenizer to use for generating sentence strings from document strings.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.tokenizer.StanfordPTBTokenizer
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11957 $
 */
public class Tokenize
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -4128320094517359349L;
  
  /** the tokenizer to use. */
  protected AbstractTokenizer m_Tokenizer;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits document strings into sentences using the specified document to sentences tokenizer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tokenizer", "tokenizer",
	    new StanfordPTBTokenizer());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the sentences as array or one-by-one.";
  }
  
  /**
   * Sets the tokenizer to use.
   *
   * @param value	the tokenizer
   */
  public void setTokenizer(AbstractTokenizer value) {
    m_Tokenizer = value;
    reset();
  }

  /**
   * Returns the tokenizer to use.
   *
   * @return		the tokenizer
   */
  public AbstractTokenizer getTokenizer() {
    return m_Tokenizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tokenizerTipText() {
    return "The tokenizer to use for generating words from strings.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "tokenizer", m_Tokenizer, "tokenizer: ");
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<String> 	words;
    
    result = null;
    
    try {
      words = m_Tokenizer.tokenize((String) m_InputToken.getPayload());
      m_Queue.addAll(words);
    }
    catch (Exception e) {
      result = handleException("Failed to tokenize string: " + m_InputToken.getPayload(), e);
    }
    
    return result;
  }
}
