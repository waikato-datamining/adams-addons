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
 * Git.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.git.GitOperation;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.source.git.AbstractGitOperation;
import adams.flow.source.git.Pull;
import adams.flow.standalone.GitRepo;

/**
 <!-- globalinfo-start -->
 * Performs the specified git operation and forwards its output
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Git
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
 * <pre>-operation &lt;adams.flow.source.git.AbstractGitOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The operation to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.git.Pull
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Git
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 5354822648702968385L;

  /** the operation to perform. */
  protected AbstractGitOperation m_Operation;

  /** the GitRepo context. */
  protected transient GitRepo m_GitRepo;

  /** the instance for the operations. */
  protected transient GitOperation m_GitOperation;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the specified git operation and forwards its output";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new Pull());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_GitRepo = null;
  }

  /**
   * Sets the operation to perform.
   *
   * @param value 	the operation
   */
  public void setOperation(AbstractGitOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the operation to perform.
   *
   * @return 		the operation
   */
  public AbstractGitOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The operation to perform.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation, "operation: ");
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MessageCollection	errors;
    String		output;

    result = null;

    if (m_GitRepo == null) {
      m_GitRepo = (GitRepo) ActorUtils.findClosestType(this, GitRepo.class, true);
      if (m_GitRepo == null)
	result = "Failed to locate " + Utils.classToString(GitRepo.class) + " actor!";
      else if (m_GitRepo.getGit() == null)
	result = "No Git instance available from " + Utils.classToString(GitRepo.class) + " actor!";
    }

    if (result == null) {
      if (m_GitOperation == null) {
	m_GitOperation = new GitOperation();
	m_GitOperation.setGit(m_GitRepo.getGit());
      }

      m_Operation.setOperation(m_GitOperation);
      errors = new MessageCollection();
      if (m_Operation.canExecute(errors)) {
	errors.clear();
	output = m_Operation.execute(errors);
	if (errors.isEmpty())
	  m_OutputToken = new Token(output);
	else
	  result = errors.toString();
      }
      else {
	if (errors.isEmpty())
	  result = "Cannot perform operation: " + m_Operation;
	else
	  result = errors.toString();
      }
    }

    return result;
  }
}
