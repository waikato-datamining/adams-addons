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
 * NormalizeURLs.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

import java.util.regex.Pattern;

/**
 * Replaces all urls with the same dummy url.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NormalizeURLs
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -7758011723883830212L;

  /** the url to replace all urls with. */
  public final static String URL = "http://url";

  /** the pattern to match. */
  public final static String PATTERN = "((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)";

  /** the compiled pattern. */
  protected transient Pattern m_Pattern = null;

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Replaces all urls with the same dummy url: " + URL;
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
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (m_Pattern == null)
      m_Pattern = Pattern.compile(PATTERN);
    if (m_Pattern.matcher(token).matches())
      return URL;
    return token;
  }
}
