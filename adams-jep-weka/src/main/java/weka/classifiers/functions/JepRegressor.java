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
 * JepRegressor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import adams.core.StoppableWithFeedback;
import adams.core.UniqueIDs;
import adams.core.scripting.JepScript;
import adams.core.scripting.SimpleJepScriptlet;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.JepEngine;
import adams.gui.core.AbstractAdvancedScript;
import jep.NDArray;
import weka.classifiers.ScriptedClassifier;
import weka.classifiers.ScriptedClassifierUtils;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Regressor that uses Jep to train and execute a Python-based regressor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JepRegressor
  extends AbstractSimpleClassifier
  implements StoppableWithFeedback, ScriptedClassifier {

  private static final long serialVersionUID = 2387124102209234888L;

  public final static String VAR_PREFIX = "{PREFIX}";

  /** the train script. */
  protected JepScript m_TrainScript;

  /** the classify script. */
  protected JepScript m_ClassifyScript;

  /** whether the classifier was stopped. */
  protected boolean m_Stopped;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /** the engine in use. */
  protected transient JepEngine m_Engine;

  /** the model object. */
  protected transient Object m_Model;

  /** the classify script instance to use. */
  protected transient JepScript m_ClassifyScriptActual;

  /** the variable prefix to use at prediction time. */
  protected transient String m_ClassifyVarPrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Regressor that uses Jep to train and execute a Python-based regressor.\n"
	     + "For training the predictors are available via the 'train_X' variable and the target via 'train_y'. "
	     + "The trained model must be stored in variable 'model'.\n"
	     + "At prediction time, the model is available as 'model', the predictors via 'pred_x' and the result must be stored in 'pred_y'.\n"
	     + "In order to avoid clashes between variables when models are used concurrently, the placeholder '" + VAR_PREFIX + "' "
	     + "can be used, e.g.: " + VAR_PREFIX + "train_X. This prefix gets replaced with a unique ID before the script is executed.\n"
	     + "Flow variables and placeholders get expanded automatically.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "train-script", "trainScript",
      new JepScript());

    m_OptionManager.add(
      "classify-script", "classifyScript",
      new JepScript());
  }

  /**
   * Sets the train script to use.
   *
   * @param value 	the script
   */
  public void setTrainScript(JepScript value) {
    m_TrainScript = value;
    reset();
  }

  /**
   * Gets the train script to use.
   *
   * @return 		the script
   */
  public JepScript getTrainScript() {
    return m_TrainScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String trainScriptTipText() {
    return "The script to use for training; flow variables get expanded automatically.";
  }

  /**
   * Sets the script to use for classifying Instance objects.
   *
   * @param value 	the script
   */
  public void setClassifyScript(JepScript value) {
    m_ClassifyScript = value;
    reset();
  }

  /**
   * Gets the script to use for classifying Instance objects.
   *
   * @return 		the script
   */
  public JepScript getClassifyScript() {
    return m_ClassifyScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classifyScriptTipText() {
    return "The script to use for classifying Instance objects; flow variables get expanded automatically.";
  }

  /**
   * Sets the flow context.
   *
   * @param value	the context
   */
  @Override
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context.
   *
   * @return		the context, null if not available
   */
  @Override
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.NUMERIC_CLASS);
    return result;
  }

  /**
   * Configures the Jep engine if necessary.
   */
  protected void setupJepEngine() {
    if (m_FlowContext == null)
      throw new IllegalStateException("No flow context set!");
    if (m_Engine == null)
      m_Engine = (JepEngine) ActorUtils.findClosestType(m_FlowContext, JepEngine.class, true);
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    double[]		X;
    double[]		y;
    double[]		row;
    int			clsIndex;
    int			i;
    int			n;
    int			m;
    AbstractAdvancedScript 	script;
    SimpleJepScriptlet	scriptlet;
    Map<String,Object>	inputs;
    String		varPrefix;

    m_Stopped = false;
    m_Model   = null;

    setupJepEngine();
    getCapabilities().test(data);

    // convert dataset
    X = new double[(data.numAttributes() - 1) * data.numInstances()];
    m = 0;
    clsIndex = data.classIndex();
    for (i = 0; i < data.numInstances(); i++) {
      row = data.instance(i).toDoubleArray();
      for (n = 0; n < row.length; n++) {
	if (n == clsIndex)
	  continue;
	X[m] = row[n];
	m++;
      }
    }
    y = data.attributeToDoubleArray(clsIndex);

    // execute script
    script    = ScriptedClassifierUtils.expand(m_TrainScript, m_FlowContext);
    varPrefix = ScriptedClassifierUtils.determineUniqueID(script, VAR_PREFIX, true, true);
    inputs    = new HashMap<>();
    inputs.put(varPrefix + "train_X", new NDArray<>(X, data.numInstances(), data.numAttributes() - 1));
    inputs.put(varPrefix + "train_y", new NDArray<>(y));
    scriptlet = new SimpleJepScriptlet(getClass().getName() + "-" + UniqueIDs.nextLong(), script.getValue(), inputs, new String[]{varPrefix + "model"}, new String[]{varPrefix + "model"});
    scriptlet.setFlowContext(m_FlowContext);
    scriptlet.setLoggingLevel(getLoggingLevel());
    m_Engine.getEngine().add(scriptlet);
    while (!isStopped() && !scriptlet.hasFinished())
      adams.core.Utils.wait(this, 1000, 100);

    if (!isStopped()) {
      if (scriptlet.hasLastError())
	throw new Exception(scriptlet.getLastError());
      if (scriptlet.getOutputs().containsKey(varPrefix + "model")) {
	if (isLoggingEnabled())
	  getLogger().info("Retrieving model: " + varPrefix + "model");
	m_Model = scriptlet.getOutputs().get(varPrefix + "model");
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("Cannot retrieve model: " + varPrefix + "model");
      }
    }
  }

  /**
   * Prepares the classifier for predictions.
   *
   * @param context	the context to use
   */
  public void initPrediction(Actor context) {
    m_FlowContext = context;
    if (m_ClassifyScriptActual == null) {
      m_ClassifyScriptActual = ScriptedClassifierUtils.expand(m_ClassifyScript, m_FlowContext);
      m_ClassifyVarPrefix    = ScriptedClassifierUtils.determineUniqueID(m_ClassifyScriptActual, VAR_PREFIX, true, true);
      if (isLoggingEnabled())
	getLogger().info("Actual classify script:\n" + m_ClassifyScriptActual.getValue());
    }
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified. Note that a classifier MUST implement either
   * this or distributionForInstance().
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    double			result;
    double[]			x;
    double[]			row;
    int				i;
    int				n;
    int				clsIndex;
    SimpleJepScriptlet		scriptlet;
    Map<String,Object>		inputs;
    Object			output;

    result = Utils.missingValue();

    setupJepEngine();
    initPrediction(m_FlowContext);

    // convert instance
    clsIndex = instance.classIndex();
    x        = new double[instance.numAttributes() - 1];
    row      = instance.toDoubleArray();
    n        = 0;
    for (i = 0; i < row.length; i++) {
      if (i == clsIndex)
	continue;
      x[n] = row[i];
      n++;
    }

    // execute script
    inputs = new HashMap<>();
    inputs.put(m_ClassifyVarPrefix + "pred_x", new NDArray<>(x));
    if (m_Model != null)
      inputs.put(m_ClassifyVarPrefix + "model", m_Model);
    scriptlet = new SimpleJepScriptlet(getClass().getName() + "-" + UniqueIDs.nextLong(), m_ClassifyScriptActual.getValue(), inputs, new String[]{m_ClassifyVarPrefix + "pred_y"});
    scriptlet.setFlowContext(m_FlowContext);
    scriptlet.setLoggingLevel(getLoggingLevel());
    m_Engine.getEngine().add(scriptlet);
    while (!isStopped() && !scriptlet.hasFinished())
      adams.core.Utils.wait(this, 200, 50);

    // retrieve prediction
    if (!isStopped()) {
      if (scriptlet.hasLastError())
	throw new Exception(scriptlet.getLastError());
      if (scriptlet.getOutputs().containsKey(m_ClassifyVarPrefix + "pred_y")) {
	output = scriptlet.getOutputs().get(m_ClassifyVarPrefix + "pred_y");
	if (output instanceof Number)
	  result = ((Number) output).doubleValue();
	else if (output instanceof NDArray)
	  result = (double) Array.get(((NDArray) output).getData(), 0);
      }
      else {
	throw new IllegalStateException("Prediction was not stored as variable '" + (m_ClassifyVarPrefix.isEmpty() ? "" : VAR_PREFIX) + "pred_y'!");
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Runs the classifier from the command-line with the specified options.
   *
   * @param args	the options for the classifier
   * @throws Exception	if execution fails
   */
  public static void main(String[] args) throws Exception {
    runClassifier(new JepRegressor(), args);
  }
}
