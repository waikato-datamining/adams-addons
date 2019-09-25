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
 * Scripted.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.generator;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import adams.flow.standalone.Rat;

/**
 <!-- globalinfo-start -->
 * A rat generator that uses any scripting handler for generating the rat actor(s) with a script located in the specified file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 * <pre>-send-error-queue &lt;adams.flow.control.StorageName&gt; (property: sendErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with send errors.
 * &nbsp;&nbsp;&nbsp;default: senderrors
 * </pre>
 *
 * <pre>-show-in-control &lt;boolean&gt; (property: showInControl)
 * &nbsp;&nbsp;&nbsp;If enabled, the generated Rat will be displayed in the adams.flow.standalone.RatControl
 * &nbsp;&nbsp;&nbsp;control panel.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-options &lt;adams.core.base.BaseText&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the script; must consist of 'key=value' pairs separated
 * &nbsp;&nbsp;&nbsp;by blanks; the value of 'key' can be accessed via the 'getAdditionalOptions
 * &nbsp;&nbsp;&nbsp;().getXYZ("key")' method in the script actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-handler &lt;adams.core.scripting.AbstractScriptingHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler to use for scripting.
 * &nbsp;&nbsp;&nbsp;default: adams.core.scripting.Dummy
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Scripted
  extends AbstractScriptedRatGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient AbstractRatGenerator m_GeneratorObject;

  /** the scripting handler to use. */
  protected AbstractScriptingHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A rat generator that uses any scripting handler for generating the "
	+ "rat actor(s) with a script located in the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      new Dummy());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
      "The options for the script; must consist of 'key=value' pairs "
	+ "separated by blanks; the value of 'key' can be accessed via the "
	+ "'getAdditionalOptions().getXYZ(\"key\")' method in the script actor.";
  }

  /**
   * Sets the handler to use for scripting.
   *
   * @param value 	the handler
   */
  public void setHandler(AbstractScriptingHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Gets the handler to use for scripting.
   *
   * @return 		the handler
   */
  public AbstractScriptingHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String handlerTipText() {
    return "The handler to use for scripting.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = m_Handler.loadScriptObject(
      AbstractRatGenerator.class,
      m_ScriptFile,
      m_ScriptOptions,
      getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checks.
   */
  @Override
  protected void check() {
    super.check();

    m_GeneratorObject = (AbstractRatGenerator) m_ScriptObject;
    m_GeneratorObject.setLog(m_Log);
    m_GeneratorObject.setSendErrorQueue(m_SendErrorQueue);
    m_GeneratorObject.setShowInControl(m_ShowInControl);
    m_GeneratorObject.setLoggingLevel(m_LoggingLevel);
    m_GeneratorObject.check();
  }

  /**
   * Generates the actual setups.
   *
   * @return		the generated setups
   */
  @Override
  protected Rat[] doGenerate() {
    return m_GeneratorObject.generate();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_GeneratorObject = null;
  }
}
