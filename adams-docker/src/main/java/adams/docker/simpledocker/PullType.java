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
 * PullType.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

/**
 * How to handle pulling of images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum PullType {
  DEFAULT(""),
  NEVER("never"),
  MISSING("missing"),
  ALWAYS("always");

  /** the underlying type string. */
  private String m_Type;

  /**
   * Initializes the enum item.
   *
   * @param type	the type string to manage
   */
  private PullType(String type) {
    m_Type = type;
  }

  /**
   * Returns the underlying type string.
   *
   * @return		the type string
   */
  public String getType() {
    return m_Type;
  }
}
