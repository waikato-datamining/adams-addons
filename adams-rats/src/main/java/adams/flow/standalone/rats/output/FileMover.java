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
 * FileMover.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Moves files into the target directory after a specified time period.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileMover
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 5834627889486613248L;

  /** the target directory. */
  protected PlaceholderDirectory m_Target;

  /** the waiting period in msec before moving the files. */
  protected int m_WaitMove;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Moves files into the target directory after a specified time period.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "wait-move", "waitMove",
	    0, 0, null);

    m_OptionManager.add(
	    "target", "target",
	    new PlaceholderDirectory());
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "waitMove", getWaitMove(), "wait-move: ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    
    return result;
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
    
    if ((result == null) && (!getTarget().exists()))
      result = "Target directory does not exist: " + getTarget();
    if ((result == null) && (!getTarget().isDirectory()))
      result = "Target is not a directory: " + getTarget();
    
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
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String		result;
    PlaceholderFile[]	files;
    boolean		ok;
    
    result = null;
    files  = FileUtils.toPlaceholderFileArray(m_Input);
    
    if (result == null) {
      doWait(m_WaitMove);
      for (File file: files) {
	try {
	  ok = FileUtils.move(file, m_Target);
	  if (isLoggingEnabled())
	    getLogger().fine("Moving " + (ok ? "succeeded" : "failed") + ": " + file + " -> " + m_Target);
	}
	catch (Exception e) {
	  if (result == null)
	    result = "";
	  else
	    result += "\n";
	  result += handleException("Failed to move '" + file + "' to '" + m_Target + "'!", e);
	}
      }
    }
    
    return result;
  }
}
