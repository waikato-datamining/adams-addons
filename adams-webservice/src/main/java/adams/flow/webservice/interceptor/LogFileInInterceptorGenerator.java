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
 * LogFileInInterceptorGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import adams.core.io.PlaceholderFile;

/**
 * Generator for {@link LogFileInInterceptor}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogFileInInterceptorGenerator
  extends AbstractInInterceptorGenerator<LogFileInInterceptor> {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a " + LogFileInInterceptor.class.getName() + " instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "log-file", "logFile",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the log file to write to.
   * 
   * @param value	the file to write to
   */
  public void setLogFile(PlaceholderFile value) {
    m_LogFile = value;
    reset();
  }
  
  /**
   * Returns the log file to write to.
   * 
   * @return		the file to write to
   */
  public PlaceholderFile getLogFile() {
    return m_LogFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logFileTipText() {
    return "The log file to write the collected data to.";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected LogFileInInterceptor doGenerate() {
    LogFileInInterceptor	result;
    
    result = new LogFileInInterceptor();
    result.setLogFile(getLogFile());
    
    return result;
  }
}
