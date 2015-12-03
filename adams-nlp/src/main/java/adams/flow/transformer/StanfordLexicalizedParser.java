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
 * StanfordLexicalizedParser.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

/**
 <!-- globalinfo-start -->
 * Parses a string (= sentence) or string array (= tokenized sentence) using the Stanford LexicalizedParser.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;edu.stanford.nlp.trees.Tree<br>
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
 * &nbsp;&nbsp;&nbsp;default: StanfordLexicalizedParser
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
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: model)
 * &nbsp;&nbsp;&nbsp;The model file to load and use.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-additional-option &lt;adams.core.base.BaseString&gt; [-additional-option ...] (property: additionalOptions)
 * &nbsp;&nbsp;&nbsp;The additional options for the parser.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class StanfordLexicalizedParser
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -563084917234302128L;

  /** the key for storing the parser object in the backup. */
  public final static String BACKUP_PARSER = "parser";

  /** the model file. */
  protected PlaceholderFile m_Model;
  
  /** the additional options for the parser. */
  protected BaseString[] m_AdditionalOptions;
  
  /** the parser in use. */
  protected LexicalizedParser m_Parser;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Parses a string (= sentence) or string array (= tokenized sentence) using the Stanford LexicalizedParser.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "model", "model",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "additional-option", "additionalOptions",
	    new BaseString[0]);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Parser = null;
  }
  
  /**
   * Sets the model file to load and use.
   *
   * @param value	the model
   */
  public void setModel(PlaceholderFile value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the model file to load and use.
   *
   * @return		the model
   */
  public PlaceholderFile getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelTipText() {
    return "The model file to load and use.";
  }

  /**
   * Sets the additional options for the parser.
   *
   * @param value	the options
   */
  public void setAdditionalOptions(BaseString[] value) {
    m_AdditionalOptions = value;
    reset();
  }

  /**
   * Returns the model file to load and use.
   *
   * @return		the model
   */
  public BaseString[] getAdditionalOptions() {
    return m_AdditionalOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalOptionsTipText() {
    return "The additional options for the parser.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "model", m_Model, "model: ");
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_PARSER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Parser != null)
      result.put(BACKUP_PARSER, m_Parser);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_PARSER)) {
      m_Parser = (LexicalizedParser) state.get(BACKUP_PARSER);
      state.remove(BACKUP_PARSER);
    }

    super.restoreState(state);
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
    return new Class[]{Tree.class};
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
    
    if (result == null) {
      if (!m_Model.exists())
	result = "Model file does not exist: " + m_Model;
      else if (m_Model.isDirectory())
	result = "Model file points to a directory: " + m_Model;
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	options;
    int		i;
    Tree	tree;
    
    result = null;
    
    // load parser?
    if (m_Parser == null) {
      options = new String[m_AdditionalOptions.length];
      for (i = 0; i < m_AdditionalOptions.length; i++)
	options[i] = m_AdditionalOptions[i].getValue();
      m_Parser = edu.stanford.nlp.parser.lexparser.LexicalizedParser.loadModel(m_Model.getAbsolutePath(), options);
    }
    
    if (m_InputToken.getPayload() instanceof String)
      tree = m_Parser.apply(Sentence.toWordList(((String) m_InputToken.getPayload()).split("\\s")));  // TODO stanford tokenizer?
    else
      tree = m_Parser.apply(Sentence.toWordList((String[]) m_InputToken.getPayload()));
    
    m_OutputToken = new Token(tree);
    
    return result;
  }
}
