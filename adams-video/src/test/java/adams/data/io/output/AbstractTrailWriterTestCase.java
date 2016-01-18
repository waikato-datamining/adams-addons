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
 * AbstractTrailWriterTestCase.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.data.io.input.SimpleTrailReader;
import adams.data.trail.Trail;
import adams.test.TmpFile;

import java.util.List;

/**
 * Ancestor for trail writer tests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailWriterTestCase
  extends AbstractDataContainerWriterTestCase<AbstractTrailWriter, Trail> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractTrailWriterTestCase(String name) {
    super(name);
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   */
  @Override
  protected Trail load(String filename) {
    List<Trail> 	result;
    SimpleTrailReader	reader;

    m_TestHelper.copyResourceToTmp(filename);

    reader = new SimpleTrailReader();
    reader.setInput(new TmpFile(filename));
    result = reader.read();

    m_TestHelper.deleteFileFromTmp(filename);

    if ((result != null) && (result.size() > 0))
      return result.get(0);
    else
      return null;
  }
}
