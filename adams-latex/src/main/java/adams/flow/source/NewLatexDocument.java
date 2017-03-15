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
 * NewLatexDocument.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.base.BaseText;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a new LaTeX document.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: LatexNewDocument
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
 * <pre>-prolog &lt;adams.core.base.BaseText&gt; (property: prolog)
 * &nbsp;&nbsp;&nbsp;The (optional) prolog to insert as comments before the document starts.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-document-class &lt;java.lang.String&gt; (property: documentClass)
 * &nbsp;&nbsp;&nbsp;The document class to use for the document.
 * &nbsp;&nbsp;&nbsp;default: article
 * </pre>
 * 
 * <pre>-document-class-options &lt;java.lang.String&gt; (property: documentClassOptions)
 * &nbsp;&nbsp;&nbsp;The options (if any) for the document class.
 * &nbsp;&nbsp;&nbsp;default: a4paper
 * </pre>
 * 
 * <pre>-additional-statements &lt;adams.core.base.BaseText&gt; (property: additionalStatements)
 * &nbsp;&nbsp;&nbsp;The additional statements (eg include or usepackage) to insert.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewLatexDocument
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 1277901907777271692L;

  /** the optional prolog (inserted as comments). */
  protected BaseText m_Prolog;

  /** the document class. */
  protected String m_DocumentClass;

  /** optional parameters for the document class. */
  protected String m_DocumentClassOptions;

  /** the additional statements to include. */
  protected BaseText m_AdditionalStatements;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a new LaTeX document.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prolog", "prolog",
      new BaseText());

    m_OptionManager.add(
      "document-class", "documentClass",
      "article");

    m_OptionManager.add(
      "document-class-options", "documentClassOptions",
      "a4paper");

    m_OptionManager.add(
      "additional-statements", "additionalStatements",
      new BaseText());
  }

  /**
   * Sets the prolog to insert (as comments).
   *
   * @param value	the prolog
   */
  public void setProlog(BaseText value) {
    m_Prolog = value;
    reset();
  }

  /**
   * Returns the prolog to insert (as comments).
   *
   * @return		the prolog
   */
  public BaseText getProlog() {
    return m_Prolog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prologTipText() {
    return "The (optional) prolog to insert as comments before the document starts.";
  }

  /**
   * Sets the document class to use.
   *
   * @param value	the class
   */
  public void setDocumentClass(String value) {
    m_DocumentClass = value;
    reset();
  }

  /**
   * Returns the document class to use.
   *
   * @return		the class
   */
  public String getDocumentClass() {
    return m_DocumentClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String documentClassTipText() {
    return "The document class to use for the document.";
  }

  /**
   * Sets the options to use for the document class.
   *
   * @param value	the options
   */
  public void setDocumentClassOptions(String value) {
    m_DocumentClassOptions = value;
    reset();
  }

  /**
   * Returns the options to use for the document class.
   *
   * @return		the options
   */
  public String getDocumentClassOptions() {
    return m_DocumentClassOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String documentClassOptionsTipText() {
    return "The options (if any) for the document class.";
  }

  /**
   * Sets the additional statements to insert.
   *
   * @param value	the statements
   */
  public void setAdditionalStatements(BaseText value) {
    m_AdditionalStatements = value;
    reset();
  }

  /**
   * Returns the additional statements to insert.
   *
   * @return		the statements
   */
  public BaseText getAdditionalStatements() {
    return m_AdditionalStatements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalStatementsTipText() {
    return "The additional statements (eg include or usepackage) to insert.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    StringBuilder	doc;
    String[]		lines;

    doc = new StringBuilder();

    // prolog
    if (!m_Prolog.isEmpty()) {
      lines = m_Prolog.getValue().split("\n");
      for (String line: lines) {
	doc.append("% ");
	doc.append(line);
	doc.append("\n");
      }
      doc.append("\n");
    }

    // class
    doc.append("\\documentclass");
    if (!m_DocumentClassOptions.isEmpty())
      doc.append("[").append(m_DocumentClassOptions).append("]");
    doc.append("{").append(m_DocumentClass).append("}\n");
    doc.append("\n");

    // additional statements
    if (!m_AdditionalStatements.isEmpty()) {
      lines = m_AdditionalStatements.getValue().split("\n");
      for (String line: lines) {
	doc.append(line);
	doc.append("\n");
      }
      doc.append("\n");
    }

    // start document
    doc.append("\\begin{document}\n");
    doc.append("\n");

    m_OutputToken = new Token(doc.toString());

    return null;
  }
}
