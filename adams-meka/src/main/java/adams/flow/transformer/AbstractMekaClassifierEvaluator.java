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
 * AbstractMekaClassifierEvaluator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.flow.container.MekaResultContainer;

/**
 * Ancestor for transformers that evaluate Meka classifiers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5949 $
 */
public abstract class AbstractMekaClassifierEvaluator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7740799988980266316L;

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  public Class[] generates() {
    return new Class[]{MekaResultContainer.class};
  }
}
