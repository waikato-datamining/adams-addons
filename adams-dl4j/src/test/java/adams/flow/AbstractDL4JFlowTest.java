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
 * AbstractDL4JFlowTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow;

/**
 * Runs the test only if DL4J can run on the machine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDL4JFlowTest
  extends AbstractFlowTest {

  /** whether DL4J can be run. */
  protected static Boolean m_CanRunDL4J;

  /**
   * Constructs the <code>AbstractFlowTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractDL4JFlowTest(String name) {
    super(name);
  }

  /**
   * Dumps the actor, in case of an error.
   *
   * @throws Throwable		any test failure
   */
  @Override
  public void runBare() throws Throwable {
    if (m_CanRunDL4J == null) {
      try {
	new org.nd4j.linalg.factory.Nd4j();
	m_CanRunDL4J = true;
      }
      catch (Throwable t) {
	m_CanRunDL4J = false;
      }

      System.out.println("Can run DL4J: " + m_CanRunDL4J);
    }

    if (m_CanRunDL4J)
      super.runBare();
  }
}
