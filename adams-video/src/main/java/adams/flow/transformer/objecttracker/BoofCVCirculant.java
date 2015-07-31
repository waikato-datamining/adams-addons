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
 * BoofCVCirculant.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import boofcv.abst.tracker.ConfigCirculantTracker;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageFloat64;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageSInt64;
import boofcv.struct.image.ImageSInt8;
import boofcv.struct.image.ImageUInt16;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Creates the Circulant feature tracker. Texture based tracker which uses the theory of circulant matrices, Discrete Fourier Transform (DCF), and linear classifiers to track a target. Fixed sized rectangular target and only estimates translation. Can't detect when it loses track or re-aquire track.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-image-type &lt;FLOAT_32|FLOAT_64|SIGNED_INT_8|UNSIGNED_INT_8|SIGNED_INT_16|UNSIGNED_INT_16|SIGNED_INT_32|SIGNED_INT_64&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The image type to use.
 * &nbsp;&nbsp;&nbsp;default: UNSIGNED_INT_8
 * </pre>
 * 
 * <pre>-output-sigma-factor &lt;double&gt; (property: outputSigmaFactor)
 * &nbsp;&nbsp;&nbsp;The spatial bandwidth. Proportional to target size.
 * &nbsp;&nbsp;&nbsp;default: 0.0625
 * </pre>
 * 
 * <pre>-sigma &lt;double&gt; (property: sigma)
 * &nbsp;&nbsp;&nbsp;The gaussian kernel bandwidth.
 * &nbsp;&nbsp;&nbsp;default: 0.2
 * </pre>
 * 
 * <pre>-lambda &lt;double&gt; (property: lambda)
 * &nbsp;&nbsp;&nbsp;The regularization term.
 * &nbsp;&nbsp;&nbsp;default: 0.01
 * </pre>
 * 
 * <pre>-interp-factor &lt;double&gt; (property: interpFactor)
 * &nbsp;&nbsp;&nbsp;The weighting factor mixing old track image and new one. Effectively adjusts 
 * &nbsp;&nbsp;&nbsp;the rate at which it can adjust to changes in appearance. Values closer 
 * &nbsp;&nbsp;&nbsp;to zero slow down the rate of change. 0.0 is no update. 0.075 is recommended.
 * &nbsp;&nbsp;&nbsp;default: 0.075
 * </pre>
 * 
 * <pre>-max-pixel-value &lt;double&gt; (property: maxPixelValue)
 * &nbsp;&nbsp;&nbsp;The maximum pixel value. Used to normalize image. 8-bit images are 255.
 * &nbsp;&nbsp;&nbsp;default: 255.0
 * </pre>
 * 
 * <pre>-padding &lt;double&gt; (property: padding)
 * &nbsp;&nbsp;&nbsp;How much padding is added around the region requested by the user. Specified 
 * &nbsp;&nbsp;&nbsp;as fraction of original image. Padding of 1 = 2x original size.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-work-space &lt;int&gt; (property: workSpace)
 * &nbsp;&nbsp;&nbsp;The length of size in work space image. A total of N*N points are sampled.
 * &nbsp;&nbsp;&nbsp; Should be set to a power of two to maximize speed. In general, larger numbers 
 * &nbsp;&nbsp;&nbsp;are more stable but slower.
 * &nbsp;&nbsp;&nbsp;default: 64
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVCirculant
  extends AbstractBoofCVObjectTracker {

  private static final long serialVersionUID = 7061565466109634695L;

  /** Spatial bandwidth.  Proportional to target size. */
  protected double m_OutputSigmaFactor;

  /** gaussian kernel bandwidth. */
  protected double m_Sigma;

  /** Regularization term. */
  protected double m_Lambda;

  /**
   * Weighting factor mixing old track image and new one.  Effectively adjusts the rate at which it can adjust
   * to changes in appearance.  Values closer to zero slow down the rate of change.  0f is no update.
   * 0.075f is recommended.
   */
  protected double m_InterpFactor;

  /** Maximum pixel value.  Used to normalize image.  8-bit images are 255 */
  protected double m_MaxPixelValue;

  /**
   * How much padding is added around the region requested by the user.  Specified as fraction of original image.
   * Padding of 1 = 2x original size.
   */
  protected double m_Padding;

  /**
   * Length of size in work space image.  A total of N*N points are sampled.  Should be set to a power of two
   * to maximize speed.  In general, larger numbers are more stable but slower.
   */
  protected int m_WorkSpace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates the Circulant feature tracker. Texture based tracker which uses the theory of circulant matrices, "
        + "Discrete Fourier Transform (DCF), and linear classifiers to track a target. Fixed sized rectangular target "
        + "and only estimates translation. Can't detect when it loses track or re-aquire track.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-sigma-factor", "outputSigmaFactor",
      1.0 / 16.0);

    m_OptionManager.add(
      "sigma", "sigma",
      0.2);

    m_OptionManager.add(
      "lambda", "lambda",
      1e-2);

    m_OptionManager.add(
      "interp-factor", "interpFactor",
      0.075);

    m_OptionManager.add(
      "max-pixel-value", "maxPixelValue",
      255.0);

    m_OptionManager.add(
      "padding", "padding",
      1.0, 0.0, null);

    m_OptionManager.add(
      "work-space", "workSpace",
      64, 1, null);
  }

  /**
   * Sets the spatial bandwidth. Proportional to target size.
   *
   * @param value	the bandwidth
   */
  public void setOutputSigmaFactor(double value) {
    if (getOptionManager().isValid("outputSigmaFactor", value)) {
      m_OutputSigmaFactor = value;
      reset();
    }
  }

  /**
   * Returns the spatial bandwidth. Proportional to target size.
   *
   * @return		the bandwidth
   */
  public double getOutputSigmaFactor() {
    return m_OutputSigmaFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputSigmaFactorTipText() {
    return "The spatial bandwidth. Proportional to target size.";
  }

  /**
   * Sets the gaussian kernel bandwidth.
   *
   * @param value	the bandwidth
   */
  public void setSigma(double value) {
    if (getOptionManager().isValid("sigma", value)) {
      m_Sigma = value;
      reset();
    }
  }

  /**
   * Returns the gaussian kernel bandwidth.
   *
   * @return		the bandwidth
   */
  public double getSigma() {
    return m_Sigma;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sigmaTipText() {
    return "The gaussian kernel bandwidth.";
  }

  /**
   * Sets the regularization term.
   *
   * @param value	the term
   */
  public void setLambda(double value) {
    if (getOptionManager().isValid("lambda", value)) {
      m_Lambda = value;
      reset();
    }
  }

  /**
   * Returns the regularization term.
   *
   * @return		the term
   */
  public double getLambda() {
    return m_Lambda;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lambdaTipText() {
    return "The regularization term.";
  }

  /**
   * Sets the weighting factor mixing old track image and new one.
   *
   * @param value	the factor
   */
  public void setInterpFactor(double value) {
    if (getOptionManager().isValid("interpFactor", value)) {
      m_InterpFactor = value;
      reset();
    }
  }

  /**
   * Returns the weighting factor mixing old track image and new one.
   *
   * @return		the factor
   */
  public double getInterpFactor() {
    return m_InterpFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String interpFactorTipText() {
    return
      "The weighting factor mixing old track image and new one. Effectively "
	+ "adjusts the rate at which it can adjust to changes in appearance. "
	+ "Values closer to zero slow down the rate of change. 0.0 is no update. "
	+ "0.075 is recommended.";
  }

  /**
   * Sets the maximum pixel value. Used to normalize image. 8-bit images are 255.
   *
   * @param value	the maximum
   */
  public void setMaxPixelValue(double value) {
    if (getOptionManager().isValid("maxPixelValue", value)) {
      m_MaxPixelValue = value;
      reset();
    }
  }

  /**
   * Returns the maximum pixel value. Used to normalize image. 8-bit images are 255.
   *
   * @return		the maximum
   */
  public double getMaxPixelValue() {
    return m_MaxPixelValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxPixelValueTipText() {
    return "The maximum pixel value. Used to normalize image. 8-bit images are 255.";
  }

  /**
   * Sets the padding added around the region.
   *
   * @param value	the padding
   */
  public void setPadding(double value) {
    if (getOptionManager().isValid("padding", value)) {
      m_Padding = value;
      reset();
    }
  }

  /**
   * Returns the padding added around the region.
   *
   * @return		the padding
   */
  public double getPadding() {
    return m_Padding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paddingTipText() {
    return
      "How much padding is added around the region requested by the user. "
	+ "Specified as fraction of original image. Padding of 1 = 2x original size.";
  }

  /**
   * Sets the gaussian kernel bandwidth.
   *
   * @param value	the bandwidth
   */
  public void setWorkSpace(int value) {
    if (getOptionManager().isValid("workSpace", value)) {
      m_WorkSpace = value;
      reset();
    }
  }

  /**
   * Returns the gaussian kernel bandwidth.
   *
   * @return		the bandwidth
   */
  public int getWorkSpace() {
    return m_WorkSpace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String workSpaceTipText() {
    return
      "The length of size in work space image. A total of N*N points are sampled. "
	+ "Should be set to a power of two to maximize speed. In general, "
	+ "larger numbers are more stable but slower.";
  }

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  @Override
  protected TrackerObjectQuad newTracker() {
    ConfigCirculantTracker	config;

    config                     = new ConfigCirculantTracker();
    config.output_sigma_factor = m_OutputSigmaFactor;
    config.sigma               = m_Sigma;
    config.lambda              = m_Lambda;
    config.interp_factor       = m_InterpFactor;
    config.maxPixelValue       = m_MaxPixelValue;
    config.padding             = m_Padding;
    config.workSpace           = m_WorkSpace;

    switch (m_ImageType) {
      case FLOAT_32:
        return FactoryTrackerObjectQuad.circulant(config, ImageFloat32.class);
      case FLOAT_64:
        return FactoryTrackerObjectQuad.circulant(config, ImageFloat64.class);
      case SIGNED_INT_16:
        return FactoryTrackerObjectQuad.circulant(config, ImageSInt16.class);
      case SIGNED_INT_32:
        return FactoryTrackerObjectQuad.circulant(config, ImageSInt32.class);
      case SIGNED_INT_64:
        return FactoryTrackerObjectQuad.circulant(config, ImageSInt64.class);
      case SIGNED_INT_8:
        return FactoryTrackerObjectQuad.circulant(config, ImageSInt8.class);
      case UNSIGNED_INT_16:
        return FactoryTrackerObjectQuad.circulant(config, ImageUInt16.class);
      case UNSIGNED_INT_8:
        return FactoryTrackerObjectQuad.circulant(config, ImageUInt8.class);
      default:
        throw new IllegalStateException("Unhandled image type: " + m_ImageType);
    }
  }
}
