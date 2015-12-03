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
 * StanfordParseTreeToXML.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.stanford.nlp.trees.Tree;

/**
 <!-- globalinfo-start -->
 * Turns a Stanford parse tree into an XML string.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output-scores &lt;boolean&gt; (property: outputScores)
 * &nbsp;&nbsp;&nbsp;If enabled, the scores are output as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class StanfordParseTreeToXML
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = -309330039614297403L;

  /** whether to output the scores as well. */
  protected boolean m_OutputScores;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a Stanford parse tree into an XML string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-scores", "outputScores",
	    false);
  }

  /**
   * Sets whether to output the scores as well.
   *
   * @param value	true if to output scores
   */
  public void setOutputScores(boolean value) {
    m_OutputScores = value;
    reset();
  }

  /**
   * Returns whether to output the scores as well.
   *
   * @return		true if scores are output
   */
  public boolean getOutputScores() {
    return m_OutputScores;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputScoresTipText() {
    return "If enabled, the scores are output as well.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Tree.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Tree		tree;
    StringWriter	swriter;
    PrintWriter		writer;
    
    tree = (Tree) m_Input;
    swriter = new StringWriter();
    writer  = new PrintWriter(swriter);
    tree.indentedXMLPrint(writer, m_OutputScores);
    writer.close();
    
    return swriter.toString();
  }
}
