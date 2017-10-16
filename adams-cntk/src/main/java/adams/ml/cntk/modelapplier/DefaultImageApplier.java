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
 * DefaultImageApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 * Copyright (C) Microsoft
 */

package adams.ml.cntk.modelapplier;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.data.image.AbstractImageContainer;
import adams.ml.cntk.predictionpostprocessor.PassThrough;
import adams.ml.cntk.predictionpostprocessor.PredictionPostProcessor;
import com.microsoft.CNTK.NDShape;
import com.microsoft.CNTK.Variable;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

/**
 * Scores images.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "CNTK",
  copyright = "Microsoft",
  license = License.MIT,
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java",
  note = "Original code based on CNTK example"
)
public class DefaultImageApplier
  extends AbstractModelApplier<AbstractImageContainer, float[]>{

  private static final long serialVersionUID = 7933924670965842681L;

  /** the post-processor to apply. */
  protected PredictionPostProcessor m_PostProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the model to images and returns the score. Images get scaled according to the model inputs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "post-processor", "postProcessor",
      getDefaultPostProcessor());
  }

  /**
   * Returns the default post-processor to use for the predictions.
   *
   * @return  		the post-processor
   */
  protected PredictionPostProcessor getDefaultPostProcessor() {
    return new PassThrough();
  }

  /**
   * Sets the post-processor to use for the predictions.
   *
   * @param value	the post-processor
   */
  public void setPostProcessor(PredictionPostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor to use for the predictions.
   *
   * @return  		the post-processor
   */
  public PredictionPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to apply to the predictions.";
  }

  /**
   * Returns the class that the applier accepts.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractImageContainer.class;
  }

  /**
   * Returns the class that the applier generates.
   *
   * @return		the class
   */
  public Class generates() {
    return float[].class;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  @Override
  protected float[] doApplyModel(AbstractImageContainer input) {
    Variable inputVar = m_Wrapper.getModel().getArguments().get(0);

    NDShape inputShape = inputVar.getShape();
    int imageWidth = (int)inputShape.getDimensions()[0];
    int imageHeight = (int)inputShape.getDimensions()[1];
    int imageChannels = (int)inputShape.getDimensions()[2];
    int imageSize = ((int) inputShape.getTotalSize());

    if (isLoggingEnabled()) {
      getLogger().info("imageWidth=" + imageWidth);
      getLogger().info("imageHeight=" + imageHeight);
      getLogger().info("imageChannels=" + imageChannels);
      getLogger().info("imageSize=" + imageSize);
    }

    // Image preprocessing to match input requirements of the model.
    BufferedImage bmp = input.toBufferedImage();
    Image resized = bmp.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
    BufferedImage bImg = new BufferedImage(
      resized.getWidth(null), resized.getHeight(null), BufferedImage.TYPE_INT_RGB);
    // or use any other fitting type
    bImg.getGraphics().drawImage(resized, 0, 0, null);

    float[] resizedCHW = new float[imageSize];

    int i = 0;
    for (int c = 0; c < imageChannels; c++) {
      for (int h = 0; h < bImg.getHeight(); h++) {
	for (int w = 0; w < bImg.getWidth(); w++) {
	  Color color = new Color(bImg.getRGB(w, h));
	  if (c == 0) {
	    resizedCHW[i] = color.getBlue();
	  } else if (c == 1) {
	    resizedCHW[i] = color.getGreen();
	  } else {
	    resizedCHW[i] = color.getRed();
	  }
	  i++;
	}
      }
    }
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().fine("resizedCHW=" + Utils.arrayToString(resizedCHW));

    try {
      float[] preds = m_Wrapper.predict(resizedCHW);
      return m_PostProcessor.postProcessPrediction(preds);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to make prediction!", e);
      return new float[0];
    }
  }
}
