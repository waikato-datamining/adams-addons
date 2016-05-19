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
 * AbstractDL4JModelEvaluator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.flow.container.DL4JEvaluationContainer;

/**
 * Ancestor for transformers that evaluate models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDL4JModelEvaluator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7740799988980266316L;

  /** whether to always use a container. */
  protected boolean m_AlwaysUseContainer;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "always-use-container", "alwaysUseContainer",
	    false);
  }

  /**
   * Sets whether to always use an evaluation container as output.
   *
   * @param value	true if to always use container
   */
  public void setAlwaysUseContainer(boolean value) {
    m_AlwaysUseContainer = value;
    reset();
  }

  /**
   * Returns whether to always use an evaluation container as output.
   *
   * @return		true if to always use container
   */
  public boolean getAlwaysUseContainer() {
    return m_AlwaysUseContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alwaysUseContainerTipText() {
    return
        "If enabled, always outputs an evaluation container.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  public Class[] generates() {
    if (m_AlwaysUseContainer)
      return new Class[]{DL4JEvaluationContainer.class};
    else
      return new Class[]{String.class};
  }
}
