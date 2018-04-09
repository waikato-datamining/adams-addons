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
 * AbstractAudioInfoReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.audioinfo;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Compatibility;

import java.util.Map;

/**
 * Ancestor for info readers for audio data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAudioInfoReader
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8842035286778396740L;

  /**
   * The accepted input types.
   *
   * @return		the input types
   */
  public abstract Class[] accepts();

  /**
   * Performs checks before reading from the input.
   *
   * @param input	the input to read
   * @return		null if successful, otherwise error message
   */
  protected String check(Object input) {
    Compatibility	comp;

    if (input == null)
      return "No input data provided!";
    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{input.getClass()}, accepts()))
      return "Expected " + Utils.classesToString(accepts()) + ", but received: " + Utils.classToString(input.getClass());
    return null;
  }

  /**
   * Reads the info from the input.
   *
   * @param input	the input data
   * @return		the generated info
   * @throws Exception	if reading fails
   */
  protected abstract Map<String,Object> doRead(Object input) throws Exception;

  /**
   * Reads the info from the input.
   *
   * @param input	the input data
   * @return		the generated info
   * @throws Exception	if reading fails
   */
  public Map<String,Object> read(Object input) throws Exception {
    String	msg;

    msg = check(input);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doRead(input);
  }
}
