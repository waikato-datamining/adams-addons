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
 * JavaCodeGeneratorProvider.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jooq;

import org.jooq.util.Generator;
import org.jooq.util.JavaGenerator;

/**
 <!-- globalinfo-start -->
 * Returns a Java code generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8697 $
 */
public class JavaCodeGeneratorProvider
  extends AbstractJOOQCodeGeneratorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1568635766676328761L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns a Java code generator.";
  }

  /**
   * Returns the actual code generator.
   * 
   * @return		the code generator
   */
  @Override
  protected Generator doGenerate() {
    return new JavaGenerator();
  }
}
