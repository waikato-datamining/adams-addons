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
 * DJLRegressor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.data.djl.idgenerator.FixedID;
import adams.data.djl.idgenerator.IDGenerator;
import adams.data.djl.networkgenerator.NetworkGenerator;
import adams.data.djl.networkgenerator.TabularRegressionGenerator;
import adams.data.djl.outputdirgenerator.FixedDir;
import adams.data.djl.outputdirgenerator.OutputDirGenerator;
import adams.flow.core.Actor;
import ai.djl.Model;
import ai.djl.basicdataset.tabular.ListFeatures;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.TabNetRegressionLoss;
import ai.djl.translate.Translator;
import weka.classifiers.ScriptedClassifier;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.djl.InstancesDataset;

import java.io.Closeable;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses Deep Java Library for building a regression model.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DJLRegressor
  extends AbstractSimpleClassifier
  implements ScriptedClassifier, AutoCloseable {

  private static final long serialVersionUID = -8361229968357782660L;

  /** for keeping track of models. */
  protected static Map<String,Model> m_Models = new HashMap<>();

  /** the network generator to use. */
  protected NetworkGenerator m_Network;

  /** the percentage for the network's training set. */
  protected int m_TrainPercentage;

  /** the batchsize. */
  protected int m_MiniBatchSize;

  /** the number of epochs to train. */
  protected int m_NumEpochs;

  /** the model ID/prefix generator. */
  protected IDGenerator m_ID;

  /** the output dir generator. */
  protected OutputDirGenerator m_OutputDir;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /** the header. */
  protected Instances m_Header;

  /** the DJL dataset. */
  protected transient InstancesDataset m_Dataset;

  /** the feature translator to use. */
  protected transient Translator<ListFeatures, Float> m_Translator;

  /** the model. */
  protected transient Model m_Model;

  /** the predictor to use. */
  protected transient Predictor<ListFeatures, Float> m_Predictor;

  /** the dataset config. */
  protected String m_DatasetConfig;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses Deep Java Library for building a regression model.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "network", "network",
      new TabularRegressionGenerator());

    m_OptionManager.add(
      "train-percentage", "trainPercentage",
      80, 1, 99);

    m_OptionManager.add(
      "mini-batch-size", "miniBatchSize",
      32, 1, null);

    m_OptionManager.add(
      "num-epochs", "numEpochs",
      20, 1, null);

    m_OptionManager.add(
      "id", "ID",
      new FixedID());

    m_OptionManager.add(
      "output-dir", "outputDir",
      new FixedDir());
  }

  /**
   * Sets the network generator to use.
   *
   * @param value 	the generator
   */
  public void setNetwork(NetworkGenerator value) {
    m_Network = value;
    reset();
  }

  /**
   * Gets the network generator to use.
   *
   * @return 		the generator
   */
  public NetworkGenerator getNetwork() {
    return m_Network;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String networkTipText() {
    return "The generator to create the network structure with.";
  }

  /**
   * Sets the percentage to use for the internal training set.
   *
   * @param value 	the percentage
   */
  public void setTrainPercentage(int value) {
    if (getOptionManager().isValid("trainPercentage", value)) {
      m_TrainPercentage = value;
      reset();
    }
  }

  /**
   * Gets the percentage to use for the internal training set.
   *
   * @return 		the percentage
   */
  public int getTrainPercentage() {
    return m_TrainPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String trainPercentageTipText() {
    return "The percentage to use for splitting the data into internal train/validation sets.";
  }

  /**
   * Sets the batch size to use.
   *
   * @param value 	the batch size
   */
  public void setMiniBatchSize(int value) {
    if (getOptionManager().isValid("miniBatchSize", value)) {
      m_MiniBatchSize = value;
      reset();
    }
  }

  /**
   * Gets the batch size to use.
   *
   * @return 		the batch size
   */
  public int getMiniBatchSize() {
    return m_MiniBatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String miniBatchSizeTipText() {
    return "The batch size to use.";
  }

  /**
   * Sets the number of epochs to train for.
   *
   * @param value 	the epochs
   */
  public void setNumEpochs(int value) {
    if (getOptionManager().isValid("numEpochs", value)) {
      m_NumEpochs = value;
      reset();
    }
  }

  /**
   * Gets the number of epochs to train for.
   *
   * @return 		the epochs
   */
  public int getNumEpochs() {
    return m_NumEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numEpochsTipText() {
    return "The number of epochs to train for.";
  }

  /**
   * Sets the ID/prefix generator for saving the model.
   *
   * @param value 	the ID/prefix
   */
  public void setID(IDGenerator value) {
    m_ID = value;
    reset();
  }

  /**
   * Gets the ID/prefix generator for saving the model.
   *
   * @return 		the ID/prefix
   */
  public IDGenerator getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The ID/prefix generator to use for saving the model.";
  }

  /**
   * Sets the output directory generator to use.
   *
   * @param value 	the generator
   */
  public void setOutputDir(OutputDirGenerator value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Gets the output directory generator to use.
   *
   * @return 		the generator
   */
  public OutputDirGenerator getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputDirTipText() {
    return "The generator to use for generating the output directory.";
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
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been
   *                   generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    Dataset[] 				splitDataset;
    Dataset 				trainDataset;
    Dataset 				validateDataset;
    TrainingConfig 			trainingConfig;
    ZooModel<ListFeatures, Float> 	zooModel;
    String 				modelID;
    File				modelDir;
    Path 				modelPath;
    String				modelName;

    getCapabilities().test(data);
    m_Network.setFlowContext(m_FlowContext);
    m_OutputDir.setFlowContext(m_FlowContext);
    m_ID.setFlowContext(m_FlowContext);

    modelID   = m_ID.generate();
    modelDir  = m_OutputDir.generate().getAbsoluteFile();
    modelPath = Path.of(modelDir.getAbsolutePath());
    modelName = modelDir + "|" + modelID;

    // delete any left-over .params files
    for (File f: modelDir.listFiles()) {
      if (f.getName().matches("^" + modelID + "-[0-9]+.params$")) {
	if (isLoggingEnabled())
	  getLogger().info("Removing: " + f);
	try {
	  if (!f.delete())
	    System.err.println("Failed to delete: " + f);
	}
	catch (Exception e) {
	  System.err.println("Failed to delete: " + f);
	  e.printStackTrace();
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().info("Training model: " + modelID);

    m_Dataset = InstancesDataset.builder()
		.setSampling(m_MiniBatchSize, true)
		.data(data)
		.addAllFeatures()
		.build();

    m_DatasetConfig = m_Dataset.toJson().toString();
    splitDataset    = m_Dataset.randomSplit(m_TrainPercentage, 100 - m_TrainPercentage);
    trainDataset    = splitDataset[0];
    validateDataset = splitDataset[1];


    synchronized (m_Models) {
      if (m_Models.containsKey(modelName)) {
	m_Models.get(modelName).close();
	m_Models.remove(modelName);
      }
      m_Model = Model.newInstance(modelName);
      m_Model.setBlock(m_Network.generate(m_Dataset));
      m_Models.put(modelName, m_Model);
    }

    trainingConfig = new DefaultTrainingConfig(
      new TabNetRegressionLoss())
	.addTrainingListeners(TrainingListener.Defaults.basic());

    try (Trainer trainer = m_Model.newTrainer(trainingConfig)) {
      trainer.initialize(new Shape(1, m_Dataset.getFeatureSize()));
      EasyTrain.fit(trainer, m_NumEpochs, trainDataset, validateDataset);
    }

    m_Translator = m_Dataset.matchingTranslatorOptions().option(ListFeatures.class, Float.class);
    m_Header     = new Instances(data, 0);

    if (isLoggingEnabled())
      getLogger().info("Saving model '" + modelID + "' to: " + modelPath);
    zooModel = new ZooModel<>(m_Model, m_Translator);
    zooModel.save(modelPath, modelID);
  }

  /**
   * Prepares the classifier for predictions.
   *
   * @param context	the context to use
   */
  public void initPrediction(Actor context) {
    String 	modelID;
    Path 	modelPath;

    m_FlowContext = context;
    m_Network.setFlowContext(m_FlowContext);
    m_OutputDir.setFlowContext(m_FlowContext);
    m_ID.setFlowContext(m_FlowContext);

    modelID   = m_ID.generate();
    modelPath = Path.of(m_OutputDir.generate().getAbsolutePath());

    if (m_Model == null) {
      if (isLoggingEnabled())
	getLogger().info("Loading model '" + modelID + "' from: " + modelPath);
      try {
	m_Dataset = InstancesDataset.builder()
		      .setSampling(m_MiniBatchSize, true)
		      .data(m_Header)
		      .fromJson(m_DatasetConfig)
		      .build();
	m_Translator = m_Dataset.matchingTranslatorOptions().option(ListFeatures.class, Float.class);
	m_Model = Model.newInstance(modelID);
	m_Model.setBlock(m_Network.generate(m_Dataset));
	m_Model.load(modelPath);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to recreate DJL dataset from config!", e);
      }
    }

    if (m_Predictor == null) {
      if (isLoggingEnabled())
	getLogger().info("Instantiating predictor for model: " + modelID);
      m_Predictor = m_Model.newPredictor(m_Translator);
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
    ListFeatures 	input;
    int			i;
    int			index;
    Float 		pred;

    initPrediction(m_FlowContext);

    input = new ListFeatures();
    for (i = 0; i < m_Dataset.getFeatureSize(); i++) {
      index = instance.dataset().attribute(m_Dataset.getFeatures().get(i).getName()).index();
      if (instance.attribute(index).isNumeric())
	input.add("" + instance.value(index));
      else
	input.add(instance.stringValue(index));
    }
    pred = m_Predictor.predict(input);
    return pred.doubleValue();
  }

  /**
   * Returns a short description of the setup.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("Network generator...: ").append(Utils.toCommandLine(getNetwork())).append("\n");
    result.append("Train %.............: ").append(getTrainPercentage()).append("\n");
    result.append("Mini batch size.....: ").append(getMiniBatchSize()).append("\n");
    result.append("# epochs............: ").append(getNumEpochs()).append("\n");
    result.append("ID generator........: ").append(Utils.toCommandLine(getID())).append("\n");
    result.append("Output dir generator: ").append(Utils.toCommandLine(getOutputDir())).append("\n");

    return result.toString();
  }

  /**
   * Runs the classifier from the command-line with the specified options.
   *
   * @param args	the options for the classifier
   * @throws Exception	if execution fails
   */
  public static void main(String[] args) throws Exception {
    runClassifier(new DJLRegressor(), args);
  }

  /**
   * Closes this resource, relinquishing any underlying resources.
   * This method is invoked automatically on objects managed by the
   * {@code try}-with-resources statement.
   *
   * <p>While this interface method is declared to throw {@code
   * Exception}, implementers are <em>strongly</em> encouraged to
   * declare concrete implementations of the {@code close} method to
   * throw more specific exceptions, or to throw no exception at all
   * if the close operation cannot fail.
   *
   * <p> Cases where the close operation may fail require careful
   * attention by implementers. It is strongly advised to relinquish
   * the underlying resources and to internally <em>mark</em> the
   * resource as closed, prior to throwing the exception. The {@code
   * close} method is unlikely to be invoked more than once and so
   * this ensures that the resources are released in a timely manner.
   * Furthermore it reduces problems that could arise when the resource
   * wraps, or is wrapped, by another resource.
   *
   * <p><em>Implementers of this interface are also strongly advised
   * to not have the {@code close} method throw {@link
   * InterruptedException}.</em>
   * <p>
   * This exception interacts with a thread's interrupted status,
   * and runtime misbehavior is likely to occur if an {@code
   * InterruptedException} is {@linkplain Throwable#addSuppressed
   * suppressed}.
   * <p>
   * More generally, if it would cause problems for an
   * exception to be suppressed, the {@code AutoCloseable.close}
   * method should not throw it.
   *
   * <p>Note that unlike the {@link Closeable#close close}
   * method of {@link Closeable}, this {@code close} method
   * is <em>not</em> required to be idempotent.  In other words,
   * calling this {@code close} method more than once may have some
   * visible side effect, unlike {@code Closeable.close} which is
   * required to have no effect if called more than once.
   * <p>
   * However, implementers of this interface are strongly encouraged
   * to make their {@code close} methods idempotent.
   *
   * @throws Exception if this resource cannot be closed
   */
  @Override
  public void close() throws Exception {
    if (m_Model != null) {
      try {
	m_Model.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Model = null;
    }
  }
}
