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
 * DefaultBackground.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import com.kennycason.kumo.bg.Background;

/**
 * Generates no background, uses default.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultBackground
  extends AbstractBackground {

  private static final long serialVersionUID = 1020878496588449967L;

  @Override
  public String globalInfo() {
    return "Generates no background, uses default.";
  }

  /**
   * Generates the background.
   *
   * @param errors 	for collecting errors
   * @return		the background, null if none generated
   */
  @Override
  public Background generate(MessageCollection errors) {
    return null;
  }
}
