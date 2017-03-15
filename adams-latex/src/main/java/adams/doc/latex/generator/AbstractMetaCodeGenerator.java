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
 * AbstractMetaCodeGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for generators that enhance another base generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaCodeGenerator
  extends AbstractCodeGenerator {

  private static final long serialVersionUID = 8498377917687585899L;
  
  /** the base generator to use. */
  protected AbstractCodeGenerator m_Generator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      getDefaultGenerator());
  }

  /**
   * Returns the default code generator to use.
   *
   * @return		the default
   */
  protected abstract AbstractCodeGenerator getDefaultGenerator();

  /**
   * Sets the base generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractCodeGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the base generator to use.
   *
   * @return		the generator
   */
  public AbstractCodeGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String generatorTipText();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }
}
