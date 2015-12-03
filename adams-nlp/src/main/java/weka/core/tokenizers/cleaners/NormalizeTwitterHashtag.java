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
 * NormalizeTwitterHashtags.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

/**
 * Replaces all hashtags with the same dummy hashtag.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NormalizeTwitterHashtag
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -7758011723883830212L;

  /** the hashtag to replace all hashtags with. */
  public final static String HASHTAG = "#hashtag";

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Replaces all hashtags with the same dummy hashtag: " + HASHTAG;
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (token.startsWith("#"))
      return HASHTAG;
    return token;
  }
}
