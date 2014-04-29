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
 * FtpTransmitter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

import org.apache.commons.net.ftp.FTPClient;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.FTPConnection;

/**
 <!-- globalinfo-start -->
 * Uses FTP to transmit the files found in the source directory.<br/>
 * Waits the specified interval (wait-move) before FTP-ing the files.<br/>
 * After the FTP transmission has finished, the files get moved to the specified 'processed' directory.<br/>
 * The transmitter waits a number of milli-seconds (wait-poll) before inspecting the source directory again.
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
 * <pre>-wait-ftp &lt;int&gt; (property: waitFTP)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before FTP-ing the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-remote-dir &lt;java.lang.String&gt; (property: remoteDir)
 * &nbsp;&nbsp;&nbsp;The FTP directory to upload the file to.
 * &nbsp;&nbsp;&nbsp;default: &#47;pub
 * </pre>
 * 
 * <pre>-processed &lt;adams.core.io.PlaceholderDirectory&gt; (property: processed)
 * &nbsp;&nbsp;&nbsp;The directory to move the files to after the FTP transmission.
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
public class FtpTransmitter
  extends AbstractThreadedTransmitter {

  /** for serialization. */
  private static final long serialVersionUID = 2038651999695207153L;

  /** the lister for listing the files. */
  protected DirectoryLister m_Lister;

  /** the directory to upload the file to. */
  protected String m_RemoteDir;

  /** the waiting period in msec before moving the files. */
  protected int m_WaitFTP;

  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /** the target directory. */
  protected PlaceholderDirectory m_Processed;

  /** the FTP connection to use. */
  protected FTPConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses FTP to transmit the files found in the source directory.\n"
	+ "Waits the specified interval (wait-move) before FTP-ing the files.\n"
	+ "After the FTP transmission has finished, the files get moved to the "
	+ "specified 'processed' directory.\n"
	+ "The transmitter waits a number of milli-seconds (wait-poll) before "
	+ "inspecting the source directory again.";
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
	    "wait-ftp", "waitFTP",
	    0, 0, null);

    m_OptionManager.add(
	    "remote-dir", "remoteDir",
	    "/pub");

    m_OptionManager.add(
	    "processed", "processed",
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
   * Sets the number of milli-seconds to wait before FTPing the files.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitFTP(int value) {
    if (value >= 0) {
      m_WaitFTP = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before FTPing the files.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitFTP() {
    return m_WaitFTP;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitFTPTipText() {
    return "The number of milli-seconds to wait before FTP-ing the files.";
  }

  /**
   * Sets the directory to move the files to.
   *
   * @param value	the processed directory
   */
  public void setProcessed(PlaceholderDirectory value) {
    m_Processed = value;
    reset();
  }

  /**
   * Returns the directory to move the files to.
   *
   * @return		the processed directory
   */
  public PlaceholderDirectory getProcessed() {
    return m_Processed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processedTipText() {
    return "The directory to move the files to after the FTP transmission.";
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
   * Sets the remote directory.
   *
   * @param value	the remote directory
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory.
   *
   * @return		the remote directory.
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The FTP directory to upload the file to.";
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
    result += QuickInfoHelper.toString(this, "waitFTP", getWaitFTP(), ", wait-ftp: ");
    result += QuickInfoHelper.toString(this, "removeDir", getRemoteDir(), ", remote dir: ");
    result += QuickInfoHelper.toString(this, "processed", getProcessed(), ", processed: ");
    result += QuickInfoHelper.toString(this, "waitPoll", getWaitPoll(), ", wait-poll: ");
    
    return result;
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Makes sure that directories exist.
   * Checks for {@link FTPConnection} actor.
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

    if (!getProcessed().exists())
      throw new IllegalStateException("Processed directory does not exist: " + getProcessed());
    if (!getProcessed().isDirectory())
      throw new IllegalStateException("Processed is not a directory: " + getProcessed());

    m_Connection = (FTPConnection) ActorUtils.findClosestType(m_Owner, FTPConnection.class);
    if (m_Connection == null)
      throw new IllegalStateException("No " + FTPConnection.class.getName() + " actor found!");
  }
  
  /**
   * Returns the {@link Worker} instance that performs the actual transmitting.
   * 
   * @return		the {@link Worker} instance to use
   */
  @Override
  protected Worker newWorker() {
    Worker	result;
    
    result = new Worker() {
      protected void ftp(String filename) {
	FTPClient		client;
	File			file;
	String			remotefile;
	BufferedInputStream	stream;

	file       = new PlaceholderFile(filename);
	remotefile = m_RemoteDir + "/" + file.getName();
	client     = m_Connection.getFTPClient();
	stream     = null;
	try {
	  if (isLoggingEnabled())
	    getLogger().info("Uploading " + file + " to " + remotefile);
	  stream = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
	  client.storeFile(remotefile, stream);
	  stream.close();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to ftp '" + file + "' to '" + remotefile + "'!", e);
	}
	finally {
	  if (stream != null) {
	    try {
	      stream.close();
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
      }
      
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
	    // wait before FTPing
	    if (isLoggingEnabled())
	      getLogger().fine("Wait (ftp): " + m_WaitFTP);
	    doWait(m_WaitFTP);

	    // FTP files
	    for (String file: files) {
	      ftp(file);
	    }
	    
	    // move files
	    for (String file: files) {
	      try {
		if (!(ok = FileUtils.move(new PlaceholderFile(file), m_Processed)))
		  getLogger().severe("Failed to move '" + file + "' to '" + m_Processed + "'!");
		if (isLoggingEnabled())
		  getLogger().info("Moving file '" + file + "': " + ok);
	      }
	      catch (Exception e) {
		getLogger().log(Level.SEVERE, "Failed to move '" + file + "' to '" + m_Processed + "'!", e);
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
}
