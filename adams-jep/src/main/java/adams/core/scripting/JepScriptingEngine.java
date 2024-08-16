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
 * JepScriptingEngine.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.logging.CustomLoggingLevelObject;
import adams.core.scriptingengine.BackgroundScriptingEngine;
import adams.core.scriptingengine.BackgroundScriptingEngineRegistry;

/**
 * Engine that executes Jep/Python scripts centrally.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepScriptingEngine
  extends CustomLoggingLevelObject
  implements BackgroundScriptingEngine {

  private static final long serialVersionUID = -3296150002622437227L;

  /** the singleton. */
  protected static JepScriptingEngine m_Singleton;

  /** the thread to use for executing the commands. */
  protected JepScriptingEngineThread m_ProcessingThread;

  /**
   * Initializes the engine.
   */
  public JepScriptingEngine() {
    m_ProcessingThread = getProcessingThread();
    m_ProcessingThread.start();
    BackgroundScriptingEngineRegistry.getSingleton().register(this);
  }

  /**
   * Places the scriplet in the processing queue.
   *
   * @param scriplet	the scriplet to execute
   */
  public void add(JepScriptlet scriplet) {
    if (getProcessingThread().isRunning())
      getProcessingThread().add(scriplet);
    else
      getLogger().severe("Engine no longer running, cannot accept new scriptlets!");
  }

  /**
   * Returns the thread for processing the scripting commands.
   *
   * @return		the thread
   */
  public synchronized JepScriptingEngineThread getProcessingThread() {
    if (m_ProcessingThread == null)
      m_ProcessingThread = new JepScriptingEngineThread(this);

    return m_ProcessingThread;
  }

  /**
   * Stops the scripting engine.
   */
  @Override
  public void stopEngine() {
    getProcessingThread().stopExecution();
    getProcessingThread().clear();
    BackgroundScriptingEngineRegistry.getSingleton().deregister(this);
  }

  /**
   * Returns the singleton instance of the scripting engine.
   *
   * @return		the engine
   */
  public static synchronized JepScriptingEngine getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new JepScriptingEngine();

    return m_Singleton;
  }
}
