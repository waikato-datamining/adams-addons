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
 * AbstractTrailReaderTestCase.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.trail.Trail;
import adams.test.TmpFile;

import java.util.List;

/**
 * Ancestor for trail reader tests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTrailReaderTestCase
  extends AbstractDataContainerReaderTestCase<AbstractTrailReader, Trail> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractTrailReaderTestCase(String name) {
    super(name);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @param ignored	the indices of lines to ignore
   * @return		true if successfully saved
   */
  @Override
  protected boolean save(List<Trail> data, String filename, int[] ignored) {
    StringBuilder	content;
    String		props;
    String[]		lines;
    int			i;

    content = new StringBuilder();

    for (Trail d: data) {
      content.append("---\n");
      if (d.hasReport()) {
	props = d.getReport().toProperties().toComment(true);
	lines = Utils.split(props, "\n");
	for (i = 1; i < lines.length; i++) {
	  if (i > 1)
	    content.append("\n");
	  content.append(lines[i]);
	}
      }
      content.append("Background: " + d.hasBackground() + "\n");
      if (d.hasBackground())
	content.append("Background dimensions: " + d.getBackground().getWidth() + "x" + d.getBackground().getHeight() + "\n");
      content.append(d.toSpreadSheet().toString());
      content.append("\n");
    }

    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), content, false);
  }
}
