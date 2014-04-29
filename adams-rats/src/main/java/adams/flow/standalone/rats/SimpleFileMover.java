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
 * SimpleFileMover.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import java.util.logging.Level;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Simple file mover that moves all located files (according to regular expression) after the specified waiting period.<br/>
 * After the set poll waiting period, the process is repeated.<br/>
 * Can work as receiver or transmitter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-source &lt;adams.core.io.PlaceholderDirectory&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The directory to watch for incoming files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files must match.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-wait-move &lt;int&gt; (property: waitMove)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before moving the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-target &lt;adams.core.io.PlaceholderDirectory&gt; (property: target)
 * &nbsp;&nbsp;&nbsp;The directory to move the files to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-wait-poll &lt;int&gt; (property: waitPoll)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before polling again.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleFileMover
  extends AbstractThreadedReceiver
  implements Transmitter {

  /** for serialization. */
  private static final long serialVersionUID = -6321580761334049454L;

  /** the lister for listing the files. */
  protected DirectoryLister m_Lister;

  /** the target directory. */
  protected PlaceholderDirectory m_Target;

  /** the waiting period in msec before moving the files. */
  protected int m_WaitMove;

  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Simple file mover that moves all located files (according to "
	+ "regular expression) after the specified waiting period.\n"
	+ "After the set poll waiting period, the process is repeated.\n"
	+ "Can work as receiver or transmitter.";
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
	    "regexp", "regExp",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "wait-move", "waitMove",
	    0, 0, null);

    m_OptionManager.add(
	    "target", "target",
	    new PlaceholderDirectory());

    m_OptionManager.add(
	    "wait-poll", "waitPoll",
	    1000, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Lister = new DirectoryLister();
    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);
    m_Lister.setRecursive(false);
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The directory to watch for incoming files.";
  }

  /**
   * Sets the regular expression for the files.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_Lister.getRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the files must match.";
  }

  /**
   * Sets the number of milli-seconds to wait before moving the files.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitMove(int value) {
    if (value >= 0) {
      m_WaitMove = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before moving the files.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitMove() {
    return m_WaitMove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitMoveTipText() {
    return "The number of milli-seconds to wait before moving the files.";
  }

  /**
   * Sets the directory to move the files to.
   *
   * @param value	the target directory
   */
  public void setTarget(PlaceholderDirectory value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the directory to move the files to.
   *
   * @return		the target directory
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
    return "The directory to move the files to.";
  }

  /**
   * Sets the number of milli-seconds to wait before polling.
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
   * Returns the number of milli-seconds to wait before polling again.
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
    return "The number of milli-seconds to wait before polling again.";
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
    result += QuickInfoHelper.toString(this, "regExp", getRegExp(), ", regexp: ");
    result += QuickInfoHelper.toString(this, "waitMove", getWaitMove(), ", wait-move: ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    result += QuickInfoHelper.toString(this, "waitPoll", getWaitPoll(), ", wait-poll: ");
    
    return result;
  }
  
  /**
   * Hook method for performing checks. Makes sure that directories exist.
   * 
   * @throws Exception	if checks fail
   */
  @Override
  public void check() throws Exception {
    super.check();
    
    if (!getSource().exists())
      throw new IllegalStateException("Source directory does not exist: " + getSource());
    if (!getSource().isDirectory())
      throw new IllegalStateException("Source is not a directory: " + getSource());

    if (!getTarget().exists())
      throw new IllegalStateException("Target directory does not exist: " + getTarget());
    if (!getTarget().isDirectory())
      throw new IllegalStateException("Target is not a directory: " + getTarget());
  }

  /**
   * Returns the {@link Worker} instance that performs the actual receiving.
   * 
   * @return		the {@link Worker} instance to use
   */
  @Override
  protected Worker newWorker() {
    Worker	result;
    
    result = new Worker() {
      @Override
      protected void doRun() {
	String[]	files;
	boolean		ok;

	while (!m_Stopped) {
	  // poll
	  files = m_Lister.list();
	  if (isLoggingEnabled())
	    getLogger().fine("Files: " + Utils.flatten(files, ", "));

	  // move files
	  if (files.length > 0) {
	    // wait before moving
	    if (isLoggingEnabled())
	      getLogger().fine("Wait (move): " + m_WaitMove);
	    doWait(m_WaitMove);

	    // move files
	    for (String file: files) {
	      try {
		if (!(ok = FileUtils.move(new PlaceholderFile(file), m_Target)))
		  getLogger().severe("Failed to move '" + file + "' to '" + m_Target + "'!");
		if (isLoggingEnabled())
		  getLogger().info("Moving file '" + file + "': " + ok);
	      }
	      catch (Exception e) {
		getLogger().log(Level.SEVERE, "Failed to move '" + file + "' to '" + m_Target + "'!", e);
	      }
	    }
	  }

	  // wait before polling
	  if (isLoggingEnabled())
	    getLogger().fine("Wait (poll): " + m_WaitPoll);
	  doWait(m_WaitPoll);
	}
      }
    };
    
    return result;
  }
  
  /**
   * Performs the actual reception of data. Starts up the {@link Worker} 
   * instance in a thread.
   * 
   * @throws Execption	if receiving of data fails
   */
  protected void doTransmit() throws Exception {
    m_Worker = newWorker();
    new Thread(m_Worker).start();
  }

  /**
   * Starts the transmission of data.
   * 
   * @throws Execption	if transmitting of data fails
   */
  public void transmit() throws Exception {
    m_Stopped = false;
    check();
    doTransmit();
  }
}
