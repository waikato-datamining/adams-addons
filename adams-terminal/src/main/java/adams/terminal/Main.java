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
 * Main.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.terminal;

import adams.core.logging.AbstractLogHandler;
import adams.core.logging.LoggingHelper;
import adams.core.logging.MultiHandler;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Environment;
import adams.terminal.application.AbstractLanternaTerminalApplication;
import adams.terminal.application.ApplicationMenu;
import adams.terminal.core.LogTextBox;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Main ADAMS application - terminal-based.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Main
  extends AbstractLanternaTerminalApplication {

  private static final long serialVersionUID = -6680605754144851435L;

  /**
   * The log handler for the application.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public static class LogHandler
    extends AbstractLogHandler {

    /** the owner. */
    protected Main m_Owner;

    public LogHandler(Main owner) {
      m_Owner = owner;
    }

    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * The <tt>Handler</tt>  is responsible for formatting the message, when and
     * if necessary.  The formatting should include localization.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    protected void doPublish(LogRecord record) {
      m_Owner.appendLog(LoggingHelper.assembleMessage(record).toString());
    }
  }

  /** the main panel. */
  protected Panel m_PanelMain;

  /** the menu bar. */
  protected Panel m_MenuBar;

  /** the textbox for the logging. */
  protected LogTextBox m_TextBoxLog;

  /** the button for clearing the log. */
  protected Button m_ButtonClear;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "ADAMS Application";
  }

  /**
   * Initializes the terminal.
   */
  @Override
  protected void initTerminal() {
    ApplicationMenu   	menu;
    Panel		panel;
    Panel		panelButtons;

    super.initTerminal();

    m_MainWindow = new BasicWindow();
    m_MainWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));

    m_PanelMain  = new Panel(new BorderLayout());

    menu         = new ApplicationMenu(this);
    m_MenuBar    = menu.getMenuBar(m_GUI);
    m_PanelMain.addComponent(m_MenuBar, Location.TOP);

    panel = new Panel(new BorderLayout());
    m_PanelMain.addComponent(panel, Location.CENTER);

    panel.addComponent(new EmptySpace(), Location.TOP);

    m_TextBoxLog = new LogTextBox(new TerminalSize(40, 10));
    panel.addComponent(m_TextBoxLog.withBorder(Borders.singleLine("Log")), Location.CENTER);

    panelButtons = new Panel(new LinearLayout(Direction.HORIZONTAL));
    panel.addComponent(panelButtons, Location.BOTTOM);

    m_ButtonClear = new Button("Clear", () -> m_TextBoxLog.clear());
    panelButtons.addComponent(m_ButtonClear);
  }

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected String getDefaultApplicationTitle() {
    return "ADAMS";
  }

  /**
   * Sets the title to use.
   *
   * @param value	the title
   */
  protected void setTitle(String value) {
    if (m_MenuBar != null)
      m_MainWindow.setComponent(m_PanelMain.withBorder(Borders.singleLine(value)));
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the textbox to be used for logging.
   *
   * @return		the textbox, null if not available
   */
  public LogTextBox getLogTextBox() {
    return m_TextBoxLog;
  }

  /**
   * Returns the log handler to use.
   *
   * @return		the handler
   */
  protected Handler createLogHandler() {
    MultiHandler result;

    result = new MultiHandler();
    result.setHandlers(new Handler[]{new LogHandler(this)});

    return result;
  }

  /**
   * Appends the log message.
   *
   * @param msg		the log message
   */
  public void appendLog(String msg) {
    m_TextBoxLog.addLine(msg);
    m_TextBoxLog.setCaretPosition(m_TextBoxLog.getLineCount(), 0);
  }

  /**
   * Starts the terminal application from commandline.
   *
   * @param args	the arguments
   * @throws Exception	if start up fails
   */
  public static void main(String[] args) throws Exception {
    runApplication(Environment.class, Main.class, args);
    System.exit(0);
  }
}
