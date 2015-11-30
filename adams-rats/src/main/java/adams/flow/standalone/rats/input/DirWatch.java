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
 * DirWatch.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.AtomicMoveSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.WatchEventKind;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Watches for file changes in a directory and forwards the affected files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-source &lt;adams.core.io.PlaceholderDirectory&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The directory to watch.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-event &lt;CREATE|MODIFY|DELETE&gt; [-event ...] (property: events)
 * &nbsp;&nbsp;&nbsp;The kind of events to report.
 * &nbsp;&nbsp;&nbsp;default: CREATE
 * </pre>
 * 
 * <pre>-wait-poll &lt;int&gt; (property: waitPoll)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before polling again whether files have 
 * &nbsp;&nbsp;&nbsp;arrived.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-wait-list &lt;int&gt; (property: waitList)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait after listing the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-move-files &lt;boolean&gt; (property: moveFiles)
 * &nbsp;&nbsp;&nbsp;If enabled, the files get moved to the specified directory first before 
 * &nbsp;&nbsp;&nbsp;being transmitted (with their new filename).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-atomic-move &lt;boolean&gt; (property: atomicMove)
 * &nbsp;&nbsp;&nbsp;If true, then an atomic move operation will be attempted (NB: not supported 
 * &nbsp;&nbsp;&nbsp;by all operating systems).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-target &lt;adams.core.io.PlaceholderDirectory&gt; (property: target)
 * &nbsp;&nbsp;&nbsp;The directory to move the files to before transmitting their names.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirWatch
  extends AbstractRatInput
  implements AtomicMoveSupporter {
  
  /** for serialization. */
  private static final long serialVersionUID = -6772954304997860394L;

  /** the directory to watch. */
  protected PlaceholderDirectory m_Source;

  /** the events to look for. */
  protected WatchEventKind[] m_Events;
  
  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /** the waiting period in msec after the files were picked up. */
  protected int m_WaitList;

  /** whether to move the files before transmitting them. */
  protected boolean m_MoveFiles;

  /** whether to perform an atomic move. */
  protected boolean m_AtomicMove;

  /** the directory to move the files to. */
  protected PlaceholderDirectory m_Target;

  /** the files that were picked up by the watch service. */
  protected List<String> m_Files;

  /** the watch service. */
  protected transient WatchService m_Watch;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Watches for file changes in a directory and forwards the affected files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "event", "events",
      new WatchEventKind[]{WatchEventKind.CREATE});

    m_OptionManager.add(
      "wait-poll", "waitPoll",
      50, 0, null);

    m_OptionManager.add(
      "wait-list", "waitList",
      0, 0, null);

    m_OptionManager.add(
      "move-files", "moveFiles",
      false);

    m_OptionManager.add(
      "atomic-move", "atomicMove",
      false);

    m_OptionManager.add(
      "target", "target",
      new PlaceholderDirectory());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Files = new ArrayList<String>();
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    stopWatchService();
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The directory to watch.";
  }

  /**
   * Sets the events to report.
   *
   * @param value	the events
   */
  public void setEvents(WatchEventKind[] value) {
    m_Events = value;
    reset();
  }

  /**
   * Returns the events to report.
   *
   * @return		the events
   */
  public WatchEventKind[] getEvents() {
    return m_Events;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String eventsTipText() {
    return "The kind of events to report.";
  }

  /**
   * Sets the number of milli-seconds to wait before polling whether files have arrived.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitPoll(int value) {
    if (value >= 0) {
      m_WaitPoll = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before polling again whether files have arrived.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitPoll() {
    return m_WaitPoll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitPollTipText() {
    return "The number of milli-seconds to wait before polling again whether files have arrived.";
  }

  /**
   * Sets the number of milli-seconds to wait after listing the files.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitList(int value) {
    if (value >= 0) {
      m_WaitList = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait after listing the files.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitList() {
    return m_WaitList;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitListTipText() {
    return "The number of milli-seconds to wait after listing the files.";
  }

  /**
   * Sets whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @param value	true if to move files
   */
  public void setMoveFiles(boolean value) {
    m_MoveFiles = value;
    reset();
  }

  /**
   * Returns whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @return		true if to move files
   */
  public boolean getMoveFiles() {
    return m_MoveFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String moveFilesTipText() {
    return 
	"If enabled, the files get moved to the specified directory first "
	+ "before being transmitted (with their new filename).";
  }

  /**
   * Sets whether to attempt atomic move operation.
   *
   * @param value	if true then attempt atomic move operation
   */
  public void setAtomicMove(boolean value) {
    m_AtomicMove = value;
    reset();
  }

  /**
   * Returns whether to attempt atomic move operation.
   *
   * @return 		true if to attempt atomic move operation
   */
  public boolean getAtomicMove() {
    return m_AtomicMove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String atomicMoveTipText() {
    return
        "If true, then an atomic move operation will be attempted "
	  + "(NB: not supported by all operating systems).";
  }

  /**
   * Sets the move-to directory.
   *
   * @param value	the move-to directory
   */
  public void setTarget(PlaceholderDirectory value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the move-to directory.
   *
   * @return		the move-to directory.
   */
  public PlaceholderDirectory getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The directory to move the files to before transmitting their names.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "source", getSource(), "source: ");
    result  = QuickInfoHelper.toString(this, "events", Utils.flatten(getEvents(), "/"), ", events: ");
    result += QuickInfoHelper.toString(this, "waitList", getWaitList(), ", wait-list: ");
    result += QuickInfoHelper.toString(this, "moveFiles", (getMoveFiles() ? "move" : "keep"), ", ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    
    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return String.class;
  }
  
  /**
   * Hook method for performing checks. Makes sure that directories exist.
   * 
   * @throws Exception	if checks fail
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();
    
    if ((result == null) && (!getSource().exists()))
      result = "Source directory does not exist: " + getSource();
    if ((result == null) && (!getSource().isDirectory()))
      result ="Source is not a directory: " + getSource();
    
    if (m_MoveFiles) {
      if ((result == null) && (!getTarget().exists()))
	result = "Target directory does not exist: " + getTarget();
      if ((result == null) && (!getTarget().isDirectory()))
	result ="Target is not a directory: " + getTarget();
    }
    
    return result;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String			result;
    WatchEvent.Kind<Path>[]	events;
    Path			dir;
    WatchKey 			key;
    WatchEvent.Kind 		kind;
    WatchEvent<Path> 		ev;
    Path 			name;
    Path			child;
    boolean 			valid;
    List<String>		files;
    int				i;
    PlaceholderFile		file;
    
    result = null;
    
    dir   = m_Source.toPath();
    files = new ArrayList<String>();
    
    // init watch service
    if (m_Watch == null) {
      try {
	events = new WatchEvent.Kind[m_Events.length];
	for (i = 0; i < m_Events.length; i++)
	  events[i] = m_Events[i].getEventKind();
	m_Watch = FileSystems.getDefault().newWatchService();
	dir.register(m_Watch, events);
      }
      catch (Exception e) {
	result = handleException("Failed to initialize watch service!", e);
      }
    }

    if (result == null) {
      while ((files.size() == 0) && !m_Stopped) {
	// wait for key to be signalled
	try {
	  key = m_Watch.poll(m_WaitPoll, TimeUnit.MILLISECONDS);
	  if (key == null)
	    continue;
	}
	catch (Exception e) {
	  result = handleException("Failed to obtain files!", e);
	  break;
	}
	
	// get events
	for (WatchEvent<?> event: key.pollEvents()) {
	  kind = event.kind();
	  if (kind == StandardWatchEventKinds.OVERFLOW)
            continue;
	  ev    = (WatchEvent<Path>) event;
          name  = ev.context();
          child = dir.resolve(name);
          files.add(child.toFile().getAbsolutePath());
	}
	
	// check whether directory still accessible
	valid = key.reset();
	if (!valid) {
	  result = "Directory " + m_Source + " no longer valid??";
	  break;
	}
      }
    }
    
    // move files?
    if (m_MoveFiles) {
      for (i = 0; i < files.size(); i++) {
	file = new PlaceholderFile(files.get(i));
	try {
	  if (!FileUtils.move(file, m_Target, m_AtomicMove))
	    result = "Failed to move '" + file + "' to '" + m_Target + "'!";
	  else
	    files.set(i, m_Target.getAbsolutePath() + File.separator + file.getName());
	}
	catch (Exception e) {
	  result = "Failed to move '" + file + "' to '" + m_Target + "': " + Utils.throwableToString(e);
	}
	if (result != null)
	  break;
      }
    }

    if (result == null)
      m_Files.addAll(files);

    return result;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Files.size() > 0);
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return m_Files.remove(0);
  }

  /**
   * Stops the watch service, if active.
   */
  protected void stopWatchService() {
    if (m_Watch != null) {
      if (isLoggingEnabled())
	getLogger().info("Stopping watch service...");
      try {
	m_Watch.close();
	if (isLoggingEnabled())
	  getLogger().info("Watch service stopped!");
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Stopping of watch service failed?", e);
      }
      m_Watch = null;
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    stopWatchService();
    super.stopExecution();
  }
}
