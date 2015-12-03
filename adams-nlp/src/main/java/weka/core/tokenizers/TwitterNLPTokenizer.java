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
 * TwitterNLPTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import cmu.arktweetnlp.Twokenize;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.core.tokenizers.cleaners.PassThrough;
import weka.core.tokenizers.cleaners.TokenCleaner;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Tokenizer using TweetNLP's Twokenize.
 * Taken from <a href="https://github.com/felipebravom/SentimentDomain/blob/master/src/weka/core/tokenizers/TwitterNLPTokenizer.java">here</a>
 *
 * @author Felipe Bravo
 * @version $Revision$
 */
public class TwitterNLPTokenizer extends Tokenizer {

  private static final long serialVersionUID = 4352757127093531518L;

  public static final String CLEANER = "cleaner";

  public static final String USE_LOWER_CASE = "use-lower-case";

  /** the iterator for the tokens. */
  protected transient Iterator<String> m_TokenIterator;

  /** whether to lower-case the tweet. */
  protected boolean m_UseLowerCase = false;

  /** the cleaner to use. */
  protected TokenCleaner m_Cleaner = getDefaultCleaner();

  /**
   * Returns a string describing the tokenizer.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public String globalInfo() {
    return "Tokenizer based on TwitterNLP's Twokenize.\n\n"
      + "For more information see:\n"
      + "http://www.ark.cs.cmu.edu/TweetNLP/\n\n"
      + "Original code from:\n"
      + "https://github.com/felipebravom/SentimentDomain/blob/master/src/weka/core/tokenizers/TwitterNLPTokenizer.java";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addFlag(result, useLowerCaseTipText(), USE_LOWER_CASE);
    WekaOptionUtils.addOption(result, cleanerTipText(), getDefaultCleaner().getClass().getName(), CLEANER);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setUseLowerCase(Utils.getFlag(USE_LOWER_CASE, options));
    setCleaner((TokenCleaner) WekaOptionUtils.parse(options, CLEANER, getDefaultCleaner()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, USE_LOWER_CASE, getUseLowerCase());
    WekaOptionUtils.add(result, CLEANER, getCleaner());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets whether to use lower case.
   *
   * @param value	true if to use lower case
   */
  public void setUseLowerCase(boolean value) {
    m_UseLowerCase = value;
  }

  /**
   * Returns whether to use lower case.
   *
   * @return		true if to use lower case
   */
  public boolean getUseLowerCase() {
    return m_UseLowerCase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useLowerCaseTipText() {
    return "If enabled, the tweet is converted to lower case before tokenized.";
  }

  /**
   * Returns the default token cleaner.
   *
   * @return		the default
   */
  protected TokenCleaner getDefaultCleaner() {
    return new PassThrough();
  }

  /**
   * Sets the token cleaner to use.
   *
   * @param value	the cleaner
   */
  public void setCleaner(TokenCleaner value) {
    m_Cleaner = value;
  }

  /**
   * Returns the token cleaner to use.
   *
   * @return		the cleaner
   */
  public TokenCleaner getCleaner() {
    return m_Cleaner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanerTipText() {
    return "The token cleaner to use (after optional lower case).";
  }

  /**
   * Tests if this enumeration contains more elements.
   *
   * @return true if and only if this enumeration object contains at least one
   *         more element to provide; false otherwise.
   */
  @Override
  public boolean hasMoreElements() {
    return m_TokenIterator.hasNext();
  }

  /**
   * Returns the next element of this enumeration if this enumeration object has
   * at least one more element to provide.
   *
   * @return the next element of this enumeration.
   */
  @Override
  public String nextElement() {
    return m_TokenIterator.next();
  }

  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   *
   * @param s the string to tokenize
   */
  @Override
  public void tokenize(String s) {
    if (m_UseLowerCase)
      s = s.toLowerCase();
    List<String> words = Twokenize.tokenizeRawTweetText(s);
    if (!(m_Cleaner instanceof PassThrough)) {
      List<String> clean = new ArrayList<>();
      for (String word: words) {
	word = m_Cleaner.clean(word);
	if (word != null)
	  clean.add(word);
      }
      words = clean;
    }
    m_TokenIterator = words.iterator();
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10203 $");
  }

  /**
   * Runs the tokenizer with the given options and strings to tokenize. The
   * tokens are printed to stdout.
   *
   * @param args the commandline options and strings to tokenize
   */
  public static void main(String[] args) {
    runTokenizer(new TwitterNLPTokenizer(), args);
  }
}
