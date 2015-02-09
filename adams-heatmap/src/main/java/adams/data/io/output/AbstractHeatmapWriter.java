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
 * AbstractHeatmapWriter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.ClassLister;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.util.List;

/**
 * Ancestor for writers that output heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapWriter
  extends AbstractDataContainerWriter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2897884035699563232L;
  
  /**
   * Returns whether writing of multiple containers is supported.
   * 
   * @return 		true if multiple containers are supported
   */
  @Override
  public boolean canWriteMultiple() {
    return false;
  }

  /**
   * The default implementation only checks whether the provided file is an
   * actual file and whether it exists (if m_OutputIsFile is TRUE). Otherwise
   * the directory has to exist.
   *
   * @param data	the data to write
   */
  protected void checkData(List<Heatmap> data) {
    Report	meta;

    super.checkData(data);

    for (Heatmap map: data) {
      meta = map.getReport().getClone();
      if (!meta.hasValue("Height"))
	meta.setValue(new Field("Height", DataType.NUMERIC), map.getHeight());
      if (!meta.hasValue("Width"))
	meta.setValue(new Field("Width", DataType.NUMERIC), map.getWidth());
      map.setReport(meta);
    }
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(AbstractHeatmapWriter.class);
  }
}
