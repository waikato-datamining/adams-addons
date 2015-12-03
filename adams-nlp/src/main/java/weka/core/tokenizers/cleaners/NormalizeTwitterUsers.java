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
 * NormalizeTwitterUsers.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

/**
 * Replaces all users with the same dummy user.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NormalizeTwitterUsers
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -7758011723883830212L;

  /** the user to replace all users with. */
  public final static String USER = "@user";

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Replaces all users with the same dummy user: " + USER;
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (token.startsWith("@"))
      return USER;
    return token;
  }
}
