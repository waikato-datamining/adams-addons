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
import adams.core.base.BaseString;
import com.microsoft.CNTK.FloatVector;
import com.microsoft.CNTK.FloatVectorVector;
import com.microsoft.CNTK.UnorderedMapVariableValuePtr;
import com.microsoft.CNTK.Value;
import com.microsoft.CNTK.Variable;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a CNTK model for making predictions.
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
public class CNTKPredictionWrapper
  extends AbstractCNTKModelWrapper {

  private static final long serialVersionUID = -1508684329565658944L;

  /** the name of the class attribute in the model. */
  protected String m_ClassName;

  /** the actual classname (eg if automatically determined). */
  protected String m_ActualClassName;

  /** the name of the output variable. */
  protected String m_OutputName;

  /** the output variable. */
  protected transient Variable m_OutputVar;

  /** the number of classes in the data. */
  protected int m_NumClasses;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates a CNTK model for making predictions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-name", "className",
      getDefaultClassName());

    m_OptionManager.add(
      "output-name", "outputName",
      getDefaultOutputName());
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_NumClasses  = -1;
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultClassName() {
    return "";
  }

  /**
   * Sets the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @param value	the name
   */
  public void setClassName(String value) {
    m_ClassName = value;
    reset();
  }

  /**
   * Returns the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @return		the name
   */
  public String getClassName() {
    return m_ClassName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classNameTipText() {
    return
      "The name of the class attribute in the model, in case it cannot be "
	+ "determined automatically.";
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultOutputName() {
    return "";
  }

  /**
   * Sets the name of the output variable in the model, in case it cannot be
   * determined automatically based on its dimension.
   *
   * @param value	the name
   */
  public void setOutputName(String value) {
    m_OutputName = value;
    reset();
  }

  /**
   * Returns the name of the output variable in the model, in case it cannot
   * be determined automatically based on its dimension.
   *
   * @return		the name
   */
  public String getOutputName() {
    return m_OutputName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputNameTipText() {
    return
      "The name of the output variable in the model, in case it cannot be "
	+ "determined automatically based on its dimension.";
  }

  /**
   * Returns the currently set # of classes.
   *
   * @return		the number of classes (1 for numeric classes, otherwise
   * 			the number of labels)
   */
  public int getNumClasses() {
    return m_NumClasses;
  }

  /**
   * Initializes the model.
   *
   * @param numClasses	the number of classes in the data: 1 for numeric classes,
   *                    otherwise the number of labels
   * @throws Exception	if loading fails
   */
  public void initModel(int numClasses) throws Exception {
    super.initModel();

    // keep # classes
    m_NumClasses = numClasses;

    // analyze model structure
    // 1. outputs
    m_OutputVar = null;
    for (Variable var: m_Model.getOutputs()) {
      if (m_OutputName.isEmpty()) {
	if (var.getShape().getTotalSize() == m_NumClasses) {
	  m_OutputVar = var;
	  break;
	}
      }
      else {
        if (var.getName().equals(m_OutputName) || var.getUid().equals(m_OutputName)) {
	  m_OutputVar = var;
	  break;
	}
      }
    }
    if (isLoggingEnabled())
      getLogger().info("Output var: " + m_OutputVar);
    if (m_OutputVar == null)
      throw new IllegalStateException("Failed to determine output variable!");

    // 2. inputs
    m_ActualClassName = m_ClassName;
    for (Variable var: m_Model.getArguments()) {
      String name = var.getName();
      String uid = var.getUid();
      for (BaseString inputName: m_InputNames) {
        if (inputName.getValue().equals(name) || inputName.getValue().equals(uid)) {
          if (m_ActualClassName.isEmpty()) {
            if (var.getShape().getTotalSize() == m_NumClasses) {
	      m_ActualClassName = inputName.getValue();
	      if (isLoggingEnabled())
	        getLogger().info("Actual classname: " + m_ActualClassName);
	    }
	  }
          break;
	}
      }
    }
  }

  /**
   * Generates a prediction on the input data.
   *
   * @param input	the input
   * @return		the score
   */
  public float[] predict(float[] input) throws Exception {
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
    if (m_Names.contains(m_ActualClassName)) {
      floatVecs.put(m_ActualClassName, new FloatVector());
      for (i = 0; i < m_NumClasses; i++)
	floatVecs.get(m_ActualClassName).add(0.0f);
    }
    for (i = 0; i < input.length; i++) {
      for (String name: m_Names) {
        range = m_Ranges.get(name);
        if ((range != null) && (range.contains(i))) {
          if (!floatVecs.containsKey(name))
            floatVecs.put(name, new FloatVector());
          if (!name.equals(m_ActualClassName))
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

  /**
   * Frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_OutputVar != null) {
      m_OutputVar.delete();
      m_OutputVar = null;
    }
    super.cleanUp();
  }
}
