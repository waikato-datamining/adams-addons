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
 * ManualBrainScriptModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

import adams.core.base.BaseText;

import java.util.ArrayList;
import java.util.List;

/**
 * Just outputs a single, manually defined model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ManualBrainScriptModel
  extends AbstractBrainScriptModelGenerator {

  private static final long serialVersionUID = -4683549348343064989L;

  /** the placeholder variable for the input dimension. */
  public final static String INPUT_DIM = "inputDim";

  /** the placeholder variable for the output dimension. */
  public final static String OUTPUT_DIM = "outputDim";

  /** the script to output. */
  protected BaseText m_Script;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Just outputs a single, manually defined BrainScript model.\n"
	+ "Inserts the following variables for input and output dimensions:\n"
	+ "- input: " + INPUT_DIM + "\n"
	+ "- output: " + OUTPUT_DIM + "\n"
	+ "\n"
	+ getBrainScriptInfo();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "script", "script",
      new BaseText(
	"model = Sequential (\n" +
	  "  Scale {featScale} :\n" +
	  "  DenseLayer  {inputDim, init=\"gaussian\", initValueScale=1.5} : Dropout: ReLU : \n" +
	  "  DenseLayer  {512, init=\"gaussian\", initValueScale=1.5} : Dropout: ReLU : \n" +
	  "  DenseLayer  {256, init=\"gaussian\", initValueScale=1.5} : Dropout: ReLU :  \n" +
	  "  LinearLayer {outputDim}\n" +
	  ")\n" +
	  "\n" +
	  "SGD = {\n" +
	  "  maxEpochs = 100\n" +
	  "  dropoutRate = 0\n" +
	  "  minibatchSize = 256\n" +
	  "}\n"));
  }

  /**
   * Sets the script to output.
   *
   * @param value	the script
   */
  public void setScript(BaseText value) {
    m_Script = value;
    reset();
  }

  /**
   * Returns the script to output.
   *
   * @return 		the script
   */
  public BaseText getScript() {
    return m_Script;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String scriptTipText() {
    return "The script to output.";
  }

  /**
   * Generates the actual models.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the models
   */
  @Override
  protected List<String> doGenerate(int numInput, int numOutput) {
    List<String> 	result;
    String 		model;
    String		script;

    result = new ArrayList<>();

    script = m_FlowContext.getVariables().expand(m_Script.getValue());
    model =
      INPUT_DIM + " = " + numInput + "\n"
	+ OUTPUT_DIM + " = " + numOutput + "\n"
	+ "\n"
	+ script;
    if (!model.endsWith("\n"))
      model = model + "\n";

    result.add(model);

    return result;
  }
}
