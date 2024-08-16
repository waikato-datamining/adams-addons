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
 * JepScriptlet.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.TempUtils;
import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingHelper;
import adams.flow.control.StorageName;
import adams.flow.control.VariableNameStorageNamePair;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import jep.SharedInterpreter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates Jep/Python scripts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepScriptlet
  extends CustomLoggingLevelObject
  implements FlowContextHandler {

  private static final long serialVersionUID = 7919172085779747176L;

  /** the owning thread (with the interpreter). */
  protected JepScriptingEngineThread m_Owner;

  /** the ID of the scriptlet. */
  protected String m_ID;

  /** the last error. */
  protected String m_LastError;

  /** the script to execute. */
  protected String m_Script;

  /** the script file to execute. */
  protected File m_ScriptFile;

  /** the flow context. */
  protected Actor m_FlowContext;

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

  /** whether the scriplet has been executed. */
  protected boolean m_Finished;

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param script	the script to execute
   */
  public JepScriptlet(String id, String script) {
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
  public JepScriptlet(String id, String script, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
    this(id, script, null, inputs, outputs, forwards, expandVars);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   * @param scriptFile	the script file to execute
   */
  public JepScriptlet(String id, File scriptFile) {
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
  public JepScriptlet(String id, File scriptFile, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
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
  public JepScriptlet(String id, String script, File scriptFile, VariableNameStorageNamePair[] inputs, VariableNameStorageNamePair[] outputs, BaseString[] forwards, boolean expandVars) {
    if ((script == null) && (scriptFile == null))
      throw new IllegalArgumentException("Either script or script file need to be provided!");
    if (scriptFile != null) {
      if (!scriptFile.exists())
	throw new IllegalArgumentException("Script file does not exist: " + scriptFile);
      if (scriptFile.isDirectory())
	throw new IllegalArgumentException("Script file points to directory: " + scriptFile);
    }

    m_Owner           = null;
    m_ID              = id;
    m_Script          = script;
    m_ScriptFile      = scriptFile;
    m_Inputs          = ObjectCopyHelper.copyObjects(inputs);
    m_Outputs         = ObjectCopyHelper.copyObjects(outputs);
    m_Forwards        = ObjectCopyHelper.copyObjects(forwards);
    m_ExpandVariables = expandVars;
    m_FlowContext     = null;
    m_Finished        = false;
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_ID);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Sets the owning thread.
   *
   * @param value	the owner
   */
  public void setOwner(JepScriptingEngineThread value) {
    m_Owner = value;
  }

  /**
   * Returns the owning thread.
   *
   * @return		the owner
   */
  public JepScriptingEngineThread getOwner() {
    return m_Owner;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns the ID of the script.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
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
   * Whether the scriptlet has finished execution.
   *
   * @return		true when finished
   */
  public boolean hasFinished() {
    return m_Finished;
  }

  /**
   * Returns any error that was encountered.
   *
   * @return		the error, null if none encountered
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Executes the script.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    SharedInterpreter 	interpreter;
    String		result;
    boolean		inline;
    File 		scriptFile;
    String		script;
    StorageName 	sname;
    Object		value;
    boolean		deleteScript;

    result       = null;
    inline       = ((m_ScriptFile == null) || !m_ScriptFile.exists() || m_ScriptFile.isDirectory());
    scriptFile   = null;
    deleteScript = false;

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
      if (inline) {
	deleteScript = true;
	scriptFile   = TempUtils.createTempFile("jep", ".py");
	script       = m_Script;
	if (m_ExpandVariables && (m_FlowContext != null)) {
	  getLogger().info("Expanding variables...");
	  script = m_FlowContext.getVariables().expand(script);
	}
	getLogger().info("Writing script to: " + scriptFile);
	FileUtils.writeToFile(scriptFile.getAbsolutePath(), script, false);
      }
      else {
	scriptFile = m_ScriptFile;
	if (m_ExpandVariables && (m_FlowContext != null)) {
	  getLogger().info("Expanding variables...");
	  script       = Utils.flatten(FileUtils.loadFromFile(m_ScriptFile), "\n");
	  script       = m_FlowContext.getVariables().expand(script);
	  deleteScript = true;
	  scriptFile   = TempUtils.createTempFile("jep", ".py");
	  getLogger().info("Writing script to: " + scriptFile);
	  FileUtils.writeToFile(scriptFile.getAbsolutePath(), script, false);
	}
      }
      getLogger().info("Running script: " + scriptFile);
      getOwner().runScript(scriptFile);

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

      getLogger().info("Finished script: " + scriptFile);
    }
    catch (Exception e) {
      if (inline)
	result = LoggingHelper.handleException(this, "Failed to execute inline script!", e);
      else
	result = LoggingHelper.handleException(this, "Failed to execute script file: " + m_ScriptFile, e);
    }

    if (deleteScript && (scriptFile != null)) {
      getLogger().info("Deleting script: " + scriptFile);
      FileUtils.delete(scriptFile);
    }

    m_LastError = result;
    m_Finished  = true;

    return result;
  }

  /**
   * Sets the error message and that the script has finished to true.
   *
   * @param msg		the error message to set
   */
  public void fail(String msg) {
    m_LastError = msg;
    m_Finished  = true;
  }
}
