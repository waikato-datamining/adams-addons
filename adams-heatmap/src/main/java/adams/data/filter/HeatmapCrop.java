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
 * CropToCentroid.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.filter.heatmapcrop.AbstractHeatmapCrop;
import adams.data.filter.heatmapcrop.Dummy;
import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Generates a cropped heatmap by applying the specified crop algorithm.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-algorithm &lt;adams.data.filter.heatmapcrop.AbstractHeatmapCrop&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The height of the cropped region.
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.heatmapcrop.Dummy
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapCrop
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2270876952032422552L;

  /** the crop algorithm. */
  protected AbstractHeatmapCrop m_Algorithm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a cropped heatmap by applying the specified crop algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new Dummy());
  }

  /**
   * Sets the crop algorithm.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractHeatmapCrop value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the crop algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractHeatmapCrop getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The height of the cropped region.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Heatmap processData(Heatmap data) {
    return m_Algorithm.crop(data);
  }
}
