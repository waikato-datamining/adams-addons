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
 * AbstractFFmpegPlugin.java
 * Copyright (C) 2012-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.ffmpeg;

import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.sink.FFmpeg;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Ancestor for {@link FFmpeg} plugins.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFFmpegPlugin
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2555683041357914117L;

  /** the owning actor. */
  protected FFmpeg m_Owner;
  
  /** the input file. */
  protected PlaceholderFile m_Input;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Owner = null;
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Input = null;
  }
  
  /**
   * Sets the owner.
   * 
   * @param owner	the owner
   */
  public void setOwner(FFmpeg owner) {
    m_Owner = owner;
  }
  
  /**
   * Sets the owner.
   * 
   * @return		the owner
   */
  public FFmpeg getOwner() {
    return m_Owner;
  }

  /**
   * Sets the input file.
   * 
   * @param value	the input file
   */
  public void setInput(PlaceholderFile value) {
    m_Input = value;
  }
  
  /**
   * Returns the input file.
   * 
   * @return		the input file, null if none set
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the ffmpeg executable.
   * 
   * @return		the executable, null if not available or no owner set
   */
  protected PlaceholderFile getExecutable() {
    if ((m_Owner == null) || (m_Owner.getConfig() == null))
      return null;
    else
      return m_Owner.getConfig().getExecutable();
  }

  /**
   * Returns a quick info about the plugin, which will be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks the configuration.
   * <br><br>
   * Default implementation only checks whether owner is set.
   * 
   * @see #getOwner()
   * 
   * @return		null if setup ok, otherwise error message
   */
  public String setUp() {
    String	result;
    
    result = null;
    
    if (m_Owner == null)
      result = "No owner set!";
    
    return result;
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a 
   * combination of both of them as string.
   * 
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  protected String handleException(String msg, Throwable t) {
    return LoggingHelper.handleException(this, msg, t);
  }
  
  /**
   * Assembles the input command-line options.
   * 
   * @return		the command-line
   */
  protected abstract String assembleInputOptions();
  
  /**
   * Assembles the ouput command-line options.
   * 
   * @return		the command-line
   */
  protected abstract String assembleOutputOptions();
  
  /**
   * Performs the ffmpeg execution.
   * 
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;
    ArrayList<String>	options;
    String		cmd;
    CollectingProcessOutput proc;
    
    result = null;
    
    if (m_Input == null)
      result = "No input file set!";
    
    if (result == null) {
      options = new ArrayList<>();
      try {
	// executable
	options.add(getExecutable().getAbsolutePath());
	// always overwrite output files
	options.add("-y");
	// input options
	cmd = assembleInputOptions();
	if (cmd.length() > 0)
	  options.addAll(Arrays.asList(OptionUtils.splitOptions(cmd)));
	options.add("-i");
	options.add(m_Input.getAbsolutePath());
	// output options
	cmd = assembleOutputOptions();
	if (cmd.length() > 0)
	  options.addAll(Arrays.asList(OptionUtils.splitOptions(cmd)));
	options.add(getOwner().getOutputFile().getAbsolutePath());
	// execute command
	if (isLoggingEnabled())
	  getLogger().info("Command-line: " + Utils.flatten(options, " "));
	proc = ProcessUtils.execute(options.toArray(new String[options.size()]));
	if (!proc.hasSucceeded())
	  result = ProcessUtils.toErrorOutput(proc);
      }
      catch (Exception e) {
	result = handleException("Failed to execute commandline:\n" + Utils.flatten(options, "\n"), e);
      }
    }
    
    m_Input = null;
    
    return result;
  }
}
