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
 * DL4JModelToJson.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import net.minidev.json.JSONAware;
import org.deeplearning4j.nn.api.Model;

/**
 <!-- globalinfo-start -->
 * Converts a DL4J model into its JSON representation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JModelToJson
  extends AbstractConversion {

  private static final long serialVersionUID = -8817702395174849629L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a DL4J model into its JSON representation.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Model.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return JSONAware.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Object		result;
    Model		model;
    String		jsonStr;
    StringToJson	conv;
    String		msg;

    model   = (Model) m_Input;
    jsonStr = model.conf().toJson();
    conv = new StringToJson();
    conv.setInput(jsonStr);
    msg = conv.convert();
    if (msg != null)
      throw new Exception("Failed to parse JSON string from model!\n" + msg);
    else
      result = conv.getOutput();
    conv.cleanUp();

    return result;
  }
}
