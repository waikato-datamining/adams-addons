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
 * FlowJepScriptlet.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.ObjectCopyHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.flow.control.StorageName;
import adams.flow.control.VariableNameStorageNamePair;
import jep.SharedInterpreter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates Jep/Python scripts that are run in the flow.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowJepScriptlet
  extends AbstractJepScriptlet {

  private static final long serialVersionUID = 7919172085779747176L;

  /** the script to execute. */
  protected String m_Script;

  /** the script file to execute. */
  protected File m_ScriptFile;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /** the inputs. */
  protected VariableNameStorageNamePair[] m_Inputs;

  /** the outputs. */
  protected VariableNameStorageNamePair[] m_Outputs;

  /** the forwards. */
  protected BaseString[] m_Forwards;

  /** the forwards map. */
  protected Map<String,Object> m_ForwardsMap;

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param script	the script to execute
   */
  public FlowJepScriptlet(String id, String script) {
    this(id, script, null, null, null, null, false);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param script	the script to execute
   * @param inputs 	the inputs for the script (from storage)
   * @param outputs 	the outputs from the script (to go back into storage)
   * @param forwards 	the variable values from the script to forward as map
   */
  public FlowJepScriptlet(String id, String script, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
    this(id, script, null, inputs, outputs, forwards, expandVars);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param scriptFile	the script file to execute
   */
  public FlowJepScriptlet(String id, File scriptFile) {
    this(id, null, scriptFile, null, null, null, false);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param scriptFile	the script file to execute
   * @param inputs 	the inputs for the script (from storage)
   * @param outputs 	the outputs from the script (to go back into storage)
   * @param forwards 	the variable values from the script to forward as map
   */
  public FlowJepScriptlet(String id, File scriptFile, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
    this(id, null, scriptFile, inputs, outputs, forwards, expandVars);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param script 	the script to execute, can be null
   * @param scriptFile	the script file to execute, can be null
   * @param inputs 	the inputs for the script (from storage)
   * @param outputs 	the outputs from the script (to go back into storage)
   * @param forwards 	the variable values from the script to forward as map
   */
  public FlowJepScriptlet(String id, String script, File scriptFile, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
    super(id);
    if ((script == null) && (scriptFile == null))
      throw new IllegalArgumentException("Either script or script file need to be provided!");
    if (scriptFile != null) {
      if (!scriptFile.exists())
	throw new IllegalArgumentException("Script file does not exist: " + scriptFile);
      if (scriptFile.isDirectory())
	throw new IllegalArgumentException("Script file points to directory: " + scriptFile);
    }

    m_Script          = script;
    m_ScriptFile      = scriptFile;
    m_Inputs          = ObjectCopyHelper.copyObjects(inputs);
    m_Outputs         = ObjectCopyHelper.copyObjects(outputs);
    m_Forwards        = ObjectCopyHelper.copyObjects(forwards);
    m_ExpandVariables = expandVars;
  }

  /**
   * Returns the script content.
   *
   * @return		the content
   */
  public String getScriptContent() {
    if (m_Script != null)
      return m_Script;
    else
      return Utils.flatten(FileUtils.loadFromFile(m_ScriptFile), "\n");
  }

  /**
   * Returns the inputs to use (objects to retrieve from storage), if any.
   *
   * @return		the inputs, can be null
   */
  public VariableNameStorageNamePair[] getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the outputs to use (objects to store back in storage), if any.
   *
   * @return		the outputs, can be null
   */
  public VariableNameStorageNamePair[] getOutputs() {
    return m_Outputs;
  }

  /**
   * Returns the variables to forward.
   *
   * @return		the forwards, can be null
   */
  public BaseString[] getForwards() {
    return m_Forwards;
  }

  /**
   * Returns the parameter values to forward as map.
   *
   * @return		the map, can be null
   */
  public Map<String,Object> getForwardsMap() {
    return m_ForwardsMap;
  }

  /**
   * Returns whether ADAMS variables get expanded in the script.
   *
   * @return		true if expanded
   */
  public boolean getExpandVariables() {
    return m_ExpandVariables;
  }

  /**
   * Executes the script.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String execute() {
    SharedInterpreter 	interpreter;
    String		result;
    boolean		inline;
    String		script;
    StorageName 	sname;
    Object		value;

    result = null;
    inline = ((m_ScriptFile == null) || !m_ScriptFile.exists() || m_ScriptFile.isDirectory());

    try {
      interpreter = getOwner().getInterpreter();

      // insert storage items
      if ((m_FlowContext != null) && (m_Inputs != null)) {
	for (VariableNameStorageNamePair input : m_Inputs) {
	  sname = input.storageNameValue();
	  if (!m_FlowContext.getStorageHandler().getStorage().has(sname))
	    throw new IllegalStateException("Storage item not found: " + sname);
	  value = m_FlowContext.getStorageHandler().getStorage().get(sname);
	  interpreter.set(input.variableNameValue(), value);
	}
      }

      // execute script
      if (inline)
	script = m_Script;
      else
	script = Utils.flatten(FileUtils.loadFromFile(m_ScriptFile), "\n");
      if (m_ExpandVariables && (m_FlowContext != null)) {
	if (isLoggingEnabled())
	  getLogger().info("Expanding variables...");
	script = m_FlowContext.getVariables().expand(script);
      }
      if (isLoggingEnabled())
	getLogger().info("Running script: " + Shortening.shortenEnd(script, 50));
      getOwner().getInterpreter().exec(script);

      // retrieve storage items
      if ((m_FlowContext != null) && (m_Inputs != null)) {
	for (VariableNameStorageNamePair output : m_Outputs) {
	  value = interpreter.getValue(output.variableNameValue());
	  m_FlowContext.getStorageHandler().getStorage().put(output.storageNameValue(), value);
	}
      }

      // compile output
      if (m_Forwards != null) {
	m_ForwardsMap = new HashMap<>();
	for (BaseString forward : m_Forwards) {
	  value = interpreter.getValue(forward.getValue());
	  m_ForwardsMap.put(forward.getValue(), value);
	}
      }

      if (isLoggingEnabled())
	getLogger().info("Finished script: " + Shortening.shortenEnd(script, 50));
    }
    catch (Exception e) {
      if (inline)
	result = LoggingHelper.handleException(this, "Failed to execute inline script!", e);
      else
	result = LoggingHelper.handleException(this, "Failed to execute script file: " + m_ScriptFile, e);
    }

    m_LastError = result;
    m_Finished  = true;

    return result;
  }
}
