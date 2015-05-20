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
 * AbstractHeatmapReader.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.ClassLister;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;

/**
 * Ancestor for readers that read heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapReader
  extends AbstractDataContainerReader<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -2206748744422806213L;

  /** whether to use absolute filename for the source report field or just the file's name. */
  protected boolean m_UseAbsoluteSource;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-absolute-source", "useAbsoluteSource",
	    getUseAbsoluteSource());
  }

  /**
   * Returns the default for using absolute source filename.
   *
   * @return		the default
   */
  protected boolean getDefaultUseAbsoluteSource() {
    return true;
  }

  /**
   * Sets whether to use absolute source filename rather than just name.
   *
   * @param value 	true if to use absolute source
   */
  public void setUseAbsoluteSource(boolean value) {
    m_UseAbsoluteSource = value;
    reset();
  }

  /**
   * Returns whether to use absolute source filename rather than just name.
   *
   * @return 		true if to use absolute source
   */
  public boolean getUseAbsoluteSource() {
    return m_UseAbsoluteSource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsoluteSourceTipText() {
    return
      "If enabled the source report field stores the absolute file name "
        + "rather than just the name.";
  }

  /**
   * For performing post-processing.
   */
  protected void postProcessData() {
    super.postProcessData();

    for (Heatmap map: m_ReadData) {
      // set filename
      if (map.hasReport()) {
	map.getReport().addField(new Field(Heatmap.FIELD_FILENAME, DataType.STRING));
        if (m_UseAbsoluteSource)
	  map.getReport().setStringValue(Heatmap.FIELD_FILENAME, m_Input.getAbsolutePath());
	else
	  map.getReport().setStringValue(Heatmap.FIELD_FILENAME, m_Input.getName());
      }
      // fix ID
      if (map.getID().trim().length() == 0)
	map.setID(m_Input.getName());
    }
  }

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(AbstractHeatmapReader.class);
  }
}
