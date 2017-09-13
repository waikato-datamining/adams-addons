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
 * CoNLLFileReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.core.io.CoNLLHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads files in CoNLL format.<br>
 * <br>
 * For more information see:<br>
 * http:&#47;&#47;ilk.uvt.nl&#47;conll&#47;#dataformat
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: CoNLLFileReader
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
 * &nbsp;&nbsp;&nbsp;If enabled, an array of spreadsheets is output instead of one by one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CoNLLFileReader
  extends AbstractArrayProvider {

  private static final long serialVersionUID = 5212193701591968782L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads files in CoNLL format.\n\n"
      + "For more information see:\n"
      + "http://ilk.uvt.nl/conll/#dataformat";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, an array of spreadsheets is output instead of one by one.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<String>	lines;
    PlaceholderFile 	file;

    result = null;

    file = null;
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      result = "Unhandled input type: " + Utils.classToString(m_InputToken.getPayload());

    if (result == null) {
      if (!file.exists())
        result = "File does not exist: " + file;
      else if (file.isDirectory())
        result = "File points to a directory: " + file;
    }

    if (result == null) {
      m_Queue.clear();
      lines = FileUtils.loadFromFile(file);
      lines = CoNLLHelper.group(lines);
      for (String content : lines)
        m_Queue.add(CoNLLHelper.parse(content));
    }

    return result;
  }
}
