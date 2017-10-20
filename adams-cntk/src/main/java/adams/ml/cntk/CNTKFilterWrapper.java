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
 * CNTKModelWrapper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 * Copyright (C) Microsoft
 */

package adams.ml.cntk;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import com.microsoft.CNTK.FloatVector;
import com.microsoft.CNTK.FloatVectorVector;
import com.microsoft.CNTK.Function;
import com.microsoft.CNTK.UnorderedMapVariableValuePtr;
import com.microsoft.CNTK.Value;
import com.microsoft.CNTK.Variable;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a CNTK model for filtering data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "CNTK",
  copyright = "Microsoft",
  license = License.MIT,
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java",
  note = "Original code based on CNTK example"
)
public class CNTKFilterWrapper
  extends AbstractCNTKModelWrapper {

  private static final long serialVersionUID = -1508684329565658944L;

  /** the layer from which to get the filtered data from. */
  protected String m_FilterLayer;

  /** the filter layer. */
  protected transient Function m_Layer;

  /** the output variable. */
  protected transient Variable m_OutputVar;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates a CNTK model for filtering data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-layer", "filterLayer",
      getDefaultFilterLayer());
  }

  /**
   * Returns the default name of the layer to obtain the filtered data from.
   *
   * @return		the default
   */
  protected String getDefaultFilterLayer() {
    return "";
  }

  /**
   * Sets the name of the layer to obtain the filtered data from.
   *
   * @param value	the name
   */
  public void setFilterLayer(String value) {
    m_FilterLayer = value;
    reset();
  }

  /**
   * Returns the name of the layer to obtain the filtered data from.
   *
   * @return		the name
   */
  public String getFilterLayer() {
    return m_FilterLayer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String filterLayerTipText() {
    return "The name of the layer to obtain the filtered data from.";
  }

  /**
   * Initializes the model.
   *
   * @throws Exception	if loading fails
   */
  @Override
  public void initModel() throws Exception {
    if (m_FilterLayer.isEmpty())
      throw new IllegalStateException("No filter layer defined!");

    super.initModel();

    m_Layer = m_Model.findByName(m_FilterLayer);
    if (m_Layer.getOutputs().size() != 1)
      throw new IllegalStateException("More than one output in layer '" + m_FilterLayer + "'!");
    m_OutputVar = m_Layer.getOutputs().get(0);
  }

  /**
   * Generates filtered data from the input data.
   *
   * @param input	the input
   * @return		the score
   */
  public float[] filter(float[] input) throws Exception {
    int					i;
    Map<String,FloatVector> 		floatVecs;
    TIntHashSet 			range;
    UnorderedMapVariableValuePtr 	inputDataMap;
    FloatVectorVector 			floatVecVec;
    Value 				inputVal;
    UnorderedMapVariableValuePtr 	outputDataMap;
    FloatVectorVector 			outputBuffer;
    FloatVector 			results;
    float[] 				result;

    if (!isInitialized())
      throw new Exception("Model not initialized!");

    // ranges initialized?
    if (m_Ranges == null) {
      m_Ranges = new HashMap<>();
      for (i = 0; i < m_Inputs.length; i++) {
	m_Inputs[i].setMax(input.length + 1);  // +1 because class value already removed from array
	m_Ranges.put(m_InputNames[i].getValue(), new TIntHashSet(m_Inputs[i].getIntIndices()));
      }
    }

    // assemble input data
    floatVecs = new HashMap<>();
    for (i = 0; i < input.length; i++) {
      for (String name: m_Names) {
        range = m_Ranges.get(name);
        if ((range != null) && (range.contains(i))) {
          if (!floatVecs.containsKey(name))
            floatVecs.put(name, new FloatVector());
	  floatVecs.get(name).add(input[i]);
	  break;
	}
      }
    }

    inputDataMap = new UnorderedMapVariableValuePtr();
    for (String name: m_Names) {
      floatVecVec = new FloatVectorVector();
      floatVecVec.add(floatVecs.get(name));
      // Create input data map
      inputVal = Value.createDenseFloat(m_InputShapes.get(name), floatVecVec, m_Device);
      inputDataMap.add(m_InputVars.get(name), inputVal);
    }

    // Create output data map. Using null as Value to indicate using system allocated memory.
    // Alternatively, create a Value object and add it to the data map.
    outputDataMap = new UnorderedMapVariableValuePtr();
    outputDataMap.add(m_OutputVar, null);

    // Start evaluation on the device
    m_Model.evaluate(inputDataMap, outputDataMap, m_Device);

    // get evaluate result as dense output
    outputBuffer = new FloatVectorVector();
    outputDataMap.getitem(m_OutputVar).copyVariableValueToFloat(m_OutputVar, outputBuffer);

    results = outputBuffer.get(0);
    result = new float[(int) results.size()];
    for (i = 0; i < result.length; i++)
      result[i] = results.get(i);

    for (FloatVector floatVec: floatVecs.values())
      floatVec.delete();
    outputBuffer.delete();
    inputDataMap.delete();
    outputDataMap.delete();
    results.delete();

    return result;
  }
}
