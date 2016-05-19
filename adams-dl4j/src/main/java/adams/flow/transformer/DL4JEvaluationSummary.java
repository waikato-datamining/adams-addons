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
 * WekaEvaluationSummary.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.flow.container.DL4JEvaluationContainer;
import adams.flow.core.Token;
import org.deeplearning4j.eval.Evaluation;

import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Generates a summary string of the Evaluation objects that it receives.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.deeplearning4j.eval.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.DL4JEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.DL4JEvaluationContainer: Evaluation, Model
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
 * &nbsp;&nbsp;&nbsp;default: DL4JEvaluationSummary
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-confusion-matrix &lt;boolean&gt; (property: confusionMatrix)
 * &nbsp;&nbsp;&nbsp;If set to true, then the confusion matrix will be output as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-comment &lt;adams.core.base.BaseText&gt; (property: comment)
 * &nbsp;&nbsp;&nbsp;An optional comment to output in the summary.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JEvaluationSummary
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8082115424369061977L;

  /** whether to print the confusion matrix as well. */
  protected boolean m_ConfusionMatrix;

  /** an optional comment to output. */
  protected BaseText m_Comment;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a summary string of the Evaluation objects "
      + "that it receives.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "confusion-matrix", "confusionMatrix",
      false);

    m_OptionManager.add(
      "comment", "comment",
      new BaseText(""));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    ArrayList<String>	options;
    String		value;

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "confusionMatrix", m_ConfusionMatrix, "confusion matrix"));
    result = QuickInfoHelper.flatten(options);
    
    value = QuickInfoHelper.toString(this, "comment", (m_Comment.stringValue().length() > 0 ? Utils.shorten(m_Comment.stringValue(), 20) : null));
    if (value != null) {
      if (result.length() > 0)
	result += ", ";
      result += "comment: " + value;
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.deeplearning4j.eval.Evaluation.class, adams.flow.container.DL4JEvaluationContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, DL4JEvaluationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Sets whether to output the confusion matrix as well.
   *
   * @param value	if true then the confusion matrix will be output as well
   */
  public void setConfusionMatrix(boolean value) {
    m_ConfusionMatrix = value;
    reset();
  }

  /**
   * Returns whether to output the confusion matrix as well.
   *
   * @return		true if the confusion matrix stats are output as well
   */
  public boolean getConfusionMatrix() {
    return m_ConfusionMatrix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String confusionMatrixTipText() {
    return "If set to true, then the confusion matrix will be output as well.";
  }

  /**
   * Sets the comment to output in the summary.
   *
   * @param value	the comment
   */
  public void setComment(BaseText value) {
    m_Comment = value;
    reset();
  }

  /**
   * Returns the comment to output in the summary.
   *
   * @return		the comment
   */
  public BaseText getComment() {
    return m_Comment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentTipText() {
    return "An optional comment to output in the summary.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Evaluation		eval;
    StringBuilder	buffer;
    boolean		prolog;
    String[]		comment;

    result = null;

    if (m_InputToken.getPayload() instanceof DL4JEvaluationContainer)
      eval = (Evaluation) ((DL4JEvaluationContainer) m_InputToken.getPayload()).getValue(DL4JEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
    buffer = new StringBuilder();
    prolog = false;

    // comments
    if (m_Comment.getValue().length() > 0) {
      comment = m_Comment.getValue().split("\n");
      if (comment.length == 1) {
	buffer.append("Comment: " + m_Comment + "\n");
      }
      else {
	buffer.append("Comment:\n");
	for (String line: comment)
	  buffer.append(line + "\n");
      }
      prolog = true;
    }

    // separator
    if (prolog)
      buffer.append("\n");

    // summary
    buffer.append(eval.stats());

    // confusion matrix
    if (m_ConfusionMatrix) {
      try {
	buffer.append("\n\n" + eval.confusionToString());
      }
      catch (Exception e) {
	result = handleException("Failed to generate confusion matrix: ", e);
      }
    }

    m_OutputToken = new Token(buffer.toString());

    return result;
  }
}
