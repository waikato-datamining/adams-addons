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
 * SimpleJepScriptlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.logging.LoggingHelper;
import jep.SharedInterpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Simple script that gets executed in memory, line by line.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleJepScriptlet
  extends AbstractJepScriptlet {

  private static final long serialVersionUID = 2669479708955359329L;

  /** the script to execute. */
  protected String m_Script;

  /** the inputs. */
  protected Map<String,Object> m_Inputs;

  /** the output names. */
  protected String[] m_OutputNames;

  /** the optional output names. */
  protected String[] m_OptionalOutputNames;

  /** the outputs. */
  protected Map<String,Object> m_Outputs;

  /**
   * Initializes the scriptlet.
   *
   * @param id 			the ID of the script
   * @param script 		the script to execute (line by line)
   * @param inputs 		the inputs for the script (name/object)
   * @param outputNames 	the names of the outputs to retrieve
   */
  public SimpleJepScriptlet(String id, String script, Map<String,Object> inputs, String[] outputNames) {
    this(id, script, inputs, outputNames, null);
  }

  /**
   * Initializes the scriptlet.
   *
   * @param id 			the ID of the script
   * @param script 		the script to execute (line by line)
   * @param inputs 		the inputs for the script (name/object)
   * @param outputNames 	the names of the outputs to retrieve
   * @param optionalOutputNames the names of the optional outputs (won't throw an exception if not present)
   */
  public SimpleJepScriptlet(String id, String script, Map<String,Object> inputs, String[] outputNames, String[] optionalOutputNames) {
    super(id);

    m_Script              = script;
    m_Inputs              = (inputs == null) ? new HashMap<>() : new HashMap<>(inputs);
    m_OutputNames         = (outputNames == null) ? new String[0] : outputNames.clone();
    m_OptionalOutputNames = (optionalOutputNames == null) ? new String[0] : optionalOutputNames.clone();
    m_Outputs             = new HashMap<>();
  }

  /**
   * Returns the script to be executed (line by line).
   *
   * @return		the script
   */
  public String getScript() {
    return m_Script;
  }

  /**
   * Returns the input map of name/value.
   *
   * @return		the inputs
   */
  public Map<String, Object> getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the outputs to retrieve from the interpreter.
   *
   * @return		the names
   */
  public String[] getOutputNames() {
    return m_OutputNames;
  }

  /**
   * Returns the names of the optional outputs, i.e., ones that won't throw and
   * exception when not present.
   *
   * @return		the names
   */
  public String[] getOptionalOutputNames() {
    return m_OptionalOutputNames;
  }

  /**
   * Returns the output map. Gets populated after execution.
   *
   * @return		the outputs (name/value)
   */
  public Map<String, Object> getOutputs() {
    return m_Outputs;
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
    Set<String>		optional;

    result = null;

    try {
      interpreter = getOwner().getInterpreter();

      // feed inputs
      for (String name: m_Inputs.keySet()) {
	if (isLoggingEnabled())
	  getLogger().info("Setting input: " + name);
	try {
	  interpreter.set(name, m_Inputs.get(name));
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to set input '" + name + "'!", e);
	}
      }

      // execute script
      if (isLoggingEnabled())
	getLogger().info("Executing: " + m_Script);
      interpreter.exec(m_Script);

      // retrieving outputs
      m_Outputs.clear();
      optional = new HashSet<>(Arrays.asList(m_OptionalOutputNames));
      for (String name: m_OutputNames) {
	if (isLoggingEnabled())
	  getLogger().info("Getting output: " + name);
	if (optional.contains(name)) {
	  try {
	    m_Outputs.put(name, interpreter.getValue(name));
	  }
	  catch (Exception e) {
	    // suppress error
	  }
	}
	else {
	  try {
	    m_Outputs.put(name, interpreter.getValue(name));
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to get output '" + name + "'!", e);
	  }
	}
      }
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to execute script:\n" + m_Script, e);
    }

    m_LastError = result;
    m_Finished  = true;

    return result;
  }
}
