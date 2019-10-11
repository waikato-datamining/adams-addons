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
 * AbstractTerminalApplication.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.application;

import adams.core.io.ConsoleHelper;
import adams.core.io.console.Lanterna;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.gui.application.AbstractInitialization;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.terminal.core.LogTextBox;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.logging.Level;

/**
 * Ancestor for terminal-based applications.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLanternaTerminalApplication
  extends AbstractTerminalApplication {

  private static final long serialVersionUID = 2187425015130568365L;

  /** the terminal in use. */
  protected Terminal m_Terminal;

  /** the screen in use. */
  protected Screen m_Screen;

  /** the GUI. */
  protected MultiWindowTextGUI m_GUI;

  /** the main window. */
  protected Window m_MainWindow;

  /**
   * Default constructor.
   */
  protected AbstractLanternaTerminalApplication() {
    super();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_MainWindow = null;
  }

  /**
   * Initializes the terminal.
   */
  protected void initTerminal() {
    DefaultTerminalFactory  factory;

    try {
      factory    = new DefaultTerminalFactory();
      factory.setTerminalEmulatorTitle(getDefaultApplicationTitle());
      m_Terminal = factory.createTerminal();
      m_Screen   = new TerminalScreen(m_Terminal);
      m_Screen.startScreen();
      m_GUI      = new MultiWindowTextGUI(m_Screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Returns the textbox to be used for logging.
   * <br>
   * Default implementation just returns null.
   *
   * @return		the textbox, null if not available
   */
  public LogTextBox getLogTextBox() {
    return null;
  }

  /**
   * Logs the message.
   *
   * @param msg		the message to log
   */
  public void logMessage(String msg) {
    if (getLogTextBox() != null)
      getLogTextBox().addLine(msg);
    else
      System.out.println(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   */
  public void logError(String msg) {
    if (getLogTextBox() != null)
      getLogTextBox().addLine(msg);
    else
      System.err.println(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   * @param t 		the exception
   */
  public void logError(String msg, Throwable t) {
    if (getLogTextBox() != null)
      getLogTextBox().addLine(msg + "\n" + LoggingHelper.throwableToString(t));
    else
      System.err.println(msg + "\n" + LoggingHelper.throwableToString(t));
  }

  /**
   * Finishes the initialization.
   */
  protected void finishTerminal() {
    RemoteScriptingEngine 	engine;

    AbstractInitialization.initAll();

    ConsoleHelper.useOther(new Lanterna(m_GUI, getLogTextBox()));
    LoggingHelper.setDefaultHandler(createLogHandler());

    if (!m_RemoteScriptingEngineCmdLine.isEmpty()) {
      try {
	engine = (RemoteScriptingEngine) OptionUtils.forAnyCommandLine(RemoteScriptingEngine.class, m_RemoteScriptingEngineCmdLine);
      }
      catch (Exception e) {
	engine = null;
	getLogger().log(
	  Level.SEVERE,
	  "Failed to instantiate remote scripting engine from commandline: '"
	    + m_RemoteScriptingEngineCmdLine + "'",
	  e);
      }
      if (engine != null)
	setRemoteScriptingEngine(engine);
    }
  }

  /**
   * Starts the application.
   */
  public void start() {
    if (m_MainWindow != null) {
      createTitle("");
      m_GUI.addWindowAndWait(m_MainWindow);
    }
  }

  /**
   * Stops the application.
   */
  public void stop() {
    try {
      if (m_MainWindow != null)
	m_MainWindow.close();
      m_Screen.stopScreen();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to stop screen!", e);
    }
  }
}
