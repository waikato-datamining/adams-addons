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
 * BoofCVMeanShiftLikelihood.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import boofcv.abst.tracker.MeanShiftLikelihoodType;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayF64;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayS64;
import boofcv.struct.image.GrayS8;
import boofcv.struct.image.GrayU16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;

/**
 <!-- globalinfo-start -->
 * Very basic and very fast implementation of mean-shift which uses a fixed sized rectangle for its region. Works best when the target is composed of a single color.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-type &lt;HISTOGRAM|HISTOGRAM_RGB_to_HSV|HISTOGRAM_INDEPENDENT_RGB_to_HSV&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The likelihood type to use.
 * &nbsp;&nbsp;&nbsp;default: HISTOGRAM
 * </pre>
 * 
 * <pre>-max-iterations &lt;int&gt; (property: maxIterations)
 * &nbsp;&nbsp;&nbsp;The maximum number of iterations to perform.
 * &nbsp;&nbsp;&nbsp;default: 30
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use for the histogram.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-max-pixel-value &lt;int&gt; (property: maxPixelValue)
 * &nbsp;&nbsp;&nbsp;The maximum value of the pixel values (usually 255, since using bytes).
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-bands &lt;int&gt; (property: numBands)
 * &nbsp;&nbsp;&nbsp;The number of bands in the image (= 3 for RGB).
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BoofCVMeanShiftLikelihood
  extends AbstractBoofCVObjectTracker {

  private static final long serialVersionUID = 7061565466109634695L;

  /** the type of likelihood. */
  protected MeanShiftLikelihoodType m_Type;

  /** the maximum number of iterations. */
  protected int m_MaxIterations;

  /** the number of bins. */
  protected int m_NumBins;

  /** the maximum pixel value. */
  protected int m_MaxPixelValue;

  /** the number of bands in the image. */
  protected int m_NumBands;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Very basic and very fast implementation of mean-shift which uses a fixed sized rectangle for its region. "
        + "Works best when the target is composed of a single color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      MeanShiftLikelihoodType.HISTOGRAM);

    m_OptionManager.add(
      "max-iterations", "maxIterations",
      30, 1, null);

    m_OptionManager.add(
      "num-bins", "numBins",
      5, 1, null);

    m_OptionManager.add(
      "max-pixel-value", "maxPixelValue",
      255, 1, null);

    m_OptionManager.add(
      "num-bands", "numBands",
      3, 1, null);
  }

  /**
   * Sets the likelihood type.
   *
   * @param value	the type
   */
  public void setType(MeanShiftLikelihoodType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the likelihood type
   *
   * @return		the type
   */
  public MeanShiftLikelihoodType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The likelihood type to use.";
  }

  /**
   * Sets the maximum number of iterations.
   *
   * @param value	the iterations
   */
  public void setMaxIterations(int value) {
    m_MaxIterations = value;
    reset();
  }

  /**
   * Returns the maximum number of iterations.
   *
   * @return		the iterations
   */
  public int getMaxIterations() {
    return m_MaxIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxIterationsTipText() {
    return "The maximum number of iterations to perform.";
  }

  /**
   * Sets the number of bins for the histogram.
   *
   * @param value	the bins
   */
  public void setNumBins(int value) {
    m_NumBins = value;
    reset();
  }

  /**
   * Returns the number of bins for the histogram.
   *
   * @return		the bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use for the histogram.";
  }

  /**
   * Sets the maximum pixel value (usually 255 when using bytes).
   *
   * @param value	the maximum
   */
  public void setMaxPixelValue(int value) {
    m_MaxPixelValue = value;
    reset();
  }

  /**
   * Returns the maximum pixel value (usually 255 when using bytes).
   *
   * @return		the maximum
   */
  public int getMaxPixelValue() {
    return m_MaxPixelValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxPixelValueTipText() {
    return "The maximum value of the pixel values (usually 255, since using bytes).";
  }

  /**
   * Sets the number of bands in the image.
   *
   * @param value	the bands
   */
  public void setNumBands(int value) {
    m_NumBands = value;
    reset();
  }

  /**
   * Returns the number of bands in the image.
   *
   * @return		the bands
   */
  public int getNumBands() {
    return m_NumBands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBandsTipText() {
    return "The number of bands in the image (= 3 for RGB).";
  }

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  @Override
  protected TrackerObjectQuad newTracker() {
    switch (m_ImageType) {
      case GRAYF32:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayF32.class));
      case GRAYF64:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayF64.class));
      case GRAYS16:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayS16.class));
      case GRAYS32:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayS32.class));
      case GRAYS64:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayS64.class));
      case GRAYS8:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayS8.class));
      case GRAYU16:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayU16.class));
      case GRAYU8:
        return FactoryTrackerObjectQuad.meanShiftLikelihood(
          m_MaxIterations, m_NumBins, m_MaxPixelValue, m_Type, ImageType.pl(m_NumBands, GrayU8.class));
      default:
        throw new IllegalStateException("Unhandled image type: " + m_ImageType);
    }
  }
}
