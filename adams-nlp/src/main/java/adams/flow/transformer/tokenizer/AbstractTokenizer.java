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
 * AbstractTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.tokenizer;

import adams.core.option.AbstractOptionHandler;

import java.util.List;

/**
 * Ancestor for classes that split strings into words ("tokenize").
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11956 $
 */
public abstract class AbstractTokenizer
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5189696431656349861L;

  /**
   * Checks the string.
   * <br><br>
   * Default implementation only checks whether a string was provided.
   * 
   * @param str		the string to check
   */
  protected void check(String str) {
    if (str == null)
      throw new IllegalArgumentException("No string provided!");
  }
  
  /**
   * Performs the actual tokenization.
   * 
   * @param str		the string to tokenize
   * @return		the list of words
   */
  protected abstract List<String> doTokenize(String str);
  
  /**
   * Tokenizes the given string into words.
   * 
   * @param str		the string to process
   * @return		the generated words
   */
  public List<String> tokenize(String str) {
    check(str);
    return doTokenize(str);
  }
}
