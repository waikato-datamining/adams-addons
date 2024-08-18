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
 * JepConsole.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.jep;

import adams.core.ShorteningType;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.core.scripting.JepScriptingEngine;
import adams.core.scripting.JepScriptlet;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.JepSyntaxEditorPanel;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesWithEncodingHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Console for editing and running Jep/Python scripts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepConsole
  extends BasePanel
  implements MenuBarProvider, TextSupplier, SendToActionSupporter, LoggingSupporter {

  /** the file to store the recent directories. */
  public final static String SESSION_FILE = "JepConsoleSession.props";

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the editor for the script. */
  protected JepSyntaxEditorPanel m_TextPanel;

  /** the new menu item. */
  protected JMenuItem m_MenuItemFileNew;

  /** the open menu item. */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemEditUndo;

  /** the redo menu item. */
  protected JMenuItem m_MenuItemEditRedo;

  /** the cut menu item. */
  protected JMenuItem m_MenuItemEditCut;

  /** the copy menu item. */
  protected JMenuItem m_MenuItemEditCopy;

  /** the paste menu item. */
  protected JMenuItem m_MenuItemEditPaste;

  /** the select all menu item. */
  protected JMenuItem m_MenuItemEditSelectAll;

  /** the find menu item. */
  protected JMenuItem m_MenuItemEditFind;

  /** the find next menu item. */
  protected JMenuItem m_MenuItemEditFindNext;

  /** the execute menu item. */
  protected JMenuItem m_MenuItemExecutionRun;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the recent files handler. */
  protected RecentFilesWithEncodingHandler<JMenu> m_RecentFilesHandler;

  /** the current file. */
  protected File m_CurrentFile;

  /** the current file encoding. */
  protected String m_CurrentEncoding;

  /** whether the content was modified. */
  protected boolean m_Modified;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** for managing undo/redo. */
  protected UndoManager m_Undo;

  /** the last search string used. */
  protected String m_LastFind;

  /** the listeners for modification events. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** for saving the content. */
  protected transient TextFileChooser m_FileChooser;

  /** the logger in use. */
  protected Logger m_Logger;

  /** whether a script is running. */
  protected boolean m_Running;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TitleGenerator     = new TitleGenerator("Jep/Python console", true, ShorteningType.START);
    m_RecentFilesHandler = null;
    m_CurrentFile        = null;
    m_CurrentEncoding    = null;
    m_Undo               = new UndoManager();
    m_ChangeListeners    = new HashSet<>();
    m_Running            = false;
    m_Logger             = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextPanel = new JepSyntaxEditorPanel();
    add(m_TextPanel, BorderLayout.CENTER);
  }

  /**
   * Adds the current file/encoding as recent item.
   */
  protected void addRecentItem() {
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(
	getCurrentFile().getAbsolutePath() + "\t" + getCurrentEncoding());
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // File/New
      menuitem = new JMenuItem("New", ImageManager.getIcon("new.gif"));
      menuitem.setMnemonic('N');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.setContent(""));
      menu.add(menuitem);
      m_MenuItemFileNew = menuitem;

	// File/Open
	menuitem = new JMenuItem("Open...", ImageManager.getIcon("open.gif"));
	menuitem.setMnemonic('O');
	menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
	menuitem.addActionListener((ActionEvent e) -> {
	  if (open())
	    addRecentItem();
	});
	menu.add(menuitem);
	m_MenuItemFileOpen = menuitem;

	// File/Recent files
	submenu = new JMenu("Open recent");
	menu.add(submenu);
	m_RecentFilesHandler = new RecentFilesWithEncodingHandler<>(SESSION_FILE, 5, submenu);
	m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,String>() {
	  @Override
	  public void recentItemAdded(RecentItemEvent<JMenu,String> e) {
	    // ignored
	  }
	  @Override
	  public void recentItemSelected(RecentItemEvent<JMenu,String> e) {
	    open(RecentFilesWithEncodingHandler.getFile(e.getItem()), RecentFilesWithEncodingHandler.getEncoding(e.getItem()));
	  }
	});
	m_MenuFileLoadRecent = submenu;

	// File/Save
	menuitem = new JMenuItem("Save", ImageManager.getIcon("save.gif"));
	menuitem.setMnemonic('a');
	menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
	menuitem.addActionListener((ActionEvent e) -> m_TextPanel.save());
	menu.add(menuitem);
	m_MenuItemFileSave = menuitem;

      // File/Save as
      menuitem = new JMenuItem("Save as...");
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.saveAs());
      menu.add(menuitem);
      m_MenuItemFileSaveAs = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close", ImageManager.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener((ActionEvent e) -> close());
      menu.add(menuitem);

      // Edit
      menu = new JMenu("Edit");
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menuitem.setMnemonic('U');
      menuitem.setEnabled(m_TextPanel.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(ImageManager.getIcon("undo.gif"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.undo());
      menu.add(menuitem);
      m_MenuItemEditUndo = menuitem;

      menuitem = new JMenuItem("Redo");
      menuitem.setMnemonic('R');
      menuitem.setEnabled(m_TextPanel.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
      menuitem.setIcon(ImageManager.getIcon("redo.gif"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.redo());
      menu.add(menuitem);
      m_MenuItemEditRedo = menuitem;

      // Edit/Cut
      menuitem = new JMenuItem("Cut", ImageManager.getIcon("cut.gif"));
      menuitem.setMnemonic('u');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed X"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.cut());
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditCut = menuitem;

      // Edit/Copy
      menuitem = new JMenuItem("Copy", ImageManager.getIcon("copy.gif"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.copy());
      menu.add(menuitem);
      m_MenuItemEditCopy = menuitem;

      // Edit/Paste
      menuitem = new JMenuItem("Paste", ImageManager.getIcon("paste.gif"));
      menuitem.setMnemonic('P');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed V"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.paste());
      menu.add(menuitem);
      m_MenuItemEditPaste = menuitem;

      // Edit/Select all
      menuitem = new JMenuItem("Select all", ImageManager.getEmptyIcon());
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed A"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.selectAll());
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditSelectAll = menuitem;

      // Edit/Find
      menuitem = new JMenuItem("Find", ImageManager.getIcon("find.gif"));
      menuitem.setMnemonic('F');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.find());
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditFind = menuitem;

      // Edit/Find next
      menuitem = new JMenuItem("Find next", ImageManager.getEmptyIcon());
      menuitem.setMnemonic('n');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed F"));
      menuitem.addActionListener((ActionEvent e) -> m_TextPanel.findNext());
      menu.add(menuitem);
      m_MenuItemEditFindNext = menuitem;

      // Execution
      menu = new JMenu("Execution");
      menu.setMnemonic('x');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Execution/Run
      menuitem = new JMenuItem("Run", ImageManager.getIcon("run.gif"));
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed R"));
      menuitem.addActionListener((ActionEvent e) -> runScript());
      menu.add(menuitem);
      m_MenuItemExecutionRun = menuitem;

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates title and menu items.
   */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;

    SwingUtilities.invokeLater(() -> {
      String title = m_TitleGenerator.generate(getCurrentFile(), m_TextPanel.isModified());
      setParentTitle(title);
    });
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    SwingUtilities.invokeLater(() -> {
      boolean contentAvailable = !m_TextPanel.getContent().isEmpty();
      m_MenuItemFileNew.setEnabled(contentAvailable);
      // File
      m_MenuItemFileSave.setEnabled(contentAvailable && isModified());
      m_MenuItemFileSaveAs.setEnabled(contentAvailable);
      // Edit
      m_MenuItemEditUndo.setEnabled(m_TextPanel.canUndo());
      m_MenuItemEditRedo.setEnabled(m_TextPanel.canRedo());
      m_MenuItemEditCut.setEnabled(m_TextPanel.canCut());
      m_MenuItemEditCopy.setEnabled(m_TextPanel.canCopy());
      m_MenuItemEditPaste.setEnabled(m_TextPanel.canPaste());
      m_MenuItemEditFind.setEnabled(contentAvailable);
      m_MenuItemEditFindNext.setEnabled(contentAvailable && (m_TextPanel.getLastFind() != null));
      // Execution
      m_MenuItemExecutionRun.setEnabled(contentAvailable && !isRunning());
    });
  }

  /**
   * Sets the content to display. Resets the modified state.
   *
   * @param value	the text
   */
  public void setContent(String value) {
    m_TextPanel.setContent(value);
    m_CurrentFile     = null;
    m_CurrentEncoding = null;
  }

  /**
   * Returns the content to display.
   *
   * @return		the text
   */
  public String getContent() {
    return m_TextPanel.getContent();
  }

  /**
   * Returns the currently loaded file.
   *
   * @return		the current file, null if none loaded
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Returns the current file encoding.
   *
   * @return		the current encoding, null if no file loaded
   */
  public String getCurrentEncoding() {
    return m_CurrentEncoding;
  }

  /**
   * Sets the modified state. If false, all edits are discarded and the
   * last search string reset as well.
   *
   * @param value 	if true then the content is flagged as modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
    if (!m_Modified)
      m_Undo.discardAllEdits();
    notifyChangeListeners();
  }

  /**
   * Returns whether the content has been modified.
   *
   * @return		true if the content was modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the content or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  public boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    result = !isModified();

    if (!result) {
      msg    = "Content not saved - save?";
      retVal = GUIHelper.showConfirmMessage(this, msg, "Content not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  saveAs();
	  result = !isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Pops up dialog to open a file.
   *
   * @return		true if successfully opened
   */
  public boolean open() {
    int		retVal;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return false;

    return open(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @return		true if successfully opened
   */
  public boolean open(File file) {
    return open(file, null);
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @param encoding	the encoding to use, use null or empty string for default UTF-8
   * @return		true if successfully opened
   */
  public boolean open(File file, String encoding) {
    boolean		result;
    List<String> content;

    if ((encoding == null) || encoding.isEmpty())
      encoding = "UTF-8";

    content = FileUtils.loadFromFile(file, encoding);
    result  = (content != null);
    if (result) {
      setContent(Utils.flatten(content, "\n"));
      setModified(false);
      m_CurrentFile     = file;
      m_CurrentEncoding = encoding;
    }

    notifyChangeListeners();

    return result;
  }

  /**
   * Pops up dialog to save the content in a file if no filename provided,
   * otherwise saves the .
   */
  public void save() {
    if (m_CurrentFile == null)
      saveAs();
    else
      save(m_CurrentFile, m_CurrentEncoding);
  }

  /**
   * Pops up dialog to save the content in a file.
   */
  public void saveAs() {
    int		retVal;

    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    save(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }

  /**
   * Saves the content under the specified file.
   *
   * @param file	the file to save the content in
   * @param encoding	the file encoding to use
   */
  protected void save(File file, String encoding) {
    String	msg;

    msg = FileUtils.writeToFileMsg(file.getAbsolutePath(), m_TextPanel.getContent(), false, encoding);
    if (msg != null) {
      GUIHelper.showErrorMessage(
	this, "Error saving content to file '" + file + "':\n" + msg);
    }
    else {
      m_CurrentFile     = file;
      m_CurrentEncoding = encoding;
      m_Modified        = false;
    }

    notifyChangeListeners();
  }

  /**
   * Removes all content. Does not reset the undos.
   */
  public void clear() {
    try {
      m_TextPanel.getDocument().remove(0, m_TextPanel.getDocument().getLength());
    }
    catch (Exception e) {
      // ignored
    }
    m_Modified = false;
    notifyChangeListeners();
  }

  /**
   * Closes the dialog, if possible.
   */
  protected void close() {
    if (m_TextPanel.checkForModified()) {
      if (getParentDialog() != null)
	getParentDialog().setVisible(false);
      else if (getParentFrame() != null)
	getParentFrame().setVisible(false);
    }

    closeParent();
  }

  /**
   * Checks whether an undo action is available.
   *
   * @return		true if an undo action is available
   */
  public boolean canUndo() {
    try {
      return m_Undo.canUndo();
    }
    catch (Exception ex) {
      return false;
    }
  }

  /**
   * Performs an undo, if possible.
   */
  public void undo() {
    try {
      // perform undo
      if (m_Undo.canUndo())
	m_Undo.undo();

      // last change undone?
      if (!m_Undo.canUndo())
	m_Modified = false;

      notifyChangeListeners();
    }
    catch (Exception ex) {
      // ignored
    }
  }

  /**
   * Checks whether a redo action is available.
   *
   * @return		true if a redo action is available
   */
  public boolean canRedo() {
    try {
      return m_Undo.canRedo();
    }
    catch (Exception ex) {
      return false;
    }
  }

  /**
   * Performs a redo, if possible.
   */
  public void redo() {
    try {
      if (m_Undo.canRedo()) {
	m_Undo.redo();
	m_Modified = true;
	notifyChangeListeners();
      }
    }
    catch (Exception ex) {
      // ignored
    }
  }

  /**
   * Checks whether text can be cut at the moment.
   *
   * @return		true if text is available for cutting
   */
  public boolean canCut() {
    return ((m_TextPanel.getTextArea().getSelectedText() != null));
  }

  /**
   * Cuts the currently selected text and places it on the clipboard.
   */
  public void cut() {
    m_TextPanel.getTextArea().cut();
    notifyChangeListeners();
  }

  /**
   * Checks whether text can be copied at the moment.
   *
   * @return		true if text is available for copying
   */
  public boolean canCopy() {
    return (m_TextPanel.getTextArea().getSelectedText() != null);
  }

  /**
   * Copies the currently selected text to the clipboard.
   */
  public void copy() {
    if (m_TextPanel.getTextArea().getSelectedText() == null)
      ClipboardHelper.copyToClipboard(m_TextPanel.getContent());
    else
      m_TextPanel.getTextArea().copy();
  }

  /**
   * Checks whether text can be pasted at the moment.
   *
   * @return		true if text is available for pasting
   */
  public boolean canPaste() {
    return (ClipboardHelper.canPasteStringFromClipboard());
  }

  /**
   * Pastes the text from the clipboard into the document.
   */
  public void paste() {
    m_TextPanel.getTextArea().paste();
    notifyChangeListeners();
  }

  /**
   * Selects all the text.
   */
  public void selectAll() {
    m_TextPanel.getTextArea().selectAll();
  }

  /**
   * Initiates a search.
   */
  public void find() {
    String	search;
    int		index;

    search = GUIHelper.showInputDialog(GUIHelper.getParentComponent(this), "Enter search string", m_LastFind);
    if (search == null)
      return;

    index = m_TextPanel.getContent().indexOf(search, m_TextPanel.getTextArea().getCaretPosition());
    if (index > -1) {
      m_LastFind = search;
      m_TextPanel.getTextArea().setCaretPosition(index + search.length());
      m_TextPanel.getTextArea().setSelectionStart(index);
      m_TextPanel.getTextArea().setSelectionEnd(index + search.length());
    }
    else {
      GUIHelper.showErrorMessage(this, "Search string '" + search + "' not found!");
    }

    notifyChangeListeners();
  }

  /**
   * Finds the next occurrence.
   */
  public void findNext() {
    int		index;

    index = m_TextPanel.getContent().indexOf(m_LastFind, m_TextPanel.getTextArea().getCaretPosition());
    if (index > -1) {
      m_TextPanel.getTextArea().setCaretPosition(index + m_LastFind.length());
      m_TextPanel.getTextArea().setSelectionStart(index);
      m_TextPanel.getTextArea().setSelectionEnd(index + m_LastFind.length());
    }
    else {
      GUIHelper.showErrorMessage(this, "Search string '" + m_LastFind + "' not found!");
    }

    notifyChangeListeners();
  }

  /**
   * Returns whether a script is currently running.
   *
   * @return		true if running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Executes the current script.
   */
  public void runScript() {
    final JepScriptlet	scriptlet;
    SwingWorker 	worker;

    if (m_TextPanel.getContent().isEmpty())
      return;

    if (isRunning()) {
      GUIHelper.showErrorMessage(this, "A script is currently running. Please wait for it to finish before executing another one!");
      return;
    }

    m_Running = true;
    update();

    scriptlet = new JepScriptlet("Jep/Python console", m_TextPanel.getContent());
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	JepScriptingEngine.getSingleton().add(scriptlet);
	while (!scriptlet.hasFinished())
	  Utils.wait(JepConsole.this, 1000, 100);
	return null;
      }
      @Override
      protected void done() {
	m_Running = false;
	update();
	if (scriptlet.hasLastError())
	  GUIHelper.showErrorMessage(JepConsole.this, "An error occurred executing the script:\n" + scriptlet.getLastError());
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Adds the given change listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the given change listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Sends an event to all change listeners.
   */
  protected void notifyChangeListeners() {
    ChangeEvent 	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return null;
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return getFileFilter();
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    return getContent();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{String.class, JTextComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return (SendToActionUtils.isAvailable(new Class[]{String.class, JTextComponent.class}, cls))
	     && !m_TextPanel.getContent().isEmpty();
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if ((SendToActionUtils.isAvailable(String.class, cls))) {
      result = m_TextPanel.getContent();
      if (((String) result).isEmpty())
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTextComponent.class, cls)) {
      if (!m_TextPanel.getContent().isEmpty())
	result = m_TextPanel;
    }

    return result;
  }

  /**
   * Returns the filter for Python files.
   *
   * @return		the filter
   */
  protected ExtensionFileFilter getFileFilter() {
    return new ExtensionFileFilter("Python", "py");
  }

  /**
   * Returns the file chooser and creates it if necessary.
   *
   * @return		the file chooser
   */
  protected TextFileChooser getFileChooser() {
    ExtensionFileFilter		filter;

    if (m_FileChooser == null) {
      m_FileChooser = new TextFileChooser();
      m_FileChooser.removeChoosableFileFilters();
      filter = getFileFilter();
      m_FileChooser.addChoosableFileFilter(filter);
      m_FileChooser.setFileFilter(filter);
      m_FileChooser.setDefaultExtension(filter.getExtensions()[0]);
    }

    return m_FileChooser;
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  @Override
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      m_Logger = LoggingHelper.getLogger(getClass());
    return m_Logger;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  @Override
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(getLogger(), Level.INFO);
  }
}
