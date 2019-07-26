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
 * AbstractBackground.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import com.kennycason.kumo.bg.Background;

/**
 * Ancestor for WordCloud backgrounds.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBackground
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6777267021650265041L;

  /**
   * Generates the background.
   *
   * @param errors 	for collecting errors
   * @return		the background, null if none generated
   */
  public abstract Background generate(MessageCollection errors);
}
