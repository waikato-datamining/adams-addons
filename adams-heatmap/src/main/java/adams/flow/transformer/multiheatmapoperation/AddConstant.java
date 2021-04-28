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
 * AddConstant.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multiheatmapoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.heatmap.Heatmap;

/**
 * Adds the specified constant value to all cells in the heatmaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AddConstant
  extends AbstractMultiHeatmapOperation<Heatmap[]>{

  private static final long serialVersionUID = 6124767252812041585L;

  /** the constant value to add. */
  protected double m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds the specified constant value to all cells in the heatmaps.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "value", "value",
      0.0);
  }

  /**
   * Sets the constant value to add.
   *
   * @param value	the value
   */
  public void setValue(double value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the constant value to add.
   *
   * @return		the value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The constant value to add to the cells.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "value", m_Value, "value: ");
  }

  /**
   * Returns the minimum number of heatmaps that are required for the operation.
   *
   * @return the number of heatmaps that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return 1;
  }

  /**
   * Returns the maximum number of heatmaps that are required for the operation.
   *
   * @return the number of heatmaps that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumSheetsRequired() {
    return 0;
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Heatmap[].class;
  }

  /**
   * Performs the actual processing of the heatmaps.
   *
   * @param maps   the heatmaps to process
   * @param errors for collecting errors
   * @return the generated data
   */
  @Override
  protected Heatmap[] doProcess(Heatmap[] maps, MessageCollection errors) {
    Heatmap[]	result;
    int		i;
    int		x;
    int		y;

    result = new Heatmap[maps.length];
    for (i = 0; i < maps.length; i++) {
      result[i] = maps[i].getClone();
      for (y = 0; y < maps[i].getHeight(); y++) {
	for (x = 0; x < maps[i].getWidth(); x++)
	  maps[i].set(y, x, maps[i].get(y, x) + m_Value);
      }
    }

    return result;
  }
}
