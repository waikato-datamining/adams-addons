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
 * StanfordGrammaticalStructure.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 <!-- globalinfo-start -->
 * Turns a Stanford parse tree into a grammatical structure.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;edu.stanford.nlp.trees.Tree<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;edu.stanford.nlp.trees.GrammaticalStructure<br>
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
 * &nbsp;&nbsp;&nbsp;default: StanfordGrammaticalStructure
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
 * <pre>-language-pack &lt;edu.stanford.nlp.trees.TreebankLanguagePack&gt; (property: languagePack)
 * &nbsp;&nbsp;&nbsp;The language pack to use.
 * &nbsp;&nbsp;&nbsp;default: edu.stanford.nlp.trees.PennTreebankLanguagePack
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class StanfordGrammaticalStructure
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -563084917234302128L;

  /** the language pack. */
  protected TreebankLanguagePack m_LanguagePack;
  
  /** the grammar structure factory. */
  protected GrammaticalStructureFactory m_Factory;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a Stanford parse tree into a grammatical structure.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "language-pack", "languagePack",
	    new PennTreebankLanguagePack());
  }
  
  /**
   * Sets the language pack to use.
   *
   * @param value	the language pack
   */
  public void setLanguagePack(TreebankLanguagePack value) {
    m_LanguagePack = value;
    reset();
  }

  /**
   * Returns the language pack to use.
   *
   * @return		the language pack
   */
  public TreebankLanguagePack getLanguagePack() {
    return m_LanguagePack;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String languagePackTipText() {
    return "The language pack to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "languagePack", m_LanguagePack.getClass());
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Tree.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{GrammaticalStructure.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    GrammaticalStructure	structure;
    
    result = null;
    
    if (m_Factory == null)
      m_Factory = m_LanguagePack.grammaticalStructureFactory();

    structure     = m_Factory.newGrammaticalStructure((Tree) m_InputToken.getPayload());
    m_OutputToken = new Token(structure);
    
    return result;
  }
}
