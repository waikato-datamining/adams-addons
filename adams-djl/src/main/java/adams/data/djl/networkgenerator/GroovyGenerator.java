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
 * GroovyGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.networkgenerator;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.scripting.GroovyScript;
import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.nn.Block;

/**
 * Uses Groovy to generate the network.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GroovyGenerator
  extends AbstractScriptedNetworkGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient NetworkGenerator m_GeneratorObject;

  /** the inline script. */
  protected GroovyScript m_InlineScript;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses Groovy to generate the network.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "inline-script", "inlineScript",
      getDefaultInlineScript());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (QuickInfoHelper.hasVariable(this, "scriptFile") || !m_ScriptFile.isDirectory())
      return super.getQuickInfo();
    else
      return QuickInfoHelper.toString(this, "inlineScript", Shortening.shortenEnd(m_InlineScript.stringValue(), 50));
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
      "The options for the Groovy script; must consist of 'key=value' pairs "
	+ "separated by blanks; the value of 'key' can be accessed via the "
	+ "'getAdditionalOptions().getXYZ(\"key\")' method in the Groovy actor.";
  }

  /**
   * Returns the default inline script.
   *
   * @return		the default script
   */
  protected GroovyScript getDefaultInlineScript() {
    return new GroovyScript();
  }

  /**
   * Sets the inline script to use instead of the external script file.
   *
   * @param value 	the inline script
   */
  public void setInlineScript(GroovyScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Gets the inline script to use instead of the external script file.
   *
   * @return 		the inline script
   */
  public GroovyScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inlineScriptTipText() {
    return "The inline script, if not using an external script file.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = adams.core.scripting.Groovy.getSingleton().loadScriptObject(
      NetworkGenerator.class,
      m_ScriptFile,
      m_InlineScript,
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
    return null;
  }

  /**
   * Method for checking whether we can setUp the script.
   *
   * @return		true if it can be setUp
   */
  @Override
  protected boolean canSetUpScript() {
    if (!m_InlineScript.isEmpty())
      return true;

    return super.canSetUpScript();
  }

  /**
   * Tries to initialize the scripts object, sets its options and performs
   * some checks.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String initScriptObject() {
    String	result;

    result = super.initScriptObject();
    if (result == null)
      m_GeneratorObject = (NetworkGenerator) m_ScriptObject;

    return result;
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    if (m_GeneratorObject != null)
      return m_GeneratorObject.requiresFlowContext();
    else
      return false;
  }

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset	the dataset to generate the network for
   * @return		the network
   */
  @Override
  protected Block doGenerate(TabularDataset dataset) {
    m_GeneratorObject.setFlowContext(m_FlowContext);
    return m_GeneratorObject.generate(dataset);
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    m_GeneratorObject = null;
    super.destroy();
  }
}
