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
 * AbstractHeatmapFilterTestCase.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import java.util.List;

import adams.data.heatmap.Heatmap;
import adams.data.io.input.SpreadSheetHeatmapReader;
import adams.data.report.Report;
import adams.test.TmpFile;

/**
 * Ancestor for heatmap filter test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapFilterTestCase
  extends adams.data.filter.AbstractFilterTestCase<AbstractFilter, Heatmap> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractHeatmapFilterTestCase(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    String[]	files;
    
    super.setUp();
    
    files = getRegressionInputFiles();
    for (String file: files)
      m_TestHelper.copyResourceToTmp(file);
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    String[]	files;
    
    files = getRegressionInputFiles();
    for (String file: files)
      m_TestHelper.deleteFileFromTmp(file);

    super.tearDown();
  }
  
  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   */
  @Override
  protected Heatmap load(String filename) {
    SpreadSheetHeatmapReader	reader;
    List<Heatmap>		heatmaps;

    reader = new SpreadSheetHeatmapReader();
    reader.setInput(new TmpFile(filename));
    heatmaps = reader.read();
    
    if (heatmaps.size() > 0)
      return heatmaps.get(0);
    else
      return null;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  @Override
  protected boolean save(Heatmap data, String filename) {
    StringBuilder	content;
    Report		report;
    
    content = new StringBuilder(data.toIntensityString());
    content.append("\n");
    report  = data.getReport().getClone();
    report.setStringValue("Filename", "-");
    content.append(report.toString());
    
    return m_TestHelper.save(content.toString(), filename);
  }
}
