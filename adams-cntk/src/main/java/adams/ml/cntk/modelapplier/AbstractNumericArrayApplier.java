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
 * AbstractNumericArrayApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import com.microsoft.CNTK.FloatVector;
import com.microsoft.CNTK.FloatVectorVector;
import com.microsoft.CNTK.NDShape;
import com.microsoft.CNTK.UnorderedMapVariableValuePtr;
import com.microsoft.CNTK.Value;
import com.microsoft.CNTK.Variable;

/**
 * Ancestor for scoring numeric arrays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <I> the input data
 */
@MixedCopyright(
  author = "CNTK",
  copyright = "Microsoft",
  license = License.MIT,
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java"
)
public abstract class AbstractNumericArrayApplier<I>
  extends AbstractModelApplier<I, float[]>{

  private static final long serialVersionUID = 7933924670965842681L;

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
  protected float[] applyModel(double[] input) {
    float[]	values;
    int		i;

    values = new float[input.length];
    for (i = 0; i < input.length; i++)
      values[i] = (float) input[i];

    return applyModel(values);
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  protected float[] applyModel(float[] input) {
    Variable outputVar = m_Model.getOutputs().get(0);
    Variable inputVar = m_Model.getArguments().get(0);
    if (isLoggingEnabled()) {
      getLogger().info("model=" + m_Model);
      getLogger().info("outputVar=" + outputVar);
      getLogger().info("inputVar=" + inputVar);
    }

    NDShape inputShape = inputVar.getShape();
    int width = (int)inputShape.getDimensions()[0];
    if (width != input.length)
      throw new IllegalStateException("Input length and model dimension #0 differ: " + input.length + " != " + width);

    if (isLoggingEnabled())
      getLogger().info("width=" + width);

    FloatVector floatVec = new FloatVector();
    for (float f : input)
      floatVec.add(f);

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
    for (int i = 0; i < result.length; i++)
      result[i] = results.get(i);

    if (isLoggingEnabled())
      getLogger().info("result=" + Utils.arrayToString(result));

    return result;
  }
}
