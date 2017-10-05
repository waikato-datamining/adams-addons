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
 * TweeboParser.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.core.io.CoNLLHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.management.ProcessUtils;
import adams.data.spreadsheet.SpreadSheet;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Parses the incoming tweet text with the TweeboParser and forwards the generated CoNLL data.<br>
 * <br>
 * For more information see:<br>
 * https:&#47;&#47;sourceforge.net&#47;projects&#47;tweeboparser&#47;<br>
 * http:&#47;&#47;ilk.uvt.nl&#47;conll&#47;#dataformat
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TweeboParser
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, then the CoNLL spreadsheets are output in an array rather than 
 * &nbsp;&nbsp;&nbsp;one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: script)
 * &nbsp;&nbsp;&nbsp;The script for running the TweeboParser.
 * &nbsp;&nbsp;&nbsp;default: run.sh
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TweeboParser
  extends AbstractArrayProvider {

  private static final long serialVersionUID = -7736014447541322982L;

  /** the tweebo parser shell script. */
  protected PlaceholderFile m_Script;

  /** for executing the tweebo parser. */
  protected transient CollectingProcessOutput m_ProcessOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Parses the incoming tweet text with the TweeboParser and forwards "
      + "the generated CoNLL data.\n\n"
      + "For more information see:\n"
      + "https://sourceforge.net/projects/tweeboparser/\n"
      + "http://ilk.uvt.nl/conll/#dataformat";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "script", "script",
      new PlaceholderFile("run.sh"));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, then the CoNLL spreadsheets are output in an array rather than one-by-one.";
  }

  /**
   * Sets the TweeboParser script to use.
   *
   * @param value	the script
   */
  public void setScript(PlaceholderFile value) {
    m_Script = value;
    reset();
  }

  /**
   * Returns the TweeboParser script to use.
   *
   * @return		the script
   */
  public PlaceholderFile getScript() {
    return m_Script;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scriptTipText() {
    return "The script for running the TweeboParser.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return SpreadSheet.class;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!m_Script.exists())
	result = "Parser script does not exist: " + m_Script;
      else if (m_Script.isDirectory())
	result = "Parser script points to a directory: " + m_Script;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		tweets;
    File		tmpFile;
    File		inFile;
    List<String>	lines;

    m_Queue.clear();

    if (m_InputToken.getPayload() instanceof String)
      tweets = new String[]{(String) m_InputToken.getPayload()};
    else
      tweets = (String[]) m_InputToken.getPayload();

    // save tweets to tmp file
    tmpFile = TempUtils.createTempFile("tweebo", ".txt");
    inFile  = new PlaceholderFile(tmpFile.getAbsolutePath() + ".predict");
    result  = FileUtils.saveToFileMsg(Arrays.asList(tweets), tmpFile, "UTF-8");

    // parse tweets
    if (result == null) {
      try {
	m_ProcessOutput = ProcessUtils.execute(
	  new String[]{
	    m_Script.getAbsolutePath(),
	    tmpFile.getAbsolutePath()
	  },
	  new PlaceholderDirectory(m_Script.getParentFile())
	);
	if (!m_ProcessOutput.hasSucceeded())
	  result = "Failed to execute TweeboParser: " + Utils.flatten(m_ProcessOutput.getCommand(), " ") + "\n" + m_ProcessOutput.getStdErr();
      }
      catch (Exception e) {
	result = handleException("Failed to execute TweeboParser: " + m_Script, e);
      }
      m_ProcessOutput = null;
    }

    // collect generated data
    if (result == null) {
      if (inFile.exists() && !inFile.isDirectory()) {
	lines = FileUtils.loadFromFile(inFile, "UTF-8");
	lines = CoNLLHelper.group(lines);
	for (String content: lines)
	  m_Queue.add(CoNLLHelper.parse(content));
      }
    }

    // clean up
    if (tmpFile.exists())
      FileUtils.delete(tmpFile);
    if (inFile.exists())
      FileUtils.delete(inFile);

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ProcessOutput != null)
      m_ProcessOutput.destroy();
    super.stopExecution();
  }
}
