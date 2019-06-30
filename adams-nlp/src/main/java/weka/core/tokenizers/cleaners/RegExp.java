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
 * RegExp.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

import adams.core.net.HtmlUtils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Cleans tokens based on regular expressions, i.e., if token matches regexp
 * it gets replaced with the specified expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExp
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -1815343837519097597L;

  public static final String FIND = "find";

  public static final String REPLACE = "replace";

  /** the regular expression to use. */
  protected String m_Find = getDefaultFind();

  /** the replacement to use. */
  protected String m_Replace = getDefaultReplace();

  /** the compiled pattern. */
  protected transient Pattern m_Pattern = null;

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Cleans tokens based on regular expressions, i.e., if token "
      + "matches regexp, it gets replaced with the specified expression.\n\n"
      + "For more information see:\n"
      + HtmlUtils.toJavaApiURL(Pattern.class);
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, findTipText(), "" + getDefaultFind(), FIND);
    WekaOptionUtils.addOption(result, replaceTipText(), "" + getDefaultReplace(), REPLACE);
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
    setFind(WekaOptionUtils.parse(options, FIND, getDefaultFind()));
    setReplace(WekaOptionUtils.parse(options, REPLACE, getDefaultReplace()));
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
    WekaOptionUtils.add(result, FIND, getFind());
    WekaOptionUtils.add(result, REPLACE, getReplace());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Resets the cleaner.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Pattern = null;
  }

  /**
   * Returns the default regular expression for finding tokens to clean.
   *
   * @return		the default
   */
  protected String getDefaultFind() {
    return ".*";
  }

  /**
   * Sets the regular expression to use for finding tokens to clean.
   *
   * @param value	the regexp
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the regular expression to use for finding tokens to clean.
   *
   * @return		the regexp
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The regular expression to use for finding tokens to clean.";
  }

  /**
   * Returns the default expression for replacing matching tokens with.
   *
   * @return		the default
   */
  protected String getDefaultReplace() {
    return "";
  }

  /**
   * Sets the expression to use for replacing matching tokens with.
   *
   * @param value	the expression
   */
  public void setReplace(String value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the expression to use for replacing matching tokens with.
   *
   * @return		the expression
   */
  public String getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The expression to use for replacing matching tokens with; "
      + "empty string results in removing a matching token completely.";
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (m_Pattern == null)
      m_Pattern = Pattern.compile(m_Find);

    if (m_Pattern.matcher(token).matches()) {
      if (m_Replace.isEmpty())
	return null;
      else
	return token.replaceAll(m_Find, m_Replace);
    }

    return token;
  }
}
