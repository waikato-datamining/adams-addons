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
 * FtpUpload.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.FTPConnection;

/**
 <!-- globalinfo-start -->
 * Sends the incoming files to a FTP server.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-wait-ftp &lt;int&gt; (property: waitFTP)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before FTP-ing the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-remote-dir &lt;java.lang.String&gt; (property: remoteDir)
 * &nbsp;&nbsp;&nbsp;The FTP directory to upload the file(s) to.
 * &nbsp;&nbsp;&nbsp;default: &#47;pub
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FtpUpload
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = -641728833085302442L;

  /** the directory to upload the file to. */
  protected String m_RemoteDir;

  /** the waiting period in msec before FTPing the files. */
  protected int m_WaitFTP;

  /** the FTP connection to use. */
  protected FTPConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the incoming files to a FTP server.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "wait-ftp", "waitFTP",
	    0, 0, null);

    m_OptionManager.add(
	    "remote-dir", "remoteDir",
	    "/pub");
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
    return "The FTP directory to upload the file(s) to.";
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "waitFTP", getWaitFTP(), "wait-ftp: ");
    result += QuickInfoHelper.toString(this, "removeDir", getRemoteDir(), ", remote dir: ");
    
    return result;
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Checks for {@link FTPConnection} actor.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();

    if (result == null) {
      m_Connection = (FTPConnection) ActorUtils.findClosestType(m_Owner, FTPConnection.class);
      if (m_Connection == null)
	result = "No " + FTPConnection.class.getName() + " actor found!";
    }
    
    return result;
  }
  
  /**
   * Uploads the specified file to the FTP server.
   * 
   * @param filename	the file to upload
   * @return		null if successful, otherwise error message
   */
  protected String ftp(String filename) {
    String		result;
    FTPClient		client;
    File		file;
    String		remotefile;
    BufferedInputStream	stream;

    result     = null;
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
      result = handleException("Failed to ftp '" + file + "' to '" + remotefile + "'!", e);
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
    return result;
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    String[]	files;
    
    result = null;
    files  = FileUtils.toStringArray(m_Input);
    doWait(m_WaitFTP);
    for (String file: files) {
      result = ftp(file);
      if (result != null)
	break;
    }
    
    return result;
  }
}
