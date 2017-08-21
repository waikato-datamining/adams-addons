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
 * BrainScriptHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BrainScriptHelper {

  /**
   * Adds the parameters to the definition.
   *
   * @param def		the current definition
   * @param params	the parameters to inject at the end
   * @param newLine	if new line, no comma is inserted
   * @return		the updated definition
   */
  public static String addParam(String def, String params, boolean newLine) {
    String	result;

    if (def.trim().endsWith("}")) {
      result = def.substring(0, def.lastIndexOf('}'));
      if (!result.trim().endsWith("{") && !newLine)
        result += ", ";
      result += params;
      if ((newLine) && !params.endsWith("\n"))
        result += "\n";
      result += def.substring(def.lastIndexOf('}'));
    }
    else {
      result = def;
      if (result.trim().length() > 0)
        result += newLine ? "\n" : ", ";
      result += params;
    }

    return result;
  }

  /**
   * Sets the output dimension for the layer.
   *
   * @param def		the current layer definition
   * @param outDim	the output dimension
   * @return		the updated definition
   */
  public static String setOutDim(String def, int outDim) {
    return setOutDim(def, "" + outDim);
  }

  /**
   * Sets the output dimension for the layer.
   *
   * @param def		the current layer definition
   * @param outDim	the output dimension
   * @return		the updated definition
   */
  public static String setOutDim(String def, String outDim) {
    String	result;
    String	rest;

    result = def.substring(0, def.indexOf('{') + 1)
      + outDim;
    rest = def.substring(def.indexOf('{') + 1);
    if (!rest.trim().startsWith("}"))
      result += ", ";
    result += rest;

    return result;
  }
}
