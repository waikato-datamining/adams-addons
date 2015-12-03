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
 * Twokenize.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.tokenizer;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses TweetNLP's Twokenize.<br>
 * <br>
 * For more details on the tokenizer see:<br>
 * https:&#47;&#47;github.com&#47;brendano&#47;ark-tweet-nlp&#47;blob&#47;master&#47;src&#47;cmu&#47;arktweetnlp&#47;Twokenize.java
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class Twokenize
  extends AbstractTokenizer {

  /** for serialization. */
  private static final long serialVersionUID = 4043221889853222507L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses TweetNLP's Twokenize.\n\n"
      + "For more details on the tokenizer see:\n"
      + "https://github.com/brendano/ark-tweet-nlp/blob/master/src/cmu/arktweetnlp/Twokenize.java";
  }

  /**
   * Performs the actual tokenization.
   * 
   * @param str		the string to tokenize
   * @return		the list of sentence words
   */
  @Override
  protected List<String> doTokenize(String str) {
    return cmu.arktweetnlp.Twokenize.tokenizeRawTweetText(str);
  }
}
