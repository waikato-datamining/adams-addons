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
 * JepScriptingEngineThread.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.Stoppable;
import adams.core.logging.LoggingHelper;
import jep.JepException;
import jep.SharedInterpreter;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A thread class for processing Jep scriptlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepScriptingEngineThread
  extends Thread
  implements Stoppable {

  /** the owning engine. */
  protected JepScriptingEngine m_Owner;

  /** the current command queue. */
  protected LinkedBlockingQueue<JepScriptlet> m_Scriptlets;

  /** whether the thread is running. */
  protected boolean m_Running;

  /** whether any command is currently being processed. */
  protected boolean m_Processing;

  /** the central interpreter. */
  protected SharedInterpreter m_Interpreter;

  /**
   * Initializes the thread.
   *
   * @param owner	the owning scripting engine
   */
  public JepScriptingEngineThread(JepScriptingEngine owner) {
    super();

    m_Owner       = owner;
    m_Scriptlets  = new LinkedBlockingQueue<>();
    m_Running     = true;
    m_Processing  = false;
  }

  /**
   * Returns the owning scripting engine.
   *
   * @return		the owner
   */
  public JepScriptingEngine getOwner() {
    return m_Owner;
  }

  /**
   * Returns the interpreter in use.
   *
   * @return		the interpreter
   */
  public SharedInterpreter getInterpreter() {
    return m_Interpreter;
  }

  /**
   * Clears the queue of scriplets.
   */
  public void clear() {
    m_Scriptlets.clear();
  }

  /**
   * Adds the scriptlet to the queue.
   *
   * @param scriptlet	the scriptlet to add
   */
  public void add(JepScriptlet scriptlet) {
    scriptlet.setOwner(this);
    if (JepUtils.isPresent())
      m_Scriptlets.add(scriptlet);
    else
      scriptlet.fail("Jep is not present! Python environment present and jep installed (pip install jep)?");
  }

  /**
   * Executes the script.
   *
   * @param scriptFile	the script to execute
   * @throws JepException	if execution fails
   */
  protected void runScript(File scriptFile) throws JepException {
    getInterpreter().runScript(scriptFile.getAbsolutePath());
  }

  /**
   * Stops the execution of scripting commands.
   */
  public void stopExecution() {
    clear();
    m_Running = false;
  }

  /**
   * Returns whether the thread is still active and waits for commands
   * to execute.
   *
   * @return		true if accepting commands to process
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns whether a command is currently being processed.
   *
   * @return		true if a command is being processed
   */
  public boolean isProcessing() {
    return m_Processing;
  }

  /**
   * Returns whether there are no commands currently in the queue.
   *
   * @return		true if no commands waiting to be executed
   */
  public synchronized boolean isEmpty() {
    return m_Scriptlets.isEmpty();
  }

  /**
   * Performs some preprocessing.
   *
   * @param scriptlet	the scriptlet that is about to be executed
   */
  protected void preProcess(JepScriptlet scriptlet) {
    getOwner().getLogger().info("Executing script: " + scriptlet.getID());
  }

  /**
   * Executes the given scriptlet.
   *
   * @param scriptlet	the scriptlet to execute
   * @return		the error message, null if no problems occurred
   */
  protected String doProcess(JepScriptlet scriptlet) {
    return scriptlet.execute();
  }

  /**
   * Performs some postprocessing.
   *
   * @param scriptlet	the scriptlet that was executed
   * @param success	true if successfully executed
   * @param lastError	the error, or null if none happened
   */
  protected void postProcess(JepScriptlet scriptlet, boolean success, String lastError) {
    if (success)
      getOwner().getLogger().info("Script " + scriptlet.getID() + " successfully executed.");
    else
      getOwner().getLogger().warning("Script " + scriptlet.getID() + " failed" + (lastError == null ? "!" : ": " + lastError));
  }

  /**
   * Executes the scripting commands.
   */
  @Override
  public void run() {
    JepScriptlet 	scriptlet;
    boolean		success;
    String		lastError;

    try {
      m_Interpreter = new SharedInterpreter();
    }
    catch (Exception e) {
      getOwner().getLogger().log(Level.SEVERE, "Failed to initialize Jep interpreter, cannot run Python scripts!", e);
      m_Running = false;
      return;
    }

    while (m_Running) {
      try {
        scriptlet = m_Scriptlets.poll(100, TimeUnit.MILLISECONDS);
        if ((scriptlet != null) && (m_Running)) {
          m_Processing = true;

          preProcess(scriptlet);

          // process scriptlet
          try {
            lastError = doProcess(scriptlet);
            success   = (lastError == null);
          }
          catch (Exception e) {
            success = false;
	    lastError = "Error encountered executing script " + scriptlet.getID() + ":\n" + LoggingHelper.throwableToString(e);
          }

          postProcess(scriptlet, success, lastError);

          m_Processing = false;
        }
      }
      catch (Exception e) {
	getOwner().getLogger().log(Level.SEVERE, "Failed to execute scriptlet!", e);
      }
    }

    m_Interpreter.close();
  }
}