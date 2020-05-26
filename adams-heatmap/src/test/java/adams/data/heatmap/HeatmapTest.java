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
 * HeatmapTest.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.data.heatmap;

import adams.core.classmanager.ClassManager;
import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.heatmap.Heatmap class. Run from commandline with: <br><br>
 * java adams.data.heatmap.HeatmapTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HeatmapTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapTest(String name) {
    super(name);
  }

  /**
   * Tests the size() method.
   */
  public void testSize() {
    Heatmap map = new Heatmap(10, 10);
    assertEquals("size differs from dimensions", 10*10, map.size());
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		the class to test
   */
  @Override
  protected void performSerializableTest(Class cls) {
    assertNotNull("Serialization failed", ClassManager.getSingleton().deepCopy(new Heatmap(10, 10)));
  }

  /**
   * Tests the get(int)/set(int) methods.
   */
  public void testGetSetByPosition() {
    Heatmap map = new Heatmap(10, 10);
    map.set(1, 2, 2.0);
    assertEquals("value differs", 2.0, map.get(12));
    map.set(12, 3.0);
    assertEquals("value differs", 3.0, map.get(12));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HeatmapTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
