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
 * DefaultImageApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.data.image.AbstractImageContainer;
import com.microsoft.CNTK.FloatVector;
import com.microsoft.CNTK.FloatVectorVector;
import com.microsoft.CNTK.NDShape;
import com.microsoft.CNTK.UnorderedMapVariableValuePtr;
import com.microsoft.CNTK.Value;
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
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java"
)
public class DefaultImageApplier
  extends AbstractModelApplier<AbstractImageContainer, float[]>{

  private static final long serialVersionUID = 7933924670965842681L;

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
    Variable outputVar = m_Model.getOutputs().get(0);
    Variable inputVar = m_Model.getArguments().get(0);
    if (isLoggingEnabled()) {
      getLogger().info("m_Model=" + m_Model);
      getLogger().info("outputVar=" + outputVar);
      getLogger().info("inputVar=" + inputVar);
    }

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

    int[] resizedCHW = new int[imageSize];

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

    FloatVector floatVec = new FloatVector();
    for (int intensity : resizedCHW)
      floatVec.add(((float) intensity));

    FloatVectorVector floatVecVec = new FloatVectorVector();
    floatVecVec.add(floatVec);
    // Create input data map
    Value inputVal = Value.createDenseFloat(inputShape, floatVecVec, m_Device);
    UnorderedMapVariableValuePtr inputDataMap = new UnorderedMapVariableValuePtr();
    inputDataMap.add(inputVar, inputVal);

    // Create output data map. Using null as Value to indicate using system allocated memory.
    // Alternatively, create a Value object and add it to the data map.
    UnorderedMapVariableValuePtr outputDataMap = new UnorderedMapVariableValuePtr();
    outputDataMap.add(outputVar, null);

    // Start evaluation on the device
    m_Model.evaluate(inputDataMap, outputDataMap, m_Device);

    // get evaluate result as dense output
    FloatVectorVector outputBuffer = new FloatVectorVector();
    outputDataMap.getitem(outputVar).copyVariableValueToFloat(outputVar, outputBuffer);

    FloatVector results = outputBuffer.get(0);
    float[] result = new float[(int) results.size()];
    for (i = 0; i < result.length; i++)
      result[i] = results.get(i);

    if (isLoggingEnabled())
      getLogger().info("result=" + Utils.arrayToString(result));

    return result;
  }
}
