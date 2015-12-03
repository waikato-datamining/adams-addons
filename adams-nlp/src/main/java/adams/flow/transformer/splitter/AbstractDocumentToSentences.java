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
 * AbstractSentenceSplitter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitter;

import java.util.List;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that split document strings into sentences.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public abstract class AbstractDocumentToSentences
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5189696431656349861L;

  /**
   * Checks the document.
   * <br><br>
   * Default implementation only checks whether a document string was provided.
   * 
   * @param doc		the document to check
   */
  protected void check(String doc) {
    if (doc == null)
      throw new IllegalArgumentException("No document provided!");
  }
  
  /**
   * Performs the actual splitting.
   * 
   * @param doc		the document to split
   * @return		the list of sentence strings
   */
  protected abstract List<String> doSplit(String doc);
  
  /**
   * Splits the given document string into sentences.
   * 
   * @param doc		the document to process
   * @return		the generated sentences
   */
  public List<String> split(String doc) {
    check(doc);
    return doSplit(doc);
  }
}
