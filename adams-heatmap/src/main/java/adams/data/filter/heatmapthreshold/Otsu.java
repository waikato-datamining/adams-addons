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
 * Otsu.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapthreshold;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageType;
import adams.data.heatmap.Heatmap;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageUInt8;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Computes the variance based threshold using Otsu's method from an input image (gray scale; boofcv.struct.image.ImageUInt8).<br>
 * <br>
 * For more information see:<br>
 * WikiPedia. Otsu's method.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.HeatmapToBufferedImageConversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion to use for turning the heatmap into a BufferedImage.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.HeatmapToBufferedImage -generator adams.gui.visualization.core.BiColorGenerator
 * </pre>
 * 
 * <pre>-min &lt;int&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;The minimum value to use in the computation (included).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max &lt;int&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum value to use in the computation (excluded).
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Otsu
  extends AbstractBufferedImageBasedHeatmapThreshold {

  private static final long serialVersionUID = -1205777458822555932L;

  /** the min value. */
  protected int m_Min;

  /** the max value. */
  protected int m_Max;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Computes the variance based threshold using Otsu's method from "
	+ "an input image (gray scale; " + ImageUInt8.class.getName() + ").\n\n"
	+ "For more information see:\n"
	+ getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Otsu's method");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/Otsu%27s_method");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "min",
	    0, 0, null);

    m_OptionManager.add(
	    "max", "max",
	    256, 1, null);
  }

  /**
   * Sets the minimum value for the computation.
   *
   * @param value	the minimum (incl)
   */
  public void setMin(int value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum value for the computation.
   *
   * @return		the minimum (incl)
   */
  public int getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minTipText() {
    return "The minimum value to use in the computation (included).";
  }

  /**
   * Sets the maximum value for the computation.
   *
   * @param value	the maximum (excl)
   */
  public void setMax(int value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum value for the computation.
   *
   * @return		the maximum (excl)
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxTipText() {
    return "The maximum value to use in the computation (excluded).";
  }

  /**
   * Performs the actual calculation of the threshold using the image.
   *
   * @param map		the original heatmap
   * @param img		the generated image to use
   * @return		the threshold
   */
  protected double doCalcThreshold(Heatmap map, BufferedImage img) {
    double	result;
    ImageBase 	gray;
    int		otsu;

    gray   = BoofCVHelper.toBoofCVImage(img, BoofCVImageType.UNSIGNED_INT_8);
    otsu   = GThresholdImageOps.computeOtsu((ImageUInt8) gray, m_Min, m_Max);
    result = m_Conversion.grayToIntensity(map, otsu);
    if (isLoggingEnabled())
      getLogger().info("Otsu (gray/intensity): " + otsu + "/" + result);

    return result;
  }
}
